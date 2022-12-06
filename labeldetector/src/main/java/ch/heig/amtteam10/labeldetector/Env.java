package ch.heig.amtteam10.labeldetector;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Configuration loader
 *
 * @author Nicolas Crausaz
 * @author Maxime Scharwath
 */
public class Env {
    private static Env instance;
    private final Dotenv env;

    private Env() {
        env = Dotenv.configure().ignoreIfMissing().systemProperties().load();
    }

    private static Env getInstance() {
        if (instance == null) instance = new Env();
        return instance;
    }

    public static String get(String key) {
        return getInstance().env.get(key);
    }

    public static String get(String key, String defaultValue) {
        return getInstance().env.get(key, defaultValue);
    }
}
