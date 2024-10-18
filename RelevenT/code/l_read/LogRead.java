package l_read;

import data.LogException;

public class LogRead {
    public static void main(String[] args) {
        Parser parser = new Parser(args);
        Query query = parser.getQuery();
        int mode = query.getMode();
        if (mode == Query.BROKEN) {
            // Query not correct, printing invalid
            System.out.println("invalid");
            System.exit(255);
        } else {
            try {
                LogReader logReader = new LogReader(query.getLogfile(), query.getToken());
                switch (mode) {
                    case Query.SMODE:
                        System.out.print(logReader.statusString());
                        break;
                    case Query.RMODE:
                        System.out.print(logReader.roomVisitString(query.oneName(), query.hasGuest()));
                        break;
                    case Query.TMODE:
                        System.out.println(logReader.timeVisitString(query.oneName(), query.hasGuest()));
                        break;
                    case Query.IMODE:
                        System.out.println(logReader.roomsWithSamePersons(query.getGuests(), query.getEmployees()));
                        break;
                }
            }  catch (LogException e) {
                System.out.println("integrity violation");
                System.exit(255);
            } catch (IllegalArgumentException e) {
                System.out.println("invalid");
                System.exit(255);
            }
        }
    }
}