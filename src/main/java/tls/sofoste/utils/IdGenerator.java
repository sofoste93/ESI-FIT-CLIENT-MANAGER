package tls.sofoste.utils;

import java.util.Random;

public class IdGenerator {
    private static final Random random = new Random();
    public static synchronized String generateId() {
        int id = 1 + random.nextInt(1000); // Random ID in range [1, 1000]
        return Integer.toString(id);
    }
}