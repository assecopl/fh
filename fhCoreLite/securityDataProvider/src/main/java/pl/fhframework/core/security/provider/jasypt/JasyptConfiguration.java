package pl.fhframework.core.security.provider.jasypt;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Tomasz.Kozlowski (created on 14.02.2019)
 */
@Configuration
@PropertySource("classpath:jasypt.properties")
public class JasyptConfiguration {

    @Value("${jasypt.encryptor.password}")
    private String password;
    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;
    @Value("${jasypt.encryptor.provider}")
    private String provider;
    @Value("${jasypt.encryptor.pool-size}")
    private String poolSize;
    @Value("${jasypt.encryptor.salt-generator}")
    private String saltGenerator;
    @Value("${jasypt.encryptor.output-type}")
    private String outputType;

    @Primary
    @Bean(name = "stringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(password);
        config.setAlgorithm(algorithm);
        config.setPoolSize(poolSize);
        config.setProviderName(provider);
        config.setSaltGeneratorClassName(saltGenerator);
        config.setStringOutputType(outputType);
        encryptor.setConfig(config);
        return encryptor;
    }

}
