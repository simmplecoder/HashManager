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
        HashManager hashManager = new OneToOneHashManager(1);
        byte[] darkhan = hashManager.generateHash("Darkhan");
        byte[] olzhas = hashManager.generateHash("Olzhas");
        int expirationPeriodMillis = hashManager.expirationPeriodMinutes() * 60 * 1000;

        try {
            Thread.sleep(expirationPeriodMillis / 2);

            Assert.assertTrue("Darkhan's hash expired too early",
                              hashManager.isLoggedIn("Darkhan", darkhan));
            Assert.assertTrue("Olzhas's hash expired too early",
                              hashManager.isLoggedIn("Olzhas", olzhas));

            hashManager.prolongHash("Darkhan", darkhan);
            hashManager.prolongHash("Olzhas", olzhas);

            //some effort to avoid spurious wakeup
            Thread.sleep(expirationPeriodMillis + 100);

            Assert.assertFalse("Darkhan's hash didn't expire in time",
                                hashManager.isLoggedIn("Darkhan", darkhan));
            Assert.assertFalse("Olzhas's hash didn't expire in time",
                                hashManager.isLoggedIn("Olzhas", olzhas));

            hashManager.shutdown();


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
