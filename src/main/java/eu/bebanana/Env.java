package eu.bebanana;

import java.util.Optional;

public class Env {

    public Env() {

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
