package benchmark.hdd;

import logging.ILogger;
import logging.CSVLogger;
import timing.Timer;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

public class FileWriter {
	private static final int MIN_BUFFER_SIZE = 1024 * 1;
	private static final int MAX_BUFFER_SIZE = 1024 * 1024 * 32;
	private static final long MIN_FILE_SIZE = 1024 * 1024 * 1;
	private static final long MAX_FILE_SIZE = 1024L * 1024 * 512;

	private final Timer timer = new Timer();
	private final Random rand = new Random();
	private double benchScore;
	private final ILogger logger;
	private CSVLogger csvLogger;
	private String testMode = "unknown";
	private final String device;

	public FileWriter(ILogger logger) {
		this.logger = logger;
		this.device = getDeviceName();
	}

	public void setCSVLogger(CSVLogger csvLogger) {
		this.csvLogger = csvLogger;
	}

	public void setTestMode(String mode) {
		this.testMode = mode;
	}

	public void streamWriteFixedFileSize(String filePrefix, String fileSuffix,
										 int minIndex, int maxIndex, long fileSize, boolean clean)
			throws IOException {

		logger.write("Stream write benchmark with fixed file size");
		int currentBufferSize = MIN_BUFFER_SIZE;
		String fileName;
		int fileIndex = 0;
		benchScore = 0;

		while (currentBufferSize <= MAX_BUFFER_SIZE
				&& fileIndex <= maxIndex - minIndex) {

			fileName = filePrefix + (fileIndex + minIndex) + fileSuffix;
			writeFile(fileName, currentBufferSize, fileSize, clean);
			currentBufferSize *= 2;
			fileIndex++;
		}

		benchScore /= (maxIndex - minIndex + 1);
		String partition = filePrefix.contains(":\\")

				? filePrefix.substring(0, filePrefix.indexOf(":\\")) + ":"
				: new File(filePrefix).toPath().getRoot().toString();
		logger.write("File write score on partition", partition + ":", String.format("%.2f", benchScore), "MB/sec");
	}

	public void streamWriteFixedBufferSize(String filePrefix, String fileSuffix,
										   int minIndex, int maxIndex, int bufferSize, boolean clean)
			throws IOException {

		logger.write("Stream write benchmark with fixed buffer size");
		long currentFileSize = MIN_FILE_SIZE;
		int fileIndex = 0;
		benchScore = 0;

		while (currentFileSize <= MAX_FILE_SIZE
				&& fileIndex <= maxIndex - minIndex) {

			String fileName = filePrefix + (fileIndex + minIndex) + fileSuffix;
			writeFile(fileName, bufferSize, currentFileSize, clean);
			currentFileSize *= 2;
			fileIndex++;
		}

		benchScore /= (maxIndex - minIndex + 1);
		String partition = filePrefix.contains(":\\")

				? filePrefix.substring(0, filePrefix.indexOf(":\\")) + ":"
				: new File(filePrefix).toPath().getRoot().toString();
		logger.write("File write score on partition", partition + ":", String.format("%.2f", benchScore), "MB/sec");
	}

	private void writeFile(String fileName, int bufferSize,
						   long fileSize, boolean clean) throws IOException {

		File folderPath = new File(fileName.substring(0,
				fileName.lastIndexOf(File.separator)));
		if (!folderPath.isDirectory()) folderPath.mkdirs();

		final File file = new File(fileName);
		final BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(file), bufferSize);

		byte[] buffer = new byte[bufferSize];
		long toWrite = fileSize / bufferSize;

		timer.start();
		for (long i = 0; i < toWrite; i++) {
			rand.nextBytes(buffer);
			outputStream.write(buffer);
		}
		outputStream.flush();
		long elapsedTime = timer.stop();
		double speed = printStats(fileName, fileSize, bufferSize, elapsedTime);

		outputStream.close();
		if (clean) file.delete();

		if (csvLogger != null) {
			csvLogger.writeRow(
					device,
					testMode,
					fileName,
					String.valueOf(fileSize / (1024 * 1024)),
					String.valueOf(bufferSize / 1024),
					String.format("%.2f", speed),
					String.valueOf(System.currentTimeMillis())
			);
		}
	}

	private double printStats(String fileName, long totalBytes, int bufferSize, long elapsedTimeNs) {
		NumberFormat nf = new DecimalFormat("#.00");
		double seconds = elapsedTimeNs / 1e9;
		double megabytes = totalBytes / (1024.0 * 1024.0);
		double rate = megabytes / seconds;

		logger.write("Done writing", totalBytes, "bytes to file:",
				fileName, "in", nf.format(seconds), "s (",
				nf.format(rate), "MB/sec)", "with buffer size",
				bufferSize / 1024, "kB");

		benchScore += rate;
		return rate;
	}


	private String getDeviceName() {
		try {
			return java.net.InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			return "unknown";
		}
	}

	public void cleanOutputDirectory() {
		String basePath = getOutputBasePath();
		File dir = new File(basePath);
		if (dir.exists() && dir.isDirectory()) {
			deleteRecursively(dir);
			logger.write("Cleaned output directory:", basePath);
		}
	}

	private String getOutputBasePath() {
		String userHome = System.getProperty("user.home");
		String sep = File.separator;
		return userHome + sep + "Documents" + sep + "temp" + sep + "bench";
	}

	private void deleteRecursively(File file) {
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				deleteRecursively(sub);
			}
		}
		file.delete();
	}

}
