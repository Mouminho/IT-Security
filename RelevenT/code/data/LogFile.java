package data;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

/**
 * stores a list of LogEntry.
 */
public class LogFile {

    private final String file;
    private final String token;
    public ArrayList<LogEntry> data;

    private byte[] salt ;

    /**
     * turns a password into a key
     * @param pass password
     * @return key
     * @throws NoSuchAlgorithmException if not supported
     * @throws InvalidKeySpecException  if not supported
     */
    private Key GenerateKey(String pass) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        SecretKey tempKey = factory.generateSecret(new PBEKeySpec(pass.toCharArray(), salt, 1024, 256));
        return new SecretKeySpec(tempKey.getEncoded(), CryptoAlgo);
    }

    private static final int IV_SIZE = 12;
    private static final String CryptoAlgo = "ChaCha20-Poly1305";
    private static final String CryptoTrans = CryptoAlgo + "/None/NoPadding";
    private static final int SALT_SIZE = 4;

    public LogFile(String file, String token) throws LogException {
        this(file, token, true);
    }
    /**
     * loads a encrypted log file, if it exists.
     * (if the file does not exists, a subsequent WriteBack will create it.)
     * @param file logfile
     * @param token password
     * @param autoCreateFile create file or throw
     * @throws LogException on any error
     */
    public LogFile(String file, String token, boolean autoCreateFile) throws LogException {
        this.file = file;
        this.token = token;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            salt = new byte[SALT_SIZE];
            if (inputStream.read(salt) != SALT_SIZE)
                throw new LogException("failed reading salt");

            byte[] iv = new byte[IV_SIZE];
            if (inputStream.read(iv) != IV_SIZE)
                throw new LogException("failed reading iv");

            Cipher cipher = Cipher.getInstance(CryptoTrans);
            cipher.init(Cipher.DECRYPT_MODE, GenerateKey(token), new IvParameterSpec(iv));

            try (ObjectInputStream objectStream = new ObjectInputStream(new CipherInputStream(inputStream, cipher))) {
                data = (ArrayList<LogEntry>) objectStream.readObject();
                for (LogEntry e : data) {
                    if (!(e instanceof LogEntry))
                        throw new LogException("non LogEntry in data");
                }
                if (objectStream.read() != -1)
                    throw new LogException("file longer than expected");
            }
        } catch (FileNotFoundException e) {
            if (autoCreateFile)
                data = new ArrayList<>();
            else
                throw new IllegalArgumentException("file not found", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("IO error", e);
        } catch (InvalidKeyException e) {
            throw new LogException("crypto failure", e);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeySpecException |
                 NoSuchPaddingException e) {
            throw new LogException("internal crypto error", e);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new LogException("invalid class", e);
        }
    }

    /**
     * writes the log file back.
     * (if the file was not existing until now, creates it)
     * @throws LogException on any error
     */
    public void WriteBack() throws LogException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            SecureRandom rng = SecureRandom.getInstanceStrong();

            if (salt == null) {
                salt = new byte[SALT_SIZE];
                rng.nextBytes(salt);
            }
            outputStream.write(salt);

            byte[] iv = new byte[IV_SIZE];
            rng.nextBytes(iv);
            outputStream.write(iv);

            Cipher cipher = Cipher.getInstance(CryptoTrans);
            cipher.init(Cipher.ENCRYPT_MODE, GenerateKey(token), new IvParameterSpec(iv));

            try (ObjectOutputStream objectSteam = new ObjectOutputStream(new CipherOutputStream(outputStream, cipher))) {
                objectSteam.writeObject(data);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("IO error", e);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            throw new LogException("internal crypto error", e);
        }
    }
}
