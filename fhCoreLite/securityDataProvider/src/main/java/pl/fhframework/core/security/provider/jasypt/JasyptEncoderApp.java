package pl.fhframework.core.security.provider.jasypt;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Tomasz.Kozlowski (created on 14.02.2019)
 */
public class JasyptEncoderApp {

    public static void main(String[] args) throws IOException {
        // load encryptor properties
        loadProperties();

        // configure encryptor component
        StringEncryptor encryptor = buildEncryptor();

        // encrypt text passed as a first argument
        if (args != null && args.length > 0) {
            String encrypted = encryptor.encrypt(args[0]);
            log(encrypted);
        } else {
            log("There is no any text to encrypt");
        }
    }

    private static void loadProperties() throws IOException {
        String filename = "jasypt.properties";
        try (InputStream input = JasyptEncoderApp.class.getClassLoader().getResourceAsStream(filename)) {
            if (input == null) {
                throw new IllegalStateException("Cannot find " + filename + " file");
            }
            System.getProperties().load(input);
        }
    }

    private static StringEncryptor buildEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(System.getProperty("jasypt.encryptor.password"));
        config.setAlgorithm(System.getProperty("jasypt.encryptor.algorithm"));
        config.setPoolSize(System.getProperty("jasypt.encryptor.pool-size"));
        config.setProviderName(System.getProperty("jasypt.encryptor.provider"));
        config.setSaltGeneratorClassName(System.getProperty("jasypt.encryptor.salt-generator"));
        config.setStringOutputType(System.getProperty("jasypt.encryptor.output-type"));
        encryptor.setConfig(config);
        return encryptor;
    }

    private static void log(String message) {
        System.out.println("\n" + message + "\n");
    }

}
