package logging;

import java.io.File;
import java.io.IOException;

public class CSVLogger extends FileLogger {
    public CSVLogger(String filename, String[] header) throws IOException {
        super(filename);
        File file = new File(filename);
        if (file.length() == 0 && header != null) {
            writeRow(header);
        }
    }

    public void writeRow(String... values) {
        try {
            for (int i = 0; i < values.length; i++) {
                writer.write(values[i]);
                if (i < values.length - 1) {
                    writer.write(",");
                }
            }
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
