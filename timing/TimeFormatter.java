package timing;

import java.util.concurrent.TimeUnit;

public class TimeFormatter {
    public static String format(long value, TimeUnit unit) {
        long converted = unit.convert(value, TimeUnit.NANOSECONDS);
        return converted + " " + unitToString(unit);
    }

    private static String unitToString(TimeUnit unit) {
        return switch (unit) {
            case NANOSECONDS -> "ns";
            case MICROSECONDS -> "μs";
            case MILLISECONDS -> "ms";
            case SECONDS -> "s";
            default -> unit.toString().toLowerCase();
        };
    }
}
