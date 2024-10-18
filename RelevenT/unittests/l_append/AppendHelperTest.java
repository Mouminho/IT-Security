package l_append;

import data.LogEntry;
import data.LogException;
import data.LogFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppendHelperTest {
    @Test
    void basic() throws LogException {
        LogFile l = new LogFile("notexistingfile", "x");
        AppendHelper.arrival(l, LogEntry.GALLERY_ID, "a", true, 1);
        AppendHelper.arrival(l, 1, "a", true, 1);
        AppendHelper.arrival(l, LogEntry.GALLERY_ID, "a", false, 1);
        AppendHelper.arrival(l, 1, "a", false, 2);
        assertEquals(4, l.data.size());

        assertThrows(IllegalArgumentException.class, () -> AppendHelper.arrival(l, 1, "a", false, 2));
        assertThrows(IllegalArgumentException.class, () -> AppendHelper.arrival(l, 2, "a", false, 2));
        assertThrows(IllegalArgumentException.class, () -> AppendHelper.arrival(l, 1, "b", false, 2));
        assertThrows(IllegalArgumentException.class, () -> AppendHelper.arrival(l, LogEntry.GALLERY_ID, "b", false, 1));

        AppendHelper.departure(l, 1, "a", false, 3);
        AppendHelper.arrival(l, 2, "a", false, 3);
        AppendHelper.departure(l, 2, "a", false, 4);
        AppendHelper.departure(l, LogEntry.GALLERY_ID, "a", false, 4);
        assertEquals(5, l.data.size());

        assertThrows(IllegalArgumentException.class, () -> AppendHelper.arrival(l, 1, "a", false, 10));
        assertThrows(IllegalArgumentException.class, () -> AppendHelper.departure(l, 2, "a", true, 10));
        assertThrows(IllegalArgumentException.class, () -> AppendHelper.departure(l, LogEntry.GALLERY_ID, "a", true, 10));
        assertThrows(IllegalArgumentException.class, () -> AppendHelper.departure(l, 2, "c", true, 10));
        assertThrows(IllegalArgumentException.class, () -> AppendHelper.departure(l, LogEntry.GALLERY_ID, "c", true, 10));
    }
}