/*
 * BlindHostnameVerifier.java
 * 
 * Prawa autorskie do oprogramowania i jego kodów źródłowych 
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 * 
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */
package pl.fhframework.dp.commons.utils.net;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * BlindHostnameVerifier
 *
 * @author <a href="mailto:pawelk@skg.pl">Pawel Kasprzak</a>
 * @version $Revision: 55391 $, $Date: 2015-11-16 10:13:15 +0100 (Mon, 16 Nov 2015) $
 */
public class BlindHostnameVerifier implements HostnameVerifier {

    public boolean verify(String arg0, SSLSession arg1) {
        return true;
    }
}
