package pl.fhframework.dp.commons.utils.net;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 20/08/2020
 */
public class HostNameUtil {
    static final String HOST_NAME_KEY = "fhdp.hostNameProperty";
    static final String JBOSS_HOST_NAME_KEY = "jboss.qualified.host.name";

    public HostNameUtil() {
    }

    public static String host() {
        return StringUtils.isNotEmpty(hostName()) ? hostName() : hostAddress();
    }

    public static String hostName() {
        return System.getProperty(System.getProperty("fhdp.hostNameProperty", "jboss.qualified.host.name"));
    }

    public static String hostAddress() {
        String hostAddress;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception var2) {
            LoggerFactory.getLogger(HostNameUtil.class).warn("Error while reading host address !", var2);
            hostAddress = "unknown";
        }

        return hostAddress;
    }
}
