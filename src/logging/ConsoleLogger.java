package logging;

import timing.TimeFormatter;

import java.util.concurrent.TimeUnit;

public class ConsoleLogger implements ILogger {
    @Override
    public void write(Object o) {
        System.out.println(o);
    }

    @Override
    public void write(Object... values) {
        for (Object val : values) {
            System.out.print(val + " ");
        }
        System.out.println();
    }

    @Override
    public void writeTime(long value, TimeUnit unit) {
        System.out.println("Elapsed time: " + TimeFormatter.format(value, unit));
    }

    @Override
    public void writeTime(String label, long value, TimeUnit unit) {
        System.out.println(label + ": " + TimeFormatter.format(value, unit));
    }

    @Override
    public void close() {}
}
