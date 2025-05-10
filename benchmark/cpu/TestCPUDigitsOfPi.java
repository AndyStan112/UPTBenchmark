package benchmark.cpu;

import benchmark.IBenchmark;
import timing.ITimer;
import timing.Timer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestCPUDigitsOfPi {
    public static void main(String[] args) {
        ITimer timer = new Timer();
        IBenchmark bench = new CPUDigitsOfPi();
        int[] digitCounts = {50, 100, 200, 500, 1000, 2000, 5000, 10000};
        int repetitions = 3;
        String[] algos = {"GaussLegendre", "Chudnovsky"};

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("static/pi_benchmark_results.csv"))) {
            writer.write("Digits,AverageTimeMillis,Algorithm\n");

            for (int algo = 0; algo < algos.length; algo++) {
                String algoName = algos[algo];

                for (int digits : digitCounts) {
                    bench.init(digits);
                    bench.warmup();

                    long totalTime = 0;
                    for (int i = 0; i < repetitions; i++) {
                        timer.start();
                        bench.run(algo);
                        totalTime += timer.stop();
                    }

                    long avgTime = totalTime / repetitions;
                    writer.write(digits + "," + avgTime + "," + algoName + "\n");

                    bench.clean();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV output", e);
        }
    }
}
