package benchmark.ram;

import benchmark.IBenchmark;
import logging.CSVLogger;
import timing.Timer;

import java.io.IOException;
import java.util.Random;

public class VirtualMemoryBenchmark implements IBenchmark {
    private MemoryMapper core;
    private String result;

    @Override
    public void init(Object... params) {}

    @Override
    public void warmup() {}

    @Override
    public void cancel() {}

    @Override
    public void run() {
        throw new UnsupportedOperationException("Use run(Object...) instead");
    }

    @Override
    public void run(Object... options) {
        Object[] params = (Object[]) options;
        long fileSize = Long.parseLong(params[0].toString());
        int bufferSize = Integer.parseInt(params[1].toString());
        String device = params[2].toString();
        String mode = params[3].toString();
        CSVLogger logger = (CSVLogger) params[4];

        byte[] buffer = new byte[bufferSize];
        Random rand = new Random();


        try {
            core = new MemoryMapper("./vmcore", fileSize);

            Timer timer = new Timer();

            timer.start();
            for (long i = 0; i < fileSize; i += bufferSize) {
                timer.pause();
                rand.nextBytes(buffer);
                timer.resume();
                core.put(i, buffer);
            }
            double writeTime = timer.stop() / 1_000_000_000.0;
            double writeSpeed = fileSize / 1024.0 / 1024.0 / writeTime;

            timer.start();
            for (long i = 0; i < fileSize; i += bufferSize) {
                buffer = core.get(i, bufferSize);
            }
            double readTime = timer.stop() / 1_000_000_000.0;

            double readSpeed = fileSize / 1024.0 / 1024.0 / readTime;

            result = String.format("Write speed: %.4f MB/s, Read speed: %.4f MB/s", writeSpeed, readSpeed);

            logger.writeRow(
                    device,
                    mode,
                    String.valueOf(fileSize / 1024 / 1024),
                    String.format("%.4f", writeSpeed),
                    String.format("%.4f", readSpeed)

            );

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (core != null) core.purge();
        }
    }


    @Override
    public void clean() {
        if (core != null) core.purge();
    }

    public String getResult() {
        return result;
    }
}
