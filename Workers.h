#include <functional>
#include <iostream>
#include <list>
#include <mutex>
#include <thread>
#include <vector>
#include <condition_variable>
#include <future>

using namespace std;

class Workers {
public:
    Workers(int numThreads) : numThreads(numThreads), stopRequested(false) {}

    void start() {
        for (int i = 0; i < numThreads; ++i) {
            threads.emplace_back([this] {
                workerThread();
            });
        }
    }

    void post(function<void()> task) {
        {
            unique_lock<mutex> lock(tasksMutex);
            tasks.emplace_back(move(task));
        }
        taskAvailable.notify_one();
    }

    template <typename Duration>
    void post_timeout(std::function<void()> task, Duration timeout) {
        std::unique_lock<std::mutex> lock(tasksMutex);

        bool timeoutCompleted = false;

        // Launch the timeout task in a separate thread
        std::thread([this, task = std::move(task), timeout, &timeoutCompleted]() mutable {
            std::this_thread::sleep_for(timeout);

            {
                std::unique_lock<std::mutex> lock(tasksMutex);
                tasks.emplace_back([task = std::move(task), &timeoutCompleted]() mutable {
                    task();
                    timeoutCompleted = true;
                });
                lock.unlock(); // Unlock before notifying to minimize lock contention
                taskAvailable.notify_one();
            }
        }).detach();

        // Wait for the timeout task to complete
        timeoutCondition.wait(lock, [&timeoutCompleted] { return timeoutCompleted; });
    }

    void stop() {
        {
            std::unique_lock<std::mutex> lock(tasksMutex);
            stopRequested = true;
        }
        taskAvailable.notify_all();  // Notify all waiting threads to stop
        for (auto &thread : threads) {
            if (thread.joinable()) {
                thread.join();
            }
        }
    }

private:
    int numThreads;
    vector<thread> threads;
    list<function<void()>> tasks;
    mutex tasksMutex;
    condition_variable taskAvailable;
    bool stopRequested;
    std::condition_variable timeoutCondition;

    void workerThread() {
        while (true) {
            std::function<void()> task;
            {
                std::unique_lock<std::mutex> lock(tasksMutex);
                taskAvailable.wait(lock, [this] { return stopRequested || !tasks.empty(); });

                if (stopRequested && tasks.empty()) {
                    return;  // Exit the thread when stop is requested and the task list is empty
                }

                task = std::move(tasks.front());
                tasks.pop_front();
            }

            if (task) {
                task();  // Run task outside of mutex lock
            }
        }
    }
};
