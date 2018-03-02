import java.util.Arrays;

public class LoginInstance {
    private String username;
    private byte[] ip;

    public LoginInstance(String username, byte[] ip) {
        if (ip.length != 4) {
            throw new IllegalArgumentException("IP Address length should be 4 bytes");
        }

        this.username = username;
        this.ip = ip;
    }

    @Override
    public int hashCode() {
        return username.hashCode() * 31 + Arrays.hashCode(ip);
    }

    @Override
    public boolean equals(Object o) {
        LoginInstance obj = (LoginInstance) o;
        return username.equals(obj.username) && Arrays.equals(ip, obj.ip);
    }
}
