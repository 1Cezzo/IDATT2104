#include <iostream>
#include <vector>
#include <thread>
#include <mutex>

using namespace std;

bool isPrime(int number) {
    if (number < 2) {
        return false;
    } else if (number == 2) {
        return true;
    }

    int divisor = 2;
    while (divisor * divisor <= number) {
        if (number % divisor == 0) {
            return false;
        }
        divisor += 1;
    }

    return true;
}

void findPrimesInRange(int start, int end, vector<int>& result, mutex& resultMutex) {
    vector<int> localResult;

    for (int i = start; i <= end; ++i) {
        if (isPrime(i)) {
            localResult.push_back(i);
        }
    }

    lock_guard<mutex> lock(resultMutex);
    result.insert(result.end(), localResult.begin(), localResult.end());
}

bool checkOrder(const vector<int>& result) {
    for (int i = 1; i <= result.size(); i++) {
        if (result[i-1] > result[i]) {
            return false;
        }
    }
    return true;
}

void insertionSort(vector<int>& result) {
    int n = result.size();

    for (int i = 1; i < n; ++i) {
        int key = result[i];
        int j = i - 1;

        while (j >= 0 && result[j] > key) {
            result[j + 1] = result[j];
            j = j - 1;
        }

        result[j + 1] = key;
    }
}

int main() {
    const int lowerLimit = 25;
    const int upperLimit = 145;
    const int numThreads = 6;

    vector<thread> threads;
    vector<int> result;
    mutex resultMutex;

    int rangeSize = (upperLimit - lowerLimit + 1) / numThreads;

    for (int i = 0; i < numThreads; ++i) {
        int start = i * rangeSize + lowerLimit;
        int end = (i == numThreads - 1) ? upperLimit : (i + 1) * rangeSize - 1 + lowerLimit;

        threads.emplace_back(findPrimesInRange, start, end, ref(result), ref(resultMutex));
    }

    for (auto& thread : threads) {
        thread.join();
    }

    if (!checkOrder(result)) {
        insertionSort(result);
    }

    cout << "Prime numbers between " << lowerLimit << " and " << upperLimit << ": ";
    for (int prime : result) {
        cout << prime << " ";
    }
    cout << endl;

    return 0;
}
