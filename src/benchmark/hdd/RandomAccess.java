package benchmark.hdd;

import timing.Timer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

class RandomAccess {
		private final Random random = new Random();

		public long randomReadFixedSize(String filePath, int bufferSize, int toRead) throws IOException {
			try (RandomAccessFile file = new RandomAccessFile(filePath, "rw")) {
				long fileSize = file.length();
				byte[] buffer = new byte[bufferSize];
				Timer timer = new Timer();

				timer.start();
				for (int i = 0; i < toRead; i++) {
					long pos = Math.abs(random.nextLong()) % (fileSize - bufferSize);
					file.seek(pos);
					file.read(buffer);
				}
				return timer.stop() / 1_000_000;
			}
		}

		public int randomReadFixedTime(String filePath, int bufferSize, int millis) throws IOException {
			RandomAccessFile file = new RandomAccessFile(filePath, "rw");
			long fileSize = file.getChannel().size();
			int counter = 0;
			byte[] bytes = new byte[bufferSize];
			Random random = new Random();

			long start = System.nanoTime();
			long end = start + millis * 1_000_000L;

			while (System.nanoTime() < end) {
				long position = Math.abs(random.nextLong()) % (fileSize - bufferSize);
				file.seek(position);
				file.read(bytes);
				counter++;
			}

			file.close();
			return counter;
		}

		public long randomWriteFixedSize(String filePath, int bufferSize, int toWrite) throws IOException {
			try (RandomAccessFile file = new RandomAccessFile(filePath, "rw")) {
				long fileSize = file.length();
				byte[] buffer = new byte[bufferSize];
				random.nextBytes(buffer);
				Timer timer = new Timer();

				timer.start();
				for (int i = 0; i < toWrite; i++) {
					long pos = Math.abs(random.nextLong()) % (fileSize - bufferSize);
					file.seek(pos);
					file.write(buffer);
				}
				return timer.stop() / 1_000_000;
			}
		}

		public int randomWriteFixedTime(String filePath, int bufferSize, long millis) throws IOException {
			try (RandomAccessFile file = new RandomAccessFile(filePath, "rw")) {
				long fileSize = file.length();
				byte[] buffer = new byte[bufferSize];
				random.nextBytes(buffer);
				long start = System.nanoTime();
				int counter = 0;

				while ((System.nanoTime() - start) / 1_000_000 < millis) {
					long pos = Math.abs(random.nextLong()) % (fileSize - bufferSize);
					file.seek(pos);
					file.write(buffer);
					counter++;
				}
				return counter;
			}
		}
	}