package l_append;

import data.LogException;
import data.LogFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

/**
 * Current assumption: For the -B flag the arguments are in the specific order -B <file>
 */
public class LogAppend {

    public static void main(String[] args) {
        // this might be setting of any further activities so renaming could be considered
        try {
            checkForBatch(args);
        } catch (LogException e) {
            System.out.println("integrity violation");
            System.exit(255);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.out.println("invalid");
            System.exit(255);
        }
    }

    /**
     * If too many or too little arguments given, exit.
     *
     * @param args
     */
    public static void checkForBatch(String[] args) throws LogException {
        for (String arg : args) {
            if ("-B".equals(arg)) {
                appendFromBatch(args);
                return;
            }
        }
        editLogFile(parseLine(args));
    }

    /**
     * Checks for correct flag (-K) and validate path. Will then read line by line and further process.
     *
     * @param args
     */
    public static void appendFromBatch(String[] args) {
        // check flag
        if (!Objects.equals(args[0], "-B")) {
            System.out.println("invalid");
            System.exit(255);
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(args[1]));
            String line = reader.readLine();

            while (line != null) {
                try {
                    // *Write useful comment here*
                    // Split the line into the args, then build new EntryInformation
                    EntryInformation entryInformation = parseLine(prepareLineFromBatch(line));
                    // actually edit log
                    editLogFile(entryInformation);
                    // read next line

                } catch (LogException e) {
                    System.out.println("integrity violation");
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("invalid");
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("invalid");
            System.exit(255);
        }
    }

    /**
     * Loops the given arg to edit for a new LogEntry
     *
     * @param args
     * @return
     */
    public static EntryInformation parseLine(String[] args) {
        EntryInformation entryInfo = new EntryInformation();

        // specifying -E and -G together is not allowed
        boolean isGuestEntry = false;
        boolean isEmployeeEntry = false;
        // specifying -A and -L together is not allowed
        boolean isArrival = false;
        boolean isDeparture = false;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-B":
                    throw new IllegalArgumentException();
                case "-T":
                    entryInfo.setTimestamp(Integer.parseInt(args[i + 1]));
                    i++;
                    break;
                case "-K":
                    entryInfo.setToken(args[i + 1]);
                    i++;
                    break;
                case "-E":
                    if(isEmployeeEntry){
                        throw new IllegalArgumentException();
                    }
                    isGuestEntry = true;
                    entryInfo.setGuest(false);
                    entryInfo.setName(args[i + 1]);
                    i++;
                    break;
                case "-G":
                    if(isGuestEntry){
                        throw new IllegalArgumentException();
                    }
                    isEmployeeEntry = true;
                    entryInfo.setGuest(true);
                    entryInfo.setName(args[i + 1]);
                    i++;
                    break;
                case "-A":
                    if(isDeparture){
                        throw new IllegalArgumentException();
                    }
                    isArrival = true;
                    entryInfo.setArrival(true);
                    break;
                case "-L":
                    if(isArrival){
                        throw new IllegalArgumentException();
                    }
                    isDeparture = true;
                    entryInfo.setArrival(false);
                    break;
                case "-R":
                    entryInfo.setRoomId(args[i + 1]);
                    i++;
                    break;
                default:
                    if(i == args.length-1)
                        entryInfo.setPath(args[i]); // very last argument is globally set and the file -> should need no further action
                    break;
            }
        }
        if(entryInfo.getPath() == null)
        {
            throw new IllegalArgumentException();
        }
        return entryInfo;
    }

    /**
     * Accesses the LogFile, edits and writes back
     *
     * @param entryInfo
     * @throws LogException
     */
    public static void editLogFile(EntryInformation entryInfo) throws LogException {
        LogFile logFile = new LogFile(entryInfo.getPath(), entryInfo.getToken());
        if (entryInfo.isArrival())
            AppendHelper.arrival(logFile, entryInfo.getRoomId(), entryInfo.getName(), entryInfo.isGuest(), entryInfo.getTimestamp());
        else
            AppendHelper.departure(logFile, entryInfo.getRoomId(), entryInfo.getName(), entryInfo.isGuest(), entryInfo.getTimestamp());
        logFile.WriteBack();
    }

    /**
     * Transforms a String into a StringArray by splitting at whitespaces.
     *
     * @param line
     * @return
     */
    public static String[] prepareLineFromBatch(String line) {
        String[] prepLine = line.split("\\s+");
        return prepLine;
    }
}