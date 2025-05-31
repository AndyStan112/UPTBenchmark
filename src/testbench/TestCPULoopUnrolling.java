package testbench;

import benchmark.cpu.CPURecursionLoopUnrolling;
import logging.CSVLogger;

import java.io.IOException;

public class TestCPULoopUnrolling {
    public static void main(String[] args) {
        CPURecursionLoopUnrolling bench = new CPURecursionLoopUnrolling();
        int[] unrollLevels = {-1, 1, 3, 5, 7, 10, 12, 15};
        int size = 2_000_000;
        int repetitions = 3;

        String csvFile = "static/loop_unrolling_scores.csv";
        String[] header = {"Device", "UnrollLevel", "AverageTimeMillis", "AverageScore"};

        try (CSVLogger logger = new CSVLogger(csvFile, header)) {
            String deviceName = java.net.InetAddress.getLocalHost().getHostName();

            for (int level : unrollLevels) {
                double totalTime = 0;
                double totalScore = 0;

                for (int i = 0; i < repetitions; i++) {
                    bench.init(size);
                    bench.warmup();

                    if (level == -1) {
                        bench.run(false);
                    } else {
                        bench.run(true, level);
                    }

                    totalTime += bench.getLastRuntime();
                    totalScore += bench.computeScore();

                    bench.clean();
                }

                double avgTime = totalTime / repetitions;
                double avgScore = totalScore / repetitions;

                logger.writeRow(
                        deviceName,
                        String.valueOf(level == -1 ? 0 : level),
                        String.valueOf(avgTime),
                        String.valueOf(avgScore)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
