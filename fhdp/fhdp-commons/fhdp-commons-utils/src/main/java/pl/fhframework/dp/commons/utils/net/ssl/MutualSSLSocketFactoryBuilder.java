package pl.fhframework.dp.commons.utils.net.ssl;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Builder do tworzenia fabryki SSLSocketFactory obsługującej 'mutual ssl'
 *
 * Komunikacja client <-> server autoryzowana dwustronnie na podstawie ceryfikatów
 *
 * @author PT
 * @version :  $, :  $
 * @created 07/10/2019
 */
public class MutualSSLSocketFactoryBuilder {

    private Logger log = LoggerFactory.getLogger(MutualSSLSocketFactoryBuilder.class);

    private String alias = null;
    private String keyStore = null;
    private String trustStore = null;
    private String keyStorePass = null;
    private String trustStorePass = null;
    private String keyStoreType = null;
    
    /**
     * Utworzenie klasy odpowiedizalnej za przygotowaneie SSLSocktFactory
     *
     * @param alias - alias certyfikatu klienta
     * @param keyStore - keyStore JKS z certyfikatem klienta zakodowany Base64
     * @param keyStorePass - hasło do keystora z certyfikatem klienta
     * @param trustStore - keyStore JKS z certyfikatem serwera zakodowany Base64
     * @param trustStorePass - hasło do keystora z certyfikatem serwera
     */
    public MutualSSLSocketFactoryBuilder(String alias, String keyStoreType, String keyStore, String keyStorePass, String trustStore, String trustStorePass) {
        if (alias == null) {
            throw new IllegalArgumentException("The alias may not be null");
        }
        this.alias = alias;
        this.keyStore = keyStore;
        this.trustStore = trustStore;
        this.keyStorePass = keyStorePass;
        this.trustStorePass = trustStorePass;
        this.keyStoreType = keyStoreType;
    }

    public SSLSocketFactory getSSLSocketFactory() throws IOException, GeneralSecurityException {

        KeyManager[] keyManagers = getKeyManagers();
        TrustManager[] trustManagers = getTrustManagers();

        //For each key manager, check if it is a X509KeyManager (because we will override its       //functionality
        for (int i = 0; i < keyManagers.length; i++) {
            log.debug("###### keyManager: {}", keyManagers[i].getClass().getCanonicalName());
            if (keyManagers[i] instanceof X509KeyManager) {
                AliasSelectorKeyManager nkm = new AliasSelectorKeyManager((X509KeyManager) keyManagers[i], alias);
                keyManagers[i] = nkm;
                log.debug("###### replacing keyManager");
            }
        }

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagers, trustManagers, new SecureRandom());

        SSLSocketFactory ssf = context.getSocketFactory();
        return ssf;
    }

    private KeyManager[] getKeyManagers()
            throws IOException, GeneralSecurityException {

        //Init a key store with the given file.

        String alg = KeyManagerFactory.getDefaultAlgorithm();
        log.debug("#####KeyManagerFactory.getDefaultAlgorithm: {}",  alg);
        KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);

        InputStream isKeyStore = new ByteArrayInputStream(Base64.decodeBase64(keyStore));
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(isKeyStore, keyStorePass.toCharArray());
        isKeyStore.close();

        //Init the key manager factory with the loaded key store
        kmFact.init(ks, keyStorePass.toCharArray());

        KeyManager[] kms = kmFact.getKeyManagers();
        return kms;
    }

    protected TrustManager[] getTrustManagers() throws IOException, GeneralSecurityException {

        String alg = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmFact = TrustManagerFactory.getInstance(alg);

        InputStream isTrustStore = new ByteArrayInputStream(Base64.decodeBase64(trustStore));
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(isTrustStore, trustStorePass.toCharArray());
        isTrustStore.close();

        tmFact.init(ks);

        TrustManager[] tms = tmFact.getTrustManagers();
        return tms;
    }
}
