package testbench;

import benchmark.cpu.CPUFixedVsFloatingPoint;
import benchmark.cpu.NumberRepresentation;
import logging.CSVLogger;
import timing.ITimer;
import timing.Timer;

import java.io.IOException;
import java.net.InetAddress;

public class TestCPUFixedVsFloatingPoint {
    public static void main(String[] args) throws IOException {
        ITimer timer = new Timer();
        String device = "Unknown";
        try {
            device = InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {}

        String path = "static/fixed_vs_float.csv";
        String[] header = {
                "Device", "Mode", "Instructions", "TimeMS"
        };
        CSVLogger logger = new CSVLogger(path, header);

        long[] sizes = {
                500_000L,
                1_000_000L,
                5_000_000L,
                10_000_000L,
                50_000_000L,
                100_000_000L
        };

        for (long size : sizes) {
            for (NumberRepresentation mode : NumberRepresentation.values()) {
                CPUFixedVsFloatingPoint bench = new CPUFixedVsFloatingPoint();
                bench.init((int) size);
                bench.warmup();

                timer.start();
                bench.run(mode);
                long timeMs = timer.stop() / 1_000_000;

                logger.writeRow(
                        device,
                        mode.name(),
                        String.valueOf(size),
                        String.valueOf(timeMs)
                );

                bench.clean();
            }
        }

        logger.close();
    }
}
