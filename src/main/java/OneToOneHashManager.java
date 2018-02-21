import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class OneToOneHashManager implements HashManager {
    private Map<String, byte[]> userToHash;
    private Map<byte[], String> hashToUser;
    private Map<byte[], ScheduledFuture<?>> hashToTask;
    private Random random;
    private int waitTimeMinutes;
    private ScheduledExecutorService timerKeeper;

    public OneToOneHashManager(int waitTimeMinutes) {
        userToHash = new ConcurrentHashMap<>();
        hashToUser = new ConcurrentHashMap<>();
        hashToTask = new ConcurrentHashMap<>();
        random = new Random();
        timerKeeper = new ScheduledThreadPoolExecutor(2);

        this.waitTimeMinutes = waitTimeMinutes;
    }


    /**
     * Cancels the timers, so server won't need to wait for shutdown
     */
    public void shutdown() {
        //doesn't matter to interrupt or not, as even in interrup GC will collect the maps
        hashToTask.forEach((hash, future)->future.cancel(false));
    }


    public byte[] generateHash(String username) {
        byte[] hash = new byte[256];
        random.nextBytes(hash);

        userToHash.put(username, hash);
        hashToUser.put(hash, username);
        hashToTask.put(hash,
                timerKeeper.schedule(new RemoveHash(userToHash, hashToUser, username), 1, TimeUnit.MINUTES));
        return hash;
    }

    public void prolongHash(String username, byte[] hash)
    {
        if (hash.length != 256)
        {
            throw new IllegalArgumentException("Passed byte array is of incorrect size");
        }

        byte[] storedHash = userToHash.get(username);
        if (!Arrays.equals(hash, storedHash))
        {
            throw new IllegalArgumentException("The hash doesn't correspond to stored hash for the username");
        }

        hashToTask.get(hash).cancel(false);
        hashToTask.put(hash,
                timerKeeper.schedule(new RemoveHash(userToHash, hashToUser, username), 1, TimeUnit.MINUTES));
    }

    public boolean isLoggedIn(String username, byte[] hash) {
        byte[] storedHash = userToHash.get(username);
        return storedHash != null && Arrays.equals(storedHash, hash);
    }

    public int expirationPeriodMinutes()
    {
        return waitTimeMinutes;
    }
}
