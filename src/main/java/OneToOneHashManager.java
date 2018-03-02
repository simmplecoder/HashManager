import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class OneToOneHashManager implements HashManager {
    private Map<LoginInstance, List<Byte>> userToHash;
    private Map<List<Byte>, LoginInstance> hashToUser;
    private Map<List<Byte>, ScheduledFuture<?>> hashToTask;
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

    public void shutdown() {
        //doesn't matter to interrupt or not, as even in interrupt GC will collect the maps
        hashToTask.forEach((hash, future)->future.cancel(false));
    }

    public byte[] generateHash(String username, byte[] ip) {
        byte[] primitiveHash = new byte[256];
        random.nextBytes(primitiveHash);
        List<Byte> hash = Arrays.asList(ArrayUtils.toObject(primitiveHash));

        LoginInstance instance = new LoginInstance(username, ip.clone());
        userToHash.put(instance, hash);
        hashToUser.put(hash, instance);
        hashToTask.put(hash,
                timerKeeper.schedule(new RemoveHash(userToHash, hashToUser, hashToTask, instance),
                        1, TimeUnit.MINUTES));
        return primitiveHash.clone();
    }

    public void prolongHash(String username, byte[] ip, byte[] primitiveHash)
    {
        if (primitiveHash.length != 256)
        {
            throw new IllegalArgumentException("Passed byte array is of incorrect size");
        }

        List<Byte> hash = Arrays.asList(ArrayUtils.toObject(primitiveHash));
        List<Byte> storedHash = userToHash.get(new LoginInstance(username, ip));
        if (!storedHash.equals(hash))
        {
            throw new IllegalArgumentException("The hash doesn't correspond to stored hash for the username");
        }

        hashToTask.get(hash).cancel(false);
        hashToTask.put(hash,
                timerKeeper.schedule(new RemoveHash(userToHash, hashToUser, hashToTask, new LoginInstance(username, ip)),
                        1, TimeUnit.MINUTES));
    }

    public LoginState isLoggedIn(String username, byte[] ip, byte[] primitiveHash) {
        List<Byte> hash = Arrays.asList(ArrayUtils.toObject(primitiveHash));
        List<Byte> storedHash = userToHash.get(new LoginInstance(username, ip));
        if (storedHash == null) {
            return LoginState.NO_HASH;
        }
        if (!storedHash.equals(hash)) {
            return LoginState.HASH_MISMATCH;
        }
        return LoginState.CORRECT_HASH;
    }

    public int expirationPeriodMinutes()
    {
        return waitTimeMinutes;
    }
}
