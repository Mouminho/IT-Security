package l_append;

import data.LogEntry;

public class EntryInformation {
    private int roomId = LogEntry.GALLERY_ID;
    private String name;
    private boolean isGuest;
    private boolean isArrival;
    private int timestamp;
    private String token;
    private String path;

    private static final int minConst = 1;
    private static final int maxConst = 1073741823;

    public void setToken(String token) {
        if (token.matches("^[a-zA-Z0-9]+$")) {
            this.token = token;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setPath(String path) {
        if (path.matches("^[a-zA-Z0-9./\\\\_]+$")) {
            this.path = path;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setRoomId(String roomId) {
        if (roomId.matches("[0-9]+")) {
            int convertedRoomId = Integer.parseInt(roomId);
            if (0 <= convertedRoomId && convertedRoomId <= maxConst) {
                this.roomId = convertedRoomId;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setName(String name) {
        if (name.matches("^[a-zA-Z]*$")) {
            this.name = name;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setTimestamp(int timestamp) {
        if (minConst <= timestamp && timestamp <= maxConst) {
            this.timestamp = timestamp;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setGuest(boolean guest) {
        isGuest = guest;
    }

    public void setArrival(boolean arrival) {
        isArrival = arrival;
    }

    public String getToken() {
        return token;
    }

    public String getPath() {
        return path;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public boolean isGuest() {
        return isGuest;
    }

    public boolean isArrival() {
        return isArrival;
    }

    public int getTimestamp() {
        return timestamp;
    }
}
