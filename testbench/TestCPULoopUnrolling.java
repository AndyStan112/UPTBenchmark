package testbench;

import benchmark.IBenchmark;
import benchmark.cpu.CPURecursionLoopUnrolling;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestCPULoopUnrolling {
    public static void main(String[] args) {
        CPURecursionLoopUnrolling bench = new CPURecursionLoopUnrolling();
        int[] unrollLevels = {-1, 1, 5, 15};
        int size = 2_000_000;
        int repetitions = 3;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("loop_unrolling_scores.csv"))) {
            writer.write("Device,UnrollLevel,AverageTimeMillis,AverageScore\n");

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

                writer.write(deviceName + "," + (level == -1 ? 0 : level) + "," + avgTime + "," + avgScore + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
