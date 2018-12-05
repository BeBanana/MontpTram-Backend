package eu.bebanana;

import java.util.Optional;

public class Env {

    private static Env instance;

    public final String liveDataUrl;
    public final String slackHook;
    public final String secureKeystore;
    public final String secureKeystorePassword;

    public static Env getInstance() {
        if(instance == null) {
            instance = new Env();
        }
        return instance;
    }

    private Env() {
        liveDataUrl = getEnv("LIVE_DATA_URL");
        slackHook = getEnv("SLACK_HOOK");
        secureKeystore = getEnv("SECURE_KEYSTORE");
        secureKeystorePassword = getEnv("SECURE_KEYSTORE_PASSWORD");
    }

    private String getEnv(String envName) {
        return Optional.of(System.getenv(envName)).orElseThrow(() -> new MissingEnvException(envName));
    }

    private class MissingEnvException extends RuntimeException {
        MissingEnvException(String envName) {
            super("The environment variable '" + envName + "' is missing.");
        }
    }

}
