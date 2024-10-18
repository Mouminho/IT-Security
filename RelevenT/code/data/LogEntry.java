package data;

import java.io.Serial;
import java.io.Serializable;

/**
 * a single log event, encapsulating arrival and departure (if happened yet) of a employee/guest
 */
public class LogEntry implements Serializable {
    /**
     * room id to use for the entire gallery
     */
    public static final int GALLERY_ID = -1;
    public int roomId;
    public String name;
    public boolean isGuest;
    public int arrivalDate;
    public int departureDate = -1;

    public boolean isDeparted() {
        return departureDate != -1;
    }

    public LogEntry() {
    }

    public LogEntry(int roomId, String name, boolean isGuest, int arrivalDate) {
        this.roomId = roomId;
        this.name = name;
        this.isGuest = isGuest;
        this.arrivalDate = arrivalDate;
    }

    @Serial
    private static final long serialVersionUID = 1L;
}
