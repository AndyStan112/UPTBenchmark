package benchmark.hdd;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import benchmark.IBenchmark;

public class HDDRandomAccess implements IBenchmark {

	private final static String PATH = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "temp" + File.separator + "test.raf";
	private String result;

	@Override
	public void init(Object... params) {
		File tempFile = new File(PATH);
		tempFile.getParentFile().mkdirs();

		long fileSizeInBytes = (Long) params[0];

		try (RandomAccessFile rafFile = new RandomAccessFile(tempFile, "rw")) {
			Random rand = new Random();
			int bufferSize = 4 * 1024;
			long toWrite = fileSizeInBytes / bufferSize;
			byte[] buffer = new byte[bufferSize];

			for (long i = 0; i < toWrite; i++) {
				rand.nextBytes(buffer);
				rafFile.write(buffer);
			}
			tempFile.deleteOnExit();

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void warmup() {}

	@Override
	public void run() {
		throw new UnsupportedOperationException("Use run(Object[]) instead");
	}

	@Override
	public void run(Object... options) {
		Object[] param = (Object[]) options;
		final int steps = 25000;
		final int runtime = 5000;

		try {
			String mode = String.valueOf(param[0]).toLowerCase();
			String type = String.valueOf(param[1]).toLowerCase();
			int bufferSize = Integer.parseInt(String.valueOf(param[2]));

			RandomAccess access = new RandomAccess();

			if (mode.equals("r")) {
				if (type.equals("fs")) {
					long timeMs = access.randomReadFixedSize(PATH, bufferSize, steps);
					result = steps + " random reads in " + timeMs + " ms ["
							+ (steps * bufferSize / 1024 / 1024) + " MB, "
							+ String.format("%.2f", 1.0 * steps * bufferSize / 1024 / 1024 / timeMs * 1000) + " MB/s]";
				} else if (type.equals("ft")) {
					int ios = access.randomReadFixedTime(PATH, bufferSize, runtime);
					result = ios + " I/Os in " + runtime + " ms ["
							+ (ios * bufferSize / 1024 / 1024) + " MB, "
							+ String.format("%.2f", 1.0 * ios * bufferSize / 1024 / 1024 / runtime * 1000) + " MB/s]";
				}
			} else if (mode.equals("w")) {
				if (type.equals("fs")) {
					long timeMs = access.randomWriteFixedSize(PATH, bufferSize, steps);
					result = steps + " random writes in " + timeMs + " ms ["
							+ (steps * bufferSize / 1024 / 1024) + " MB, "
							+ String.format("%.2f", 1.0 * steps * bufferSize / 1024 / 1024 / timeMs * 1000) + " MB/s]";
				} else if (type.equals("ft")) {
					int ios = access.randomWriteFixedTime(PATH, bufferSize, runtime);
					result = ios + " I/Os in " + runtime + " ms ["
							+ (ios * bufferSize / 1024 / 1024) + " MB, "
							+ String.format("%.2f", 1.0 * ios * bufferSize / 1024 / 1024 / runtime * 1000) + " MB/s]";
				}
			} else {
				throw new UnsupportedOperationException("Unknown mode: " + mode);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clean() {}

	@Override
	public void cancel() {}

	public String getResult() {
		return result;
	}


}
