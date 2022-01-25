/*
 * BlindSSLSocketFactory.java
 * 
 * Prawa autorskie do oprogramowania i jego kodów źródłowych 
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 * 
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */
package pl.fhframework.dp.commons.utils.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

/**
 * Custom SSL Socket factory class accept server certificate without verifying
 * it. Class can be used for establish secured connection to systems which use
 * self signed certificates.
 *
 * @author <a href="mailto:pawelk@skg.pl">Pawel Kasprzak</a>
 * @version $Revision: 43928 $, $Date: 2013-08-08 09:40:21 +0200 (Cz, 08 sie 2013) $
 */
public class BlindSSLSocketFactory extends SSLSocketFactory {

    private static final Logger logger = LoggerFactory.getLogger(BlindSSLSocketFactory.class);
    private static SSLSocketFactory factory;

    /**
     * Builds an all trusting "blind" ssl socket factory.
     */
    static {
        // create a trust manager that will purposefully fall down on the
        // job
        TrustManager[] blindTrustManager = new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] c, String a) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] c, String a) {
                }
            }};

        // create "blind" ssl socket factory with lazy trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, blindTrustManager, new java.security.SecureRandom());
            factory = sc.getSocketFactory();
        } catch (GeneralSecurityException e) {
            logger.error("Error creating ssl socket factory !", e);
        }
    }

    public static synchronized SSLSocketFactory getFactory() {
        return new BlindSSLSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return factory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return factory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(String arg0, int arg1) throws IOException, UnknownHostException {
        return factory.createSocket(arg0, arg1);
    }

    @Override
    public Socket createSocket(String arg0, int arg1, InetAddress arg2, int arg3) throws IOException, UnknownHostException {
        return factory.createSocket(arg0, arg1, arg2, arg3);
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1) throws IOException {
        return factory.createSocket(arg0, arg1);
    }

    @Override
    public Socket createSocket(InetAddress arg0, int arg1, InetAddress arg2, int arg3) throws IOException {
        return factory.createSocket(arg0, arg1, arg2, arg3);
    }

    @Override
    public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException {
        return factory.createSocket(arg0, arg1, arg2, arg3);
    }
}
