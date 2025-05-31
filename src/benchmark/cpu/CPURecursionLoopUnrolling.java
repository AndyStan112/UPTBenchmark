package benchmark.cpu;

import benchmark.IBenchmark;

public class CPURecursionLoopUnrolling implements IBenchmark {
    private int size = 2_000_000;
    private boolean cancelled;
    private int lastCounter;
    private long lastReached;
    private double lastRuntime;

    public long getLastReached() {
        return lastReached;
    }

    public int getLastCounter() {
        return lastCounter;
    }

    public double getLastRuntime() {
        return lastRuntime;
    }

    @Override
    public void run() {
        run(false);
    }

    @Override
    public void run(Object... params) {
        boolean useUnrolling = (boolean) params[0];
        int unrollLevel = params.length > 1 ? (int) params[1] : 0;

        long startTime = System.nanoTime();

        if (!useUnrolling) {
            recursive(1, size, 0);
        } else {
            recursiveUnrolled(1, unrollLevel, size, 0);
        }

        long endTime = System.nanoTime();
        lastRuntime = (endTime - startTime) / 1_000_000.0;
    }

    private long recursive(long start, long size, int counter) {
        if (start > size || cancelled) return 0;

        try {
            long sum = 0;
            if (isPrime(start)) sum += start;
            return sum + recursive(start + 1, size, counter + 1);
        } catch (StackOverflowError e) {
            lastReached = start;
            lastCounter = counter;
            return 0;
        }
    }

    private long recursiveUnrolled(long start, int unrollLevel, int size, int counter) {
        if (start > size || cancelled) return 0;

        try {
            long sum = 0;
            for (int i = 0; i < unrollLevel && start <= size; i++, start++) {
                if (isPrime(start)) sum += start;
            }
            return sum + recursiveUnrolled(start, unrollLevel, size, counter + 1);
        } catch (StackOverflowError e) {
            lastReached = start;
            lastCounter = counter;
            return 0;
        }
    }

    private boolean isPrime(long x) {
        if (x <= 2) return true;
        if (x % 2 == 0) return false;
        for (long i = 3; i <= Math.sqrt(x); i += 2) {
            if (x % i == 0) return false;
        }
        return true;
    }

    @Override
    public void init(Object... params) {
        if (params.length == 1 && params[0] instanceof Integer) {
            size = (Integer) params[0];
        }
        cancelled = false;
        lastReached = 0;
        lastCounter = 0;
        lastRuntime = 0;
    }

    @Override
    public void clean() {
        size = 2_000_000;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public void warmup() {
        recursive(1, 10_000, 0);
    }

    public double computeScore() {
        return size / (Math.log(lastRuntime + 1) / Math.log(2) * Math.sqrt(lastCounter + 1) + 1);
    }
}
