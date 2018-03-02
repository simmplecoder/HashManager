import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public class RemoveHash implements Runnable {
    private Map<LoginInstance, List<Byte>> userToHash;
    private Map<List<Byte>, LoginInstance> hashToUser;
    private Map<List<Byte>, ScheduledFuture<?>> hashToTask;
    private LoginInstance instance;

    public RemoveHash(Map<LoginInstance, List<Byte>> userToHash, Map<List<Byte>, LoginInstance> hashToUser,
                      Map<List<Byte>, ScheduledFuture<?>> hashToTask, LoginInstance instance) {
        this.userToHash = userToHash;
        this.hashToUser = hashToUser;
        this.hashToTask = hashToTask;
        this.instance = instance;
    }

    @Override
    public void run() {
        List<Byte> hash = userToHash.remove(instance);
        hashToUser.remove(hash);
        hashToTask.remove(hash);
    }
}
