package l_append;

import data.LogEntry;
import data.LogFile;

public class AppendHelper {
    /**
     * someone arrives
     * does check for gallery status,
     * does not check for parameter validity
     * does not call writeBack
     * @param f
     * @param roomId
     * @param name
     * @param guest
     * @param date
     */
    public static void arrival(LogFile f, int roomId, String name, boolean guest, int date) {
        checkTimeTravel(f, date);
        if (roomId == LogEntry.GALLERY_ID) {
            for (LogEntry e : f.data) {
                if (e.isGuest == guest && e.name.equals(name)) {
                    if (e.isDeparted())
                        continue;
                    throw new IllegalArgumentException("already in gallery");
                }
            }
        }
        else {
            boolean isInGallery = false;
            for (LogEntry e : f.data) {
                if (e.isGuest == guest && e.name.equals(name)) {
                    if (e.isDeparted())
                        continue;
                    if (e.roomId == LogEntry.GALLERY_ID)
                        isInGallery = true;
                    else
                        throw new IllegalArgumentException("already in room");
                }
            }
            if (!isInGallery)
                throw new IllegalArgumentException("not in gallery");
        }
        f.data.add(new LogEntry(roomId, name, guest, date));
    }

    /**
     * someone departs
     * does check for gallery status,
     * does not check for parameter validity
     * does not call writeBack
     * @param f
     * @param roomId
     * @param name
     * @param guest
     * @param date
     */
    public static void departure(LogFile f, int roomId, String name, boolean guest, int date) {
        checkTimeTravel(f, date);
        LogEntry dep = null;
        if (roomId == LogEntry.GALLERY_ID) {
            for (LogEntry e : f.data) {
                if (e.isGuest == guest && e.name.equals(name)) {
                    if (e.isDeparted())
                        continue;
                    if (e.roomId == roomId)
                        dep = e;
                    else
                        throw new IllegalArgumentException("still in room");
                }
            }
        }
        else {
            boolean isInGallery = false;
            for (LogEntry e : f.data) {
                if (e.isGuest == guest && e.name.equals(name)) {
                    if (e.isDeparted())
                        continue;
                    if (e.roomId == roomId)
                        dep = e;
                    else if (e.roomId == LogEntry.GALLERY_ID)
                        isInGallery = true;
                }
            }
            if (!isInGallery)
                throw new IllegalArgumentException("not in gallery");
        }
        if (dep == null)
            throw new IllegalArgumentException("not in room");
        dep.departureDate = date;
    }

    private static void checkTimeTravel(LogFile f, int date) {
        for (LogEntry e : f.data) {
            if (e.arrivalDate > date || e.departureDate > date)
                throw new IllegalArgumentException("invented time travel?");
        }
    }
}
