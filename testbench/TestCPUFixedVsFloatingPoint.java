package testbench;

import logging.ConsoleLogger;
import logging.ILogger;
import java.util.concurrent.TimeUnit;
import timing.ITimer;
import timing.Timer;
import benchmark.cpu.CPUFixedVsFloatingPoint;
import benchmark.cpu.NumberRepresentation;

public class TestCPUFixedVsFloatingPoint {

    public static void main(String[] args) {
        ITimer timer = new Timer();
        ILogger log = new ConsoleLogger();
        TimeUnit unit = TimeUnit.MILLISECONDS;


        CPUFixedVsFloatingPoint bench = new CPUFixedVsFloatingPoint();
        bench.init(10_000_000);
        bench.warmup();

        timer.start();
      //  bench.run(NumberRepresentation.FIXED);
      bench.run(NumberRepresentation.FLOATING);
        long t = timer.stop();

        log.writeTime("Time ", t, unit);
        log.write("Result is", bench.getResult());

        bench.clean();
        log.close();
    }
}
