import java.util.Map;

public class RemoveHash implements Runnable {
    private Map<String, byte[]> userToHash;
    private Map<byte[], String> hashToUser;
    private String username;

    public RemoveHash(Map<String, byte[]> userToHash, Map<byte[], String> hashToUser, String username) {
        this.userToHash = userToHash;
        this.hashToUser = hashToUser;
        this.username = username;
    }

    @Override
    public void run() {
        byte[] hash = userToHash.remove(username);
        hashToUser.remove(hash);
    }
}
