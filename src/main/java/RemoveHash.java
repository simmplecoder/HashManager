import java.util.Map;

public class RemoveHash implements Runnable {
    private Map<LoginInstance, byte[]> userToHash;
    private Map<byte[], LoginInstance> hashToUser;
    private LoginInstance instance;

    public RemoveHash(Map<LoginInstance, byte[]> userToHash, Map<byte[], LoginInstance> hashToUser, LoginInstance instance) {
        this.userToHash = userToHash;
        this.hashToUser = hashToUser;
        this.instance = instance;
    }

    @Override
    public void run() {
        byte[] hash = userToHash.remove(instance);
        hashToUser.remove(hash);
    }
}
