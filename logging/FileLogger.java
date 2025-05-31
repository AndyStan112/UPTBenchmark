package logging;

import timing.TimeFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FileLogger implements ILogger, AutoCloseable {
    protected final BufferedWriter writer;

    public FileLogger(String filename) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(filename, true));
    }

    @Override
    public void write(Object o) {
        try {
            writer.write(o.toString());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(Object... values) {
        try {
            for (Object val : values) {
                writer.write(val.toString());
                writer.write(" ");
            }
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeTime(long value, TimeUnit unit) {
        write("Elapsed time: " + TimeFormatter.format(value, unit));
    }

    @Override
    public void writeTime(String label, long value, TimeUnit unit) {
        write(label + ": " + TimeFormatter.format(value, unit));
    }

    @Override
    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
