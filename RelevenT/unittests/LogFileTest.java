import data.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;


class LogFileTest {
    @Test
    void basic(@TempDir Path tempDir) {
        try {
            Path file = tempDir.resolve("test.log");
            LogFile l = new LogFile(file.toString(), "abc");
            LogEntry e = new LogEntry();
            e.isGuest = true;
            e.name = "42";
            l.data.add(e);
            l.WriteBack();
            l = new LogFile(file.toString(), "abc");
            assertEquals(1, l.data.size());
            assertEquals(e.name, l.data.getFirst().name);
            assertEquals(e.isGuest, l.data.getFirst().isGuest);

            try {
                l = new LogFile(file.toString(), "x");
                fail("did not throw invalid token");
            } catch (LogException _) {}

            Path file2 = tempDir.resolve("test2.log");
            Files.copy(file, file2);
            try (RandomAccessFile s = new RandomAccessFile(file2.toString(), "rw")) {
                s.seek(16);
                s.write(42);
            }
            try {
                l = new LogFile(file2.toString(), "abc");
                fail("did not throw tampered file");
            } catch (LogException _) {}

            Files.copy(file, file2, StandardCopyOption.REPLACE_EXISTING);
            try (FileOutputStream s = new FileOutputStream(file2.toString(), true)) {
                s.write(42);
            }
            try {
                l = new LogFile(file2.toString(), "abc");
                fail("did not throw tampered file (append)");
            } catch (LogException _) {}
        } catch (LogException | IOException e) {
            fail(e);
        }
    }
}