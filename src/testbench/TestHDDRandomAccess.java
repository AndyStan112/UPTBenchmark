package testbench;

import benchmark.hdd.HDDRandomAccess;
import logging.CSVLogger;

import java.io.IOException;
import java.net.InetAddress;

public class TestHDDRandomAccess {
    public static void main(String[] args) {
        HDDRandomAccess bench = new HDDRandomAccess();
        long fileSize = 20L * 1024 * 1024 * 1024; // 20 GB since both of us have 16GB RAM
        int[] bufferSizes = {512, 1024, 2048, 4096, 8192, 16384, 32768, 65536};
        String[] modes = {"r", "w"};
        String[] types = {"fs", "ft"};

        String device = "Unknown";
        try {
            device = InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {}

        String[] header = {"Device", "TestMode", "BufferSizeKB", "WriteSpeedMBps"};
        String csvFile = "static/hdd_random_access.csv";

        try (CSVLogger logger = new CSVLogger(csvFile, header)) {
            for (String mode : modes) {
                for (String type : types) {
                    for (int buffer : bufferSizes) {
                        bench.init(fileSize);
                        bench.run(new Object[]{mode, type, buffer});
                        String result = bench.getResult();

                        double speedMBps = parseSpeed(result);
                        logger.writeRow(
                                device,
                                mode + "-" + type,
                                String.valueOf(buffer),
                                String.format("%.2f", speedMBps)
                        );
                        System.out.println(result);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static double parseSpeed(String result) {
        try {
            String[] parts = result.split("MB/s");
            String[] tokens = parts[0].split(" ");
            return Double.parseDouble(tokens[tokens.length - 1]);
        } catch (Exception e) {
            return 0.0;
        }
    }
}
