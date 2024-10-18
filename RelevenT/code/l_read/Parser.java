package l_read;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class describes a parser analyzing passed arguments.
 */
public class Parser {

    private int mode;
    private String token;
    private String logfile;
    private final ArrayList<String> employees;
    private final ArrayList<String> guests;
    private final ArrayList<String> argList;

    /**
     * Constructor for parser
     *
     * @param args String[], the arguments
     */
    public Parser(String[] args) {
        employees = new ArrayList<String>();
        guests = new ArrayList<String>();
        argList = new ArrayList<>(Arrays.asList(args));

        boolean cont = findLogfile();
        if (cont) {
            cont = collectToken();
        }
        if (cont) {
            cont = collectNames(false);
        }
        if (cont) {
            cont = collectNames(true);
        }
        if (cont) {
            cont = findMode();
        }
        if (cont) {
            cont = argList.isEmpty();
        }
        if (!cont) {
            mode = Query.BROKEN;
        }
    }

    /**
     * Expects the logfile at the last position of argList
     * Takes the last argument and adds it as logfile and removes it from argList
     *
     * @return boolean, if argList empty
     */
    private boolean findLogfile() {
        int lastIndex = argList.size() - 1;
        if (lastIndex == -1) {
            return false;
        }
        logfile = argList.get(lastIndex);
        argList.remove(lastIndex);
        return true;
    }

    /**
     * Expects the token to follow the flag "-K".
     * If found exactly one "-K", token is added as token and removed from argList together with flag "-K"
     *
     * @return boolean, if exactly one token found.
     */
    private boolean collectToken() {
        ArrayList<String> tokens = new ArrayList<String>();
        int i = 0;
        while (i < argList.size() - 1) {
            if (argList.get(i).equals("-K")) {
                tokens.add(argList.get(i + 1));
                argList.remove(i);
                argList.remove(i);
            } else {
                i++;
            }
        }
        if (tokens.isEmpty()) {
            return false;
        }
        token = tokens.get(tokens.size()-1);
        return true;
    }

    /**
     * Expects the mode to be described by the flags "-S", "-R", "-T" or "-I".
     * Expects exactly one of these flag-types and at least one flag and will add the mode accordingly.
     * Flags will be removed from argList.
     *
     * @return boolean, if there was exactly one of these mode-flags.
     */
    private boolean findMode() {
        int a = 0;
        int[] counts = new int[]{0, 0, 0, 0};
        String[] flags = new String[]{"-S", "-R", "-T", "-I"};
        while (a < argList.size()) {
            for (int f = 0; f < 4; f++) {
                if (argList.get(a).equals(flags[f])) {
                    counts[f]++;
                    argList.remove(a);
                    a--;
                    break;
                }
            }
            a++;
        }
        boolean atLeastOne = false;
        for (int f = 0; f < 4; f++) {
            if (counts[f] > 0) {
                if (atLeastOne) {
                    return false;
                }
                atLeastOne = true;
                mode = f;
            }
        }
        return atLeastOne;
    }

    /**
     * Collects all provided names and adds them to the corresponding ArrayList as well as removes them
     * from the argList. Looks for guests or employees only by the flags "-G" or "-E"
     *
     * @param isGuest boolean, if looking for guests, else looking for employees.
     * @return boolean, always true since there will always be 0 to any person.
     */
    private boolean collectNames(boolean isGuest) {
        ArrayList<String> names = employees;
        String flag = "-E";
        if (isGuest) {
            names = guests;
            flag = "-G";
        }
        int i = 0;
        while (i < argList.size() - 1) {
            if (argList.get(i).equals(flag)) {
                names.add(argList.get(i + 1));
                argList.remove(i);
                argList.remove(i);
            } else {
                i++;
            }
        }
        return true;
    }

    /**
     * Returns a query containing the parsed args.
     *
     * @return Query, the query
     */
    public Query getQuery() {
        return new Query(mode, token, logfile, employees, guests);
    }


}
