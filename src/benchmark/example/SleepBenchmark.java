package benchmark.example;

import benchmark.IBenchmark;

public class SleepBenchmark implements IBenchmark {
    private long sleepDurationMs;
    private boolean cancelled;

    @Override
    public void init(Object... params) {
        if (params.length != 1 || !(params[0] instanceof Integer)) {
            throw new IllegalArgumentException("Expected one Integer parameter for sleep duration in milliseconds.");
        }
        sleepDurationMs = (Integer) params[0];
        cancelled = false;
    }

    @Override
    public void run() {
        run((Object[]) null);
    }

    @Override
    public void run(Object... params) {
        try {
            if (!cancelled) Thread.sleep(sleepDurationMs);
        } catch (InterruptedException _) {

        }
    }

    @Override
    public void clean() {

    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public void warmup() {

    }
}
