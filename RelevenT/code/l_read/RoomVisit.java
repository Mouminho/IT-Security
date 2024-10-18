package l_read;

import java.util.ArrayList;

/**
 * Describes the all visits by persons to a certain room
 */
public class RoomVisit {
    private final int roomId;
    private final ArrayList<String> names;
    private final ArrayList<Boolean> areGuests;
    private final ArrayList<Visit> visits;

    /**
     * Static method to find index of RoomVisit with given room Id in a list of RoomVisits
     * @param list ArrayList</RoomVisit>, the RoomVisit-List
     * @param roomId int, the id to find
     * @return int, the index to corresponding id. Is -1 when not found.
     */
    public static int IndexOfRoomId(ArrayList<RoomVisit> list, int roomId) {
        for(int i = 0 ; i < list.size(); i++) {
            if(list.get(i).getRoomId() == roomId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Method to find index of Person within RoomVisit
     * @param name String, the name of the person
     * @param isGuest boolean, if person is guest
     * @return int, the index of the Person, -1 when not found
     */
    private int IndexOfPerson(String name, boolean isGuest) {
        for(int i = 0; i < names.size(); i++) {
            if(name.equals(names.get(i)) && isGuest == areGuests.get(i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Constructor for RoomVisit
     * @param roomId int, the id of the room related to this RoomVisit object
     */
    public RoomVisit(int roomId) {
        this.roomId = roomId;
        names = new ArrayList<String>();
        areGuests = new ArrayList<Boolean>();
        visits = new ArrayList<Visit>();
    }

    /**
     * Adds a new visit by a person to this room
     * @param name String, name of person
     * @param isGuest boolean, if person is guest
     * @param arrivalTime int, arrival time of this person
     * @param departureTime int, departure time of this person
     */
    public void add(String name,  boolean isGuest, int arrivalTime, int departureTime) {
        int index = IndexOfPerson(name, isGuest);
        // If person not yet in RoomVisit, new entry will be added
        if(index == -1) {
            names.add(name);
            areGuests.add(isGuest);
            visits.add(new Visit());
            index = names.size()-1;
        }
        visits.get(index).add(arrivalTime, departureTime);
    }

    /**
     * Returns if all Visitors added to this room have spent time together (= at the same time) in this room
     * @return boolean, if all added visitors have spent time together in this room
     */
    public boolean timeTogether() {
        if(visits.size() == 0) {
            return false;
        }
        Visit ret = visits.get(0);
        for(int i = 1; i < visits.size(); i++) {
            ret = new Visit(ret, visits.get(i));
        }
        return !ret.getList().isEmpty();
    }

    /**
     * Returns the size (= the count of person entries in this RoomVisit)
     * @return int, the size (= the count of person entries in this RoomVisit)
     */
    public int size() {
        return names.size();
    }

    /**
     * Returns roomId of this RoomVisit
     * @return int, roomId
     */
    public int getRoomId() {
        return roomId;
    }
}
