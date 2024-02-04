#include <iostream>
#include <functional>
#include "Workers.h"

int main() {
    // Create instances of Workers
    Workers worker_threads(4);
    Workers event_loop(1);

    // Start the worker threads
    worker_threads.start();
    event_loop.start();

    // Post tasks to worker threads
    worker_threads.post([] {
        std::cout << "Task A runs in thread " << std::this_thread::get_id() << std::endl;
    });
    worker_threads.post([] {
        std::cout << "Task AA runs in thread " << std::this_thread::get_id() << std::endl;
    });

    worker_threads.post_timeout([] {
        std::cout << "Task after timeout runs in thread " << std::this_thread::get_id() << std::endl;
    }, std::chrono::seconds(10));

    worker_threads.post([] {
        std::cout << "Task B runs in thread " << std::this_thread::get_id() << std::endl;
    });

    // Post tasks to the event loop
    event_loop.post([] {
        std::cout << "Task C runs in thread " << std::this_thread::get_id() << std::endl;
    });

    event_loop.post([] {
        std::cout << "Task D runs in thread " << std::this_thread::get_id() << std::endl;
    });

    // Join the worker threads
    worker_threads.stop();
    event_loop.stop();

    return 0;
}
