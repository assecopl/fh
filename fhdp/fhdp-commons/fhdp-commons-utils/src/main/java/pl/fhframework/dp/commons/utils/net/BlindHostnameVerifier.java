package pl.fhframework.dp.commons.utils.net;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class BlindHostnameVerifier implements HostnameVerifier {

    public boolean verify(String arg0, SSLSession arg1) {
        return true;
    }
}
