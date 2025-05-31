package benchmark.hdd;

import benchmark.IBenchmark;
import logging.ILogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class HDDWriteSpeed implements IBenchmark {
	private ILogger logger;

	public HDDWriteSpeed(ILogger logger) {
		this.logger = logger;
	}

	@Override
	public void init(Object... params) {
		if (params.length > 0 && params[0] instanceof ILogger) {
			this.logger = (ILogger) params[0];
		}
	}

	@Override
	public void warmup() {}

	@Override
	public void run() {
		throw new UnsupportedOperationException("Use run(Object...) instead");
	}

	@Override
	public void run(Object... options) {
		FileWriter writer = new FileWriter(logger);

		String option = (String) options[0];
		Boolean clean = (Boolean) options[1];

		String baseDir;
		if (isWindows()) {
			baseDir = "D:" + File.separator + "000-bench";
		} else {
			baseDir = System.getProperty("user.home") + File.separator + "bench";
		}

		new File(baseDir).mkdirs();

		String prefix = Paths.get(baseDir, "write-").toAbsolutePath().toString();
		if (!prefix.endsWith(File.separator)) {
			prefix += File.separator;
		}
		String suffix = ".dat";

		int minIndex = 0;
		int maxIndex = 8;
		long fileSize = 1024L * 1024 * 256; // 256 MB
		int bufferSize = 1024 * 4;          // 4 KB

		try {
			if (option.equals("fs")) {
				writer.streamWriteFixedFileSize(prefix, suffix, minIndex,
						maxIndex, fileSize, clean);
			} else if (option.equals("fb")) {
				writer.streamWriteFixedBufferSize(prefix, suffix, minIndex,
						maxIndex, bufferSize, clean);
			} else {
				throw new IllegalArgumentException("Unknown option: " + option);
			}
		} catch (IOException e) {
			logger.write("IO Error:", e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	@Override
	public void clean() {}

	@Override
	public void cancel() {

	}

}
