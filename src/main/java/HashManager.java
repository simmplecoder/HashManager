public interface HashManager {
    /**
     * Generate a hash for newly logged in user
     * @param username a username for which to generate hash
     * @return generated hash for the user
     */
    byte[] generateHash(String username);

    /**
     * Prolong lifetime of a hash. Used if user shows any
     * signs of activity
     * @param username the username whose hash to prolog to
     * @param hash the hash the user passed it, will be checked with stored
     */
    void prolongHash(String username, byte[] hash);

    /**
     * Checks if user is logged in, by comparing the hash passed from user
     * to hash stored here (server). If hashes don't match or
     * @param username
     * @param hash
     * @return
     */
    boolean isLoggedIn(String username, byte[] hash);

    /**
     * @return maximum amount of time the hash will be stored if user is inactive
     */
    int expirationPeriodMinutes();

    /**
     * gracefully shuts down the timers, so it won't hang the shutdown process
     */
    void shutdown();
}
