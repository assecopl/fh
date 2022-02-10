package pl.fhframework.dp.commons.utils.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

/**
 * User: phojnacki Date: 27.01.15 Time: 09:28
 */
public class SSLUtil {
    static final String KEYSTORE_LOCATION = "ssl.keystoreLocation";
    static final String KEYSTORE_PASS = "ssl.keystorePassword";
    static final String KEYSTORE_TYPE = "ssl.keystoreType";

    static final String TRUSTSTORE_LOCATION = "ssl.truststoreLocation";
    static final String TRUSTSTORE_PASS = "ssl.truststorePassword";
    static final String TRUSTSTORE_TYPE = "ssl.truststoreType";

    private static final Logger log = LoggerFactory.getLogger(SSLUtil.class);

    private final static HostnameVerifier hostVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    };

    private static SSLSocketFactory sslSocketFactory;

    public static Map<String, Object> setupSSLSocketFactoryParams() throws Exception {
        Map<String, Object> sslParams = new HashMap<>();
        HttpsURLConnection.setDefaultHostnameVerifier(SSLUtil.getHostVerifier());
        HttpsURLConnection.setDefaultSSLSocketFactory(SSLUtil.getSSLFactory());
        sslParams.put("com.sun.xml.ws.transport.https.client.hostname.verifier", SSLUtil.getHostVerifier());
        sslParams.put("com.sun.xml.ws.transport.https.client.SSLSocketFactory", SSLUtil.getSSLFactory());
        sslParams.put("com.sun.xml.internal.ws.transport.https.client.hostname.verifier", SSLUtil.getHostVerifier());
        sslParams.put("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory", SSLUtil.getSSLFactory());
        
        return sslParams;

    }

    private static HostnameVerifier getHostVerifier() {
        return hostVerifier;
    }

    private static SSLSocketFactory getSSLFactory() throws Exception {
//        if (sslSocketFactory == null) {
//            return sslSocketFactory;
//        }
        KeyManagerFactory keyManagerFactory = getKeyManagerFactory();
        TrustManagerFactory trustManagerFactory = getTrustManagerFactory();
        TrustManager[] blindTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] c, String a) {
            }

            public void checkServerTrusted(X509Certificate[] c, String a) {
            }
        }};

        SSLContext context = SSLContext.getInstance("SSL");
        context.init(keyManagerFactory.getKeyManagers(), blindTrustManagers,
                new SecureRandom());

        sslSocketFactory = context.getSocketFactory();
        return sslSocketFactory;
    }

    private static TrustManagerFactory getTrustManagerFactory() throws Exception {
        TrustManagerFactory trustManagerFactory;
        trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        KeyStore trustStore = getTrustStore();

        trustManagerFactory.init(trustStore);
        return trustManagerFactory;
    }

    public static KeyStore getTrustStore() throws KeyStoreException, URISyntaxException, IOException, NoSuchAlgorithmException, CertificateException {
        char[] pass = getSslConfigurationProperty(TRUSTSTORE_PASS) != null ? getSslConfigurationProperty(TRUSTSTORE_PASS).toCharArray() : null;
        KeyStore trustStore = KeyStore.getInstance(getSslConfigurationProperty(TRUSTSTORE_TYPE));

        String storeFile = getSslConfigurationProperty(TRUSTSTORE_LOCATION);

        InputStream trustInput = new FileInputStream(new File(storeFile));
        trustStore.load(trustInput, pass);
        trustInput.close();
        trustInput = null;
        return trustStore;
    }

    private static KeyManagerFactory getKeyManagerFactory() throws Exception {
        KeyManagerFactory keyManagerFactory;
        keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        char[] pass = getSslConfigurationProperty(KEYSTORE_PASS) != null ? getSslConfigurationProperty(KEYSTORE_PASS).toCharArray() : null;

        keyManagerFactory.init(getKeyStore(), pass);
        return keyManagerFactory;
    }

    public static KeyStore getKeyStore() throws KeyStoreException, URISyntaxException, IOException, NoSuchAlgorithmException, CertificateException {
        char[] pass = getSslConfigurationProperty(KEYSTORE_PASS) != null ? getSslConfigurationProperty(KEYSTORE_PASS).toCharArray() : null;
        KeyStore keyStore = KeyStore.getInstance(getSslConfigurationProperty(KEYSTORE_TYPE));

        String storeFile = getSslConfigurationProperty(KEYSTORE_LOCATION);

        InputStream keyInput = new FileInputStream(new File(storeFile));
        keyStore.load(keyInput, pass);
        keyInput.close();
        keyInput = null;
        return keyStore;
    }

    public static final String getSslConfigurationProperty(String propCode) {
        String property = null;
        property = System.getProperty(propCode);
        if (property == null || property.trim().length() == 0) {
            log.error("Property " + propCode + " is missing");
        }
        return property;
    }

    public static KeyStore createKeyStore(String keystoreLocation, String keystoreType, String pass) throws Exception {

        char[] passCharArr = pass != null ? pass.toCharArray() : null;

        KeyManagerFactory keyManagerFactory;
        keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

        KeyStore keyStore;
        keyStore = KeyStore.getInstance(keystoreType);//PKCS12 JKS

        String storeFile = keystoreLocation;

        InputStream keyInput = new FileInputStream(new File(new URL(storeFile).toURI()));
        keyStore.load(keyInput, passCharArr);
        keyInput.close();
        keyInput = null;

        keyManagerFactory.init(keyStore, passCharArr);
        return keyStore;
    }

}
