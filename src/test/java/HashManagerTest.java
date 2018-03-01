import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.util.concurrent.TimeUnit;

public class HashManagerTest {
    //the timers tend to hang the tests, so setup a timeout
    //make sure to set this to maximum possible execution time of a test
    @Rule
    public Timeout globalTimeout = new Timeout(2, TimeUnit.MINUTES);

    @Test
    public void correctExpirationTest()
    {
        byte[] darkhanIP = {(byte) 192, (byte) 168, 1, 1};
        byte[] olzhasIP = {(byte) 192, (byte) 168, 1, 2};

        HashManager hashManager = new OneToOneHashManager(1);
        byte[] darkhan = hashManager.generateHash("Darkhan", darkhanIP);
        byte[] olzhas = hashManager.generateHash("Olzhas", olzhasIP);
        int expirationPeriodMillis = hashManager.expirationPeriodMinutes() * 60 * 1000;

        try {
            Thread.sleep(expirationPeriodMillis / 2);

            Assert.assertTrue("Darkhan's hash expired too early",
                              hashManager.isLoggedIn("Darkhan", darkhanIP, darkhan) == HashManager.LoginState.CORRECT_HASH);
            Assert.assertTrue("Olzhas's hash expired too early",
                              hashManager.isLoggedIn("Olzhas", olzhasIP, olzhas) == HashManager.LoginState.CORRECT_HASH);

            hashManager.prolongHash("Darkhan", darkhanIP, darkhan);
            hashManager.prolongHash("Olzhas", olzhasIP, olzhas);

            //some effort to avoid spurious wakeup
            Thread.sleep(expirationPeriodMillis + 100);

            Assert.assertTrue("Darkhan's hash didn't expire in time",
                                hashManager.isLoggedIn("Darkhan", darkhanIP, darkhan) == HashManager.LoginState.NO_HASH);
            Assert.assertTrue("Olzhas's hash didn't expire in time",
                                hashManager.isLoggedIn("Olzhas", olzhasIP, olzhas) == HashManager.LoginState.NO_HASH);

            hashManager.shutdown();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
