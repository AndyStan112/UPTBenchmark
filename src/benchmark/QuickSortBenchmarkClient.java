package benchmark;

import logging.ConsoleLogger;
import logging.ILogger;
import timing.ITimer;
import timing.Timer;

import java.util.concurrent.TimeUnit;

public class QuickSortBenchmarkClient {
    public static void main(String[] args) {
        ITimer timer = new Timer();
        ILogger log = new ConsoleLogger();
        IBenchmark bench = new QuickSortBenchmark();

        bench.init(10000);
        timer.start();
        bench.run();
        long time = timer.stop();

        log.writeTime("Finished in", time, TimeUnit.NANOSECONDS);
        log.close();
        bench.clean();
    }
}
