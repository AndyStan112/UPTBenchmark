package testbench;

import benchmark.hdd.FileWriter;
import logging.CSVLogger;
import logging.ConsoleLogger;

import java.io.File;
import java.io.IOException;

public class TestHDDWriteSpeed {
	private static String getBenchmarkBasePath() {
		String userHome = System.getProperty("user.home");
		String sep = File.separator;
		return userHome + sep + "Documents" + sep + "temp" + sep + "bench" + sep;
	}

	public static void main(String[] args) {
		String csvPath = "static/hdd_write_speed.csv";
		String outputPrefix = getBenchmarkBasePath() + "write-";
		new File(outputPrefix).getParentFile().mkdirs();

		String[] header = {
			"Device", "TestMode", "FileName", "FileSizeMB",
			"BufferSizeKB", "WriteSpeedMBps", "Timestamp"
		};

		try (CSVLogger csvLogger = new CSVLogger(csvPath, header)) {
			ConsoleLogger logger = new ConsoleLogger();
			FileWriter writer = new FileWriter(logger);
			writer.setCSVLogger(csvLogger);

			writer.setTestMode("fs");
			writer.streamWriteFixedFileSize(outputPrefix, ".dat", 0, 8,
					1024L * 1024 * 256, true);

			writer.setTestMode("fb");
			writer.streamWriteFixedBufferSize(outputPrefix, ".dat", 0, 8,
					1024 * 4, true);

			boolean clean = true;

			if (clean) {
				writer.cleanOutputDirectory();
			}


		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
