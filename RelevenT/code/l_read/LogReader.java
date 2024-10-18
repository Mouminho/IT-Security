package l_read;


import java.util.ArrayList;
import java.util.Collections;

import data.LogEntry;
import data.LogException;
import data.LogFile;

/**
 * Class describes a reader for given log file.
 * Callable methods within this class will return a string
 * containing the wanted information.
 */
public class LogReader {

    private final ArrayList<LogEntry> data;

    private static boolean containsString(ArrayList<String> list, String str) {
        for (String s : list) {
            if (s.equals(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sorts given ArrayList of Strings alphabetically
     *
     * @param toSort ArrayList</String>, ArrayList to sort
     */
    private static void sortStringAlphabetically(ArrayList<String> toSort) {
        toSort.sort(String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Joins an ArrayList of String to a string separated by one comma
     *
     * @param list ArrayList</String>, ArrayList to join
     * @return String, the joined string
     */
    private static String listToString(ArrayList<String> list) {
        return String.join(",", list);
    }

    /**
     * Constructor for LogReader
     *
     * @param logFile String, path to logfile
     * @param token   String, secret token for logfile
     * @throws LogException
     */
    public LogReader(String logFile, String token) throws LogException {
        LogFile lf = new LogFile(logFile, token, false);
        data = lf.data;
    }

    private int now() {
        int max = 0;
        for (LogEntry entry : data) {
            if (entry.arrivalDate > max) {
                max = entry.arrivalDate;
            }
            if (entry.departureDate > max) {
                max = entry.departureDate;
            }
        }
        return max;
    }

    /**
     * Returns the highest roomId logged in the logfile
     *
     * @return int, the highest roomId within the logfile
     */
    private int highestRoom() {
        int max = 0;
        for (LogEntry entry : data) {
            if (entry.roomId > max) {
                max = entry.roomId;
            }
        }
        return max;
    }

    /**
     * Returns either all guest names that have not left the gallery or all employee names
     *
     * @param getGuests boolean, true results in guest names, false in employee names
     * @return ArrayList</ String> the list of guests only or employees only
     */
    private ArrayList<String> personsInGallery(boolean getGuests) {
        ArrayList<String> ret = new ArrayList<String>();
        for (LogEntry entry : data) {
            if (!entry.isDeparted() && entry.isGuest == getGuests && !containsString(ret, entry.name)) {
                ret.add(entry.name);
            }
        }
        sortStringAlphabetically(ret);
        return ret;
    }

    /**
     * Returns all names of persons that are currently in given room
     *
     * @param roomId int, the room id
     * @return ArrayList</ String> the list of names
     */
    private ArrayList<String> personsInRoom(int roomId) {
        ArrayList<String> ret = new ArrayList<String>();
        for (LogEntry entry : data) {
            if (entry.roomId == roomId && !entry.isDeparted()) {
                ret.add(entry.name);
            }
        }
        sortStringAlphabetically(ret);
        return ret;
    }

    /**
     * Returns an ArrayList of LogEntry containing all entries of a guest or employee by name.
     *
     * @param name    String, name of guest or employee
     * @param isGuest boolean, if guest, else employee
     * @return ArrayList</ LogEntry>, all entries
     */
    private ArrayList<LogEntry> getByName(String name, boolean isGuest) {
        ArrayList<LogEntry> ret = new ArrayList<LogEntry>();
        for (LogEntry entry : data) {
            if (entry.name.equals(name) && entry.isGuest == isGuest) {
                ret.add(entry);
            }
        }
        return ret;
    }

    /**
     * Returns a string containing all persons currently within the gallery.
     *
     * @return String, the string containing all persons in the gallery. First line shows employees, second line guests, then room by room
     */
    public String statusString() {
        String employees = listToString(personsInGallery(false));
        String guests = listToString(personsInGallery(true));
        ArrayList<String> rooms = new ArrayList<String>();
        int hr = highestRoom() + 1;
        for (int i = 0; i < hr; i++) {
            ArrayList<String> roomGuests = personsInRoom(i);
            if (!roomGuests.isEmpty()) {
                rooms.add(i + ": " + listToString(roomGuests) + "\n");
            }
        }
        if (employees.length() > 0) {
            employees += "\n";
        }
        if (guests.length() > 0) {
            guests += "\n";
        }
        return employees + guests + String.join("", rooms);
    }

    /**
     * Returns a string containing all rooms visited by a guest or employee
     *
     * @param name    String, name of guest or employee
     * @param isGuest boolean, true if guest, else employee
     * @return String, string containing all visited rooms
     */
    public String roomVisitString(String name, boolean isGuest) {
        ArrayList<LogEntry> entries = getByName(name, isGuest);
        ArrayList<String> rooms = new ArrayList<String>();
        for (LogEntry entry : entries) {
            if (entry.roomId != LogEntry.GALLERY_ID) {
                rooms.add(String.valueOf(entry.roomId));
            }
        }
        return listToString(rooms);
    }

    /**
     * Returns a number within a string containing the seconds a person has spent in the gallery until leaving or until now
     *
     * @param name    String, the name of the person
     * @param isGuest boolean, if person is guest, else employee
     * @return String, number of seconds
     */
    public String timeVisitString(String name, boolean isGuest) {
        ArrayList<LogEntry> entries = getByName(name, isGuest);
        boolean empty = true;
        int sum = 0;
        for (LogEntry entry : entries) {
            if (entry.roomId == LogEntry.GALLERY_ID) {
                if (!entry.isDeparted()) {
                    sum += now() - entry.arrivalDate;
                } else {
                    sum += entry.departureDate - entry.arrivalDate;
                }
                empty = false;
            }
        }
        if (empty) {
            return "";
        }
        return String.valueOf(sum);
    }

    /**
     * Returns a comma-separated string of numbers ordered by ascending order containing all rooms the guests and
     * employees provided have spent time together within.
     *
     * @param guests, ArrayList</String>, list of guests
     * @param employees, ArrayList</String>, list of employees
     * @return String, rooms the persons have occupied at the same time
     */
    public String roomsWithSamePersons(ArrayList<String> guests, ArrayList<String> employees) {
        ArrayList<RoomVisit> visits = new ArrayList<RoomVisit>();
        for(LogEntry entry : data) {
            if((entry.isGuest && containsString(guests, entry.name)) || (!entry.isGuest && containsString(employees, entry.name))) {
                int index = RoomVisit.IndexOfRoomId(visits, entry.roomId);
                if(index < 0) {
                    visits.add(new RoomVisit(entry.roomId));
                    index = visits.size()-1;
                }
                visits.get(index).add(entry.name, entry.isGuest, entry.arrivalDate, entry.departureDate);
            }
        }
        ArrayList<Integer> roomIdsTogether = new ArrayList<Integer>();
        int personCount = guests.size() + employees.size();
        for(RoomVisit rv : visits) {
            if(rv.timeTogether() && rv.getRoomId() > 0 && rv.size() == personCount) {
                roomIdsTogether.add(rv.getRoomId());
            }
        }
        Collections.sort(roomIdsTogether);
        ArrayList<String> stringList = new ArrayList<String>();
        for(Integer i : roomIdsTogether) {
            stringList.add(String.valueOf(i));
        }
        return listToString(stringList);
    }
}
