package testbench;

import benchmark.ram.VirtualMemoryBenchmark;
import logging.CSVLogger;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestVirtualMemory {
    static class Sample {
        String timestamp;
        double cpuPercent;
        double ramPercent;
        double diskPercent;
        String mode;
        String device;

        Sample(String ts, double cpu, double ram, double disk, String m, String d) {
            this.timestamp = ts;
            this.cpuPercent = cpu;
            this.ramPercent = ram;
            this.diskPercent = disk;
            this.mode = m;
            this.device = d;
        }
    }

    public static void main(String[] args) {
        long[] fileSizesMB = {1L*1024, 2L * 1024,4L *1024, 8L *1024,10L*1024,12L*1024 };

        String device = "unknown";
        try {
            device = InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) { }
        String finalDevice = device;
        String perfCsvPath = "static/ram/csv/virtual_memory_benchmark.csv";

        for (long fileSizeMB : fileSizesMB) {
            long fileSizeBytes = fileSizeMB * 1024L * 1024L;
            String mode = fileSizeMB <= 4L * 1024 ? "ram" : "swap";
            List<Sample> samples = Collections.synchronizedList(new ArrayList<>());

            AtomicBoolean running = new AtomicBoolean(true);


            Thread sampler = new Thread(() -> {
                SystemInfo si = new SystemInfo();
                HardwareAbstractionLayer hal = si.getHardware();
                CentralProcessor cpu = hal.getProcessor();
                GlobalMemory mem = hal.getMemory();
                List<HWDiskStore> disks = hal.getDiskStores();

                long[] prevCpuTicks = cpu.getSystemCpuLoadTicks();

                long[] prevDiskTransferTime = new long[disks.size()];
                for (int i = 0; i < disks.size(); i++) {
                    HWDiskStore disk = disks.get(i);
                    disk.updateAttributes();
                    prevDiskTransferTime[i] = disk.getTransferTime();
                }

                {
                    double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevCpuTicks) * 100.0;
                    prevCpuTicks = cpu.getSystemCpuLoadTicks();

                    long total = mem.getTotal();
                    long avail = mem.getAvailable();
                    double ramPerc = ((double)(total - avail) / total) * 100.0;

                    double diskPerc = 0.0;

                    String ts = Instant.now()
                            .atOffset(ZoneOffset.UTC)
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                    samples.add(new Sample(ts, cpuLoad, ramPerc, diskPerc, mode, finalDevice));
                }

                while (running.get()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        break;
                    }

                    double cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevCpuTicks) * 100.0;
                    prevCpuTicks = cpu.getSystemCpuLoadTicks();

                    long total = mem.getTotal();
                    long avail = mem.getAvailable();
                    double ramPerc = ((double)(total - avail) / total) * 100.0;

                    long sumDeltaTransferTime = 0;
                    for (int i = 0; i < disks.size(); i++) {
                        HWDiskStore disk = disks.get(i);
                        disk.updateAttributes();
                        long curr = disk.getTransferTime();
                        long delta = curr - prevDiskTransferTime[i];
                        sumDeltaTransferTime += delta;
                        prevDiskTransferTime[i] = curr;
                    }
                    double diskPerc = 0.0;
                    if (!disks.isEmpty()) {
                        diskPerc = ((double)sumDeltaTransferTime / (1000.0 * disks.size())) * 100.0;
                        diskPerc = Math.min(100.0, Math.max(0.0, diskPerc));
                    }

                    String ts = Instant.now()
                            .atOffset(ZoneOffset.UTC)
                            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

                    samples.add(new Sample(ts, cpuLoad, ramPerc, diskPerc, mode, finalDevice));
                }
            });

            sampler.start();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) { }

            try (CSVLogger logger = new CSVLogger(perfCsvPath, new String[]{
                    "Device", "Mode", "FileSizeMB",
                    "WriteSpeedMBps", "ReadSpeedMBps",
            })) {
                VirtualMemoryBenchmark bench = new VirtualMemoryBenchmark();
                bench.run(new Object[]{
                        fileSizeBytes,
                        64 * 1024,
                        device,
                        mode,
                        logger
                });
                System.out.printf(
                        "Completed %s, %d MB → %s%n",
                        mode, fileSizeMB, bench.getResult()
                );
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) { }

            running.set(false);
            try {
                sampler.join();
            } catch (InterruptedException ignored) { }


            String outCsv = String.format("static/ram/csv/virtual_memory_usage_%s_%d.csv", mode, fileSizeMB);
            try (CSVLogger logger = new CSVLogger(outCsv, new String[]{
                    "Timestamp", "CPU_Percent", "RAM_Used_Percent", "Disk_Util_Percent", "Mode", "Device"
            })) {
                synchronized (samples) {
                    for (Sample s : samples) {
                        logger.writeRow(s.timestamp,
                                String.format("%.2f", s.cpuPercent),
                                String.format("%.2f", s.ramPercent),
                                String.format("%.2f", s.diskPercent),
                                s.mode, s.device);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All runs complete.");
    }
}
