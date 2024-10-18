package l_read;

import java.util.ArrayList;

/**
 * Class describes multiple visit times for one person and one location
 */
public class Visit {
    private final ArrayList<Integer[]> times;

    /**
     * Intern greater function, handles negative numbers (especially -1) as infinity
     * @param a int, parameter that is greater expected to be greater
     * @param b int, parameter, that is not expected to be greater
     * @return boolean, if a > b
     */
    private static boolean greater(int a, int b) {
        if(a < 0 || b < 0) {
            return false;
        }
        return a > b;
    }

    /**
     * Intern min function, handels negative numbers (especially -1) as infinity
     * @param a int, first parameter
     * @param b int, second parameter
     * @return int, min(a, b)
     */
    private int min(int a, int b) {
        if (greater(a, b)) {
            return b;
        }
        return a;
    }

    /**
     * Intern max function, handels negative numbers (especially -1) as infinity
     * @param a int, the first parameter
     * @param b int, the second parameter
     * @return int, max(a, b)
     */
    private int max(int a, int b) {
        if(greater(a, b)) {
            return a;
        }
        return b;
    }

    /**
     * Constructor for Visit
     */
    public Visit() {
        times = new ArrayList<Integer[]>();
    }

    /**
     * Constructor for Visit from 2 Visits.
     * This visit will combine the two given visits by being limited only to common time of both visits
     * @param visit1 Visit, the first visit
     * @param visit2 Visit, the second visit
     */
    public Visit(Visit visit1, Visit visit2) {
        ArrayList<Integer[]> times1 = visit1.getList();
        ArrayList<Integer[]> times2 = visit2.getList();
        times = new ArrayList<Integer[]>();
        for(Integer[] entry1 : times1) {
            for(Integer[] entry2 : times2) {
                Integer[] newEntry = new Integer[2];
                newEntry[0] = max(entry1[0], entry2[0]);
                newEntry[1] = min(entry1[1], entry2[1]);
                if(!greater(newEntry[0], newEntry[1])) {
                    times.add(newEntry);
                }
            }
        }
    }

    /**
     * Adds a new arrival and departure to this location by the same person
     * @param arrival int, arrival time
     * @param departure int, departure time
     */
    public void add(int arrival, int departure) {
        Integer[] toAdd = new Integer[2];
        toAdd[0] = arrival;
        toAdd[1] = departure;
        times.add(toAdd);
    }

    /**
     * Returns the times list
     * @return ArrayList</Integer[]>, the list
     */
    public ArrayList<Integer[]> getList() {
        return times;
    }
}
