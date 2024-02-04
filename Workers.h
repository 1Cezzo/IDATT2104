#include <iostream>
#include <list>
#include <vector>
#include <functional>
#include <mutex>
#include <condition_variable>
#include <thread>
#include <chrono>
#include <algorithm>

using namespace std;

class Workers {
public:
    Workers(int numThreads) : numThreads(numThreads), stopRequested(false) {}

    ~Workers() {
        stop();
    }

    void start() {
        for (int i = 0; i < numThreads; ++i) {
            threads.emplace_back([this] {
                workerThread();
            });
        }
    }

    void stop() {
        {
            unique_lock<mutex> lock(tasksMutex);
            stopRequested = true;
        }
        taskAvailable.notify_all();
        for (auto &thread : threads) {
            if (thread.joinable()) {
                thread.join();
            }
        }
    }

    void post(const function<void()> &task) {
        {
            unique_lock<mutex> lock(tasksMutex);
            tasks.emplace_back(task);
        }
        taskAvailable.notify_one();
    }

    template <typename Duration>
    void post_timeout(const function<void()> &task, Duration timeout) {
        auto executeTime = chrono::steady_clock::now() + timeout;

        {
            unique_lock<mutex> lock(tasksMutex);
            timedTasks.emplace_back(executeTime, task);
        }
        taskAvailable.notify_one();
    }

private:
    int numThreads;
    vector<thread> threads;
    list<function<void()>> tasks;
    list<pair<chrono::steady_clock::time_point, function<void()>>> timedTasks;
    mutex tasksMutex;
    condition_variable taskAvailable;
    bool stopRequested;

    void workerThread() {
        while (true) {
            function<void()> task;
            chrono::steady_clock::time_point executeTime;

            {
                unique_lock<mutex> lock(tasksMutex);
                taskAvailable.wait(lock, [this] {
                    return stopRequested || !tasks.empty() || !timedTasks.empty();
                });

                if (stopRequested && tasks.empty() && timedTasks.empty()) {
                    return;
                }

                if (!tasks.empty()) {
                    task = move(tasks.front());
                    tasks.pop_front();
                }

                if (!timedTasks.empty()) {
                    auto earliestTask = min_element(timedTasks.begin(), timedTasks.end(),
                        [](const auto &lhs, const auto &rhs) {
                            return lhs.first < rhs.first;
                        });

                    executeTime = earliestTask->first;

                    if (executeTime <= chrono::steady_clock::now()) {
                        task = earliestTask->second;
                        timedTasks.erase(earliestTask);
                    }
                }
            }

            if (task) {
                task();
            }
        }
    }
};
