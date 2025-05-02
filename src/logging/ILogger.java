package logging;

import java.util.concurrent.TimeUnit;

public interface ILogger {
    void write(Object o);
    void write(Object... values);
    void writeTime(long value, TimeUnit unit);
    void writeTime(String label, long value, TimeUnit unit);
    void close();
}
