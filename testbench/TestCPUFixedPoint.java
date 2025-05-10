package testbench;

import logging.ConsoleLogger;
import logging.ILogger;
import timing.ITimer;
import timing.Timer;
import benchmark.cpu.CPUFixedPoint;
import java.util.concurrent.TimeUnit;

public class TestCPUFixedPoint {
    
    private static final int opsArithmetic = 29;   
    private static final int opsBranching = 9;   
    private static final int opsArray = 3;   

    public static void main(String[] args) {
        int size = 10000000;
        ILogger log = new ConsoleLogger();
        ITimer timer = new Timer();


        CPUFixedPoint bench = new CPUFixedPoint();
        bench.init(size);
        bench.warmup();


        timer.start();
        bench.run();
        long t = timer.stop();

 
        long totalOps = (long) size * (opsArithmetic + opsBranching + opsArray);
        double mops = totalOps * 1000.0 / t; 

        log.writeTime("Time", t, TimeUnit.MILLISECONDS);
        log.write(String.format("Estimated MOPS: %.2f", mops));

        bench.clean();
        log.close();
    }
}
