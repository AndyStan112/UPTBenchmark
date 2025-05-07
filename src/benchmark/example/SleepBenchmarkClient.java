package benchmark.example;

import benchmark.IBenchmark;
import logging.ConsoleLogger;
import logging.ILogger;
import timing.ITimer;
import timing.Timer;

import java.util.concurrent.TimeUnit;

public class SleepBenchmarkClient {
    public static void main(String[] args) {
        ITimer timer = new Timer();
        ILogger log = new ConsoleLogger();
        IBenchmark bench = new SleepBenchmark();

        int loops = 10;
        int sleepMs = 100;

        bench.init(sleepMs);
        timer.start();

        for (int i = 0; i < loops; i++) {
            timer.resume();
            bench.run();
            long segmentTime = timer.pause();
            log.writeTime("Pause " + (i + 1), segmentTime, TimeUnit.NANOSECONDS);
        }

        long totalTime = timer.stop();
        log.writeTime("Total", totalTime, TimeUnit.NANOSECONDS);

        log.close();
        bench.clean();
    }
}
