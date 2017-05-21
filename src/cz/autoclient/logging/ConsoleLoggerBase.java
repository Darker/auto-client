package cz.autoclient.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ConsoleLoggerBase extends Handler {

    @Override
    public void publish(LogRecord record) {
        System.out.println(record.getMessage());
    }

    @Override
    public void flush() {
        System.out.flush();
    }
    // No need to close system.out
    @Override
    public void close() throws SecurityException {}
}
