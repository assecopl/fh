package pl.fhframework.dp.commons.utils.net.ssl;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Builder do tworzenia fabryki SSLSocketFactory obsługującej 'mutual ssl'
 *
 * Komunikacja client <-> server autoryzowana dwustronnie na podstawie ceryfikatów
 *
 * @author PT
 * @version :  $, :  $
 * @created 07/10/2019
 */
public class MutualSSLSocketFactoryBuilderFiles {

    private String alias = null;
    private String keyStore = null;
    private String trustStore = null;
    private String keyStorePass = null;
    private String trustStorePass = null;
    private String keyStoreType = null;

    public MutualSSLSocketFactoryBuilderFiles(String alias, String keyStoreType, String keyStore, String keyStorePass, String trustStore, String trustStorePass) {
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
            System.out.println("###### keyManager: " + keyManagers[i].getClass().getCanonicalName());
            if (keyManagers[i] instanceof X509KeyManager) {
                AliasSelectorKeyManager nkm = new AliasSelectorKeyManager((X509KeyManager) keyManagers[i], alias);
                keyManagers[i] = nkm;
                System.out.println("###### replacing keyManager");
            }
        }

        SSLContext def = SSLContext.getDefault();
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(keyManagers, trustManagers, null);

        SSLSocketFactory ssf = context.getSocketFactory();
        return ssf;
    }

    private KeyManager[] getKeyManagers()
            throws IOException, GeneralSecurityException {

        //Init a key store with the given file.

        String alg = KeyManagerFactory.getDefaultAlgorithm();
        System.out.println("#####KeyManagerFactory.getDefaultAlgorithm: " + alg);
        KeyManagerFactory kmFact = KeyManagerFactory.getInstance(alg);

        InputStream isKeyStore = new FileInputStream(keyStore);
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

        InputStream isTrustStore = new FileInputStream(trustStore);
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(isTrustStore, trustStorePass.toCharArray());
        isTrustStore.close();

        tmFact.init(ks);

        TrustManager[] tms = tmFact.getTrustManagers();
        return tms;
    }
}
