public interface HashManager {
    /**
     *
     */
    enum LoginState {
        NO_HASH,
        CORRECT_HASH,
        HASH_MISMATCH
    }

    /**
     * Generate a hash for newly logged in user
     * @param username a username for which to generate hash
     * @return generated hash for the user
     */
    byte[] generateHash(String username, byte[] ip);

    /**
     * Prolong lifetime of a hash. Used if user shows any
     * signs of activity
     * @param username the username whose hash to prolog to
     * @param hash the hash the user passed it, will be checked with stored
     */
    void prolongHash(String username, byte[] ip, byte[] hash);

    /**
     * Needs reconsideration. Will the communication layer provide only hash?
     * @param username
     * @param hash
     * @return
     */
    LoginState isLoggedIn(String username, byte[] ip, byte[] hash);

    /**
     * @return maximum amount of time the hash will be stored if user is inactive
     */
    int expirationPeriodMinutes();

    /**
     * gracefully shuts down the timers, so it won't hang the shutdown process
     */
    void shutdown();
}
