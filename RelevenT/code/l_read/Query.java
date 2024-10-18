package l_read;

import java.util.ArrayList;

/**
 * Describes a query to read data from the logfile
 */
public class Query {
    // would be nice if this was an enum, but make file is broken and doesnt allow it...
    // BROKEN: Query is broken or not applicable
    public static final int BROKEN = -1;
    // SMODE: Query for the status of the gallery, flag "-F"
    public static final int SMODE = 0;
    // RMODE: Query for the rooms visited by a person, flag "-R"
    public static final int RMODE = 1;
    // TMODE: Query for the time a person has spent in the gallery, flag "-T"
    public static final int TMODE = 2;
    //IMode: Query for the times different persons have spent time in the same room, flag "-I"
    public static final int IMODE = 3;

    private int mode;
    private String token;
    private String logfile;
    private ArrayList<String> employees;
    private ArrayList<String> guests;

    /**
     * Constructor for query
     *
     * @param mode      int, the mode of the query.
     * @param token     String, the token provided for the logfile
     * @param logfile   String, the provided path to the logfile
     * @param employees ArrayList</String>, list of employees provided
     * @param guests    ArrayList</String>, list of guests provided
     */
    public Query(int mode, String token, String logfile, ArrayList<String> employees, ArrayList<String> guests) {
        this.mode = mode;
        this.token = token;
        this.logfile = logfile;
        this.employees = employees;
        this.guests = guests;

        // If something is wrong with the query, mode is set to BROKEN and everything else is removed
        if (!argsAllowed() || !modeAllowed()) {
            this.mode = BROKEN;
            this.token = "";
            this.logfile = "";
            this.employees = null;
            this.guests = null;
        }
    }

    /**
     * checks if every argument follows the guidelines.
     * In detail this means that every provided person name can only contain letters.
     *
     * @return boolean, if every argument follows the guidelines
     */
    private boolean argsAllowed() {
        for (String s : employees) {
            if (!s.matches("[A-Za-z]*")) {
                return false;
            }
        }
        for (String s : guests) {
            if (!s.matches("[A-Za-z]*")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given mode corresponds to given arguments.
     *
     * @return boolean, if mode corresponds to the arguments.
     */
    private boolean modeAllowed() {
        switch (mode) {
            case SMODE:
                // query expects no persons at all
                return guests.isEmpty() && employees.isEmpty();
            case RMODE:
            case TMODE:
                // query for exactly one person, no more, no less
                return (guests.isEmpty() && employees.size() == 1) || (guests.size() == 1 && employees.isEmpty());
            case IMODE:
                // query for at least on person
                return !guests.isEmpty() || !employees.isEmpty();
        }
        return false;
    }

    public int getMode() {
        return mode;
    }

    public String getToken() {
        return token;
    }

    public String getLogfile() {
        return logfile;
    }

    public ArrayList<String> getEmployees() {
        return employees;
    }

    public ArrayList<String> getGuests() {
        return guests;
    }

    /**
     * Returns the only name within the query.
     * Only works IF there is exactly one name only.
     *
     * @return String, one name from the query.
     */
    public String oneName() {
        if (!guests.isEmpty()) {
            return guests.get(0);
        }
        if (!employees.isEmpty()) {
            return employees.get(0);
        }
        return "";
    }

    /**
     * Returns if there is at least one guest within this query.
     *
     * @return bool, if there is a guest within this query.
     */
    public boolean hasGuest() {
        return !guests.isEmpty();
    }
}
