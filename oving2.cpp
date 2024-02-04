#include <iostream>
#include <functional>
#include "Workers.h"

int main() {
    Workers worker_threads(4);
    Workers event_loop(1);

    worker_threads.start();
    event_loop.start();

    worker_threads.post([] {
        std::cout << "Task A runs in thread " << std::this_thread::get_id() << std::endl;
    });
    
    worker_threads.post([] {
        std::cout << "Task B runs in thread " << std::this_thread::get_id() << std::endl;
    });

    worker_threads.post_timeout([] {
        std::cout << "Task C after a 10 second timeout runs in thread " << std::this_thread::get_id() << std::endl;
    }, std::chrono::seconds(10));

    worker_threads.post([] {
        std::cout << "Task D runs in thread " << std::this_thread::get_id() << std::endl;
    });

    worker_threads.post_timeout([] {
        std::cout << "Task E after a 5 second timeout runs in thread " << std::this_thread::get_id() << std::endl;
    }, std::chrono::seconds(5));

    event_loop.post([] {
        std::cout << "Task F runs in thread " << std::this_thread::get_id() << std::endl;
    });

    event_loop.post([] {
        std::cout << "Task G runs in thread " << std::this_thread::get_id() << std::endl;
    });

    worker_threads.stop();
    event_loop.stop();

    return 0;
}
