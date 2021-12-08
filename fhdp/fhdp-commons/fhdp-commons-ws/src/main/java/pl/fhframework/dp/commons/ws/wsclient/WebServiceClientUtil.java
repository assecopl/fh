package pl.fhframework.dp.commons.ws.wsclient;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ext.logging.AbstractLoggingInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.WSAddressingFeature;
import org.apache.cxf.ws.policy.WSPolicyFeature;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.slf4j.LoggerFactory;
import pl.fhframework.dp.commons.base.model.AuthorizationTypeEnum;
import pl.fhframework.dp.commons.base.model.IEndpointCfg;
import pl.fhframework.dp.commons.utils.net.BlindSSLSocketFactory;

import javax.net.ssl.SSLSocketFactory;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.CLIENT_ADDRESSING_PROPERTIES;

/**
 * The class that creates the webservice client from the generated Port class from WSDL.
 * <p>
 * - address (url)
 * - timeouts
 * - request
 * - connect
 * - authorization:
 * BASIC,
 * WS-SEC + WS-ADDR,
 * - ssl
 * f.e..:BlindSSLSocketFactory
 * <p>
 * Example:
 * <p>
 * pdrOnDemandService = new WebServiceClientUtil(1000, 60000, new WebServiceClientUtil.IConfigInterceptors() {
 * @Override public void addInterceptor(Client client) {
 * client.getInInterceptors().add(new LoggingInInterceptor());
 * client.getOutInterceptors().add(new LoggingOutInterceptor());
 * <p>
 * }
 * }, null).configureWsaPort(PdrOnDemandWebService.class, "https://pdr.skg.pl/pdr-web/services/OnDemandPort", "skg", null, "skg", "sasasa", AuthorizationTypeEnum.Digest, true);
 * @created 17/12/2013
 */
public class WebServiceClientUtil {

    public static final String CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout";
    public static final String REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";

    long conectTimeout; 
    long requestTimeout; 
    private IConfigWebServiceClient configWebServiceClient;
    private IConfigInterceptors configInterceptors;
    private SSLSocketFactory customSSLSocketFactory;

    public long conectTimeoutFromSystem() {
        return Long.parseLong(System.getProperty("japis.ws.connect.timeout", "1000"));
    }

    public long requestTimeoutFromSystem() {
        return Long.parseLong(System.getProperty("japis.ws.request.timeout", "60000"));
    }

    public WebServiceClientUtil() {
        conectTimeout = conectTimeoutFromSystem();
        requestTimeout = requestTimeoutFromSystem();
    }

    public WebServiceClientUtil(IConfigInterceptors configInterceptors, SSLSocketFactory customSSLSocketFactory) {
        this();
        this.configInterceptors = configInterceptors;
        this.customSSLSocketFactory = customSSLSocketFactory;
    }

    public WebServiceClientUtil(IConfigWebServiceClient configWebServiceClient, IConfigInterceptors configInterceptors, SSLSocketFactory customSSLSocketFactory) {
        this();
        this.configWebServiceClient = configWebServiceClient;
        this.configInterceptors = configInterceptors;
        this.customSSLSocketFactory = customSSLSocketFactory;
    }

    /**
     * @param conectTimeout_ms  - 
     * @param requestTimeout_ms - 
     */
    public WebServiceClientUtil(long conectTimeout_ms, long requestTimeout_ms, IConfigInterceptors configInterceptors, SSLSocketFactory customSSLSocketFactory) {
        this.conectTimeout = conectTimeout_ms;
        this.requestTimeout = requestTimeout_ms;
        this.configInterceptors = configInterceptors;
        this.customSSLSocketFactory = customSSLSocketFactory;
    }

    public <E> E configureBasicPort(Class<E> serviceCalzz, String address, String user, String password,
                                    AuthorizationTypeEnum authType, boolean logOn) {
        return configureBasicPort(serviceCalzz, address, user, password, authType, logOn, null,
                null, null, null, null);
    }

    public <E> E configureBasicPort(Class<E> serviceCalzz, String address, String user, String password,
                                    AuthorizationTypeEnum authType, boolean logOn, Map<String, List<String>> headers) {
        return configureBasicPort(serviceCalzz, address, user, password, authType, logOn, headers, null, null, null, null);
    }

    public <E> E configureBasicPort(Class<E> serviceCalzz, String address, String user, String password,
                                    AuthorizationTypeEnum authType, boolean logOn, Map<String, List<String>> headers,
                                    String proxyHost, Integer proxyPort, String proxyUser, String proxyPassword) {
        return (E) Proxy.newProxyInstance(serviceCalzz.getClassLoader(), new Class[]{serviceCalzz}, (proxy, method, args) -> {
            E basicPort;
            long time = System.nanoTime();
            try {
                basicPort = createBasicPort(serviceCalzz, address, user, password,
                        authType, logOn, headers,
                        proxyHost, proxyPort, proxyUser, proxyPassword);
            } finally {
                BigDecimal duration = new BigDecimal((System.nanoTime() - time) / (1000.0 * 1000 * 1000)).setScale(3, RoundingMode.HALF_UP);
                LoggerFactory.getLogger(WebServiceClientUtil.class).debug("createWsaPort dla {}/{}: {}[s]", serviceCalzz.getSimpleName(), method.getName(), duration);
                if(duration.compareTo(BigDecimal.ONE) > 0) {
                    LoggerFactory.getLogger(WebServiceClientUtil.class).warn("createWsaPort for {}/{} took {}[s]!", serviceCalzz.getSimpleName(), method.getName(), duration);
                }
            }
            return method.invoke(basicPort, args);
        });
    }

    protected <E> E createBasicPort(Class<E> serviceCalzz, String address, String user, String password,
                                    AuthorizationTypeEnum authType, boolean logOn, Map<String, List<String>> headers,
                                    String proxyHost, Integer proxyPort, String proxyUser, String proxyPassword) {

        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(serviceCalzz);

        factory.setAddress(address);

        List features = new ArrayList<>(factory.getFeatures());

        WSPolicyFeature wsPolicyFeature = new WSPolicyFeature();
        wsPolicyFeature.setEnabled(true);
        wsPolicyFeature.setIgnoreUnknownAssertions(true);
        features.add(wsPolicyFeature);

        factory.setFeatures(features);

        E port = (E) factory.create();

        Client client = ClientProxy.getClient(port);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        if (authType != null) {
            AuthorizationPolicy authPolicy = new AuthorizationPolicy();
            authPolicy.setAuthorizationType(authType.name());
            authPolicy.setUserName(user);
            authPolicy.setPassword(password);
            http.setAuthorization(authPolicy);
        }

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(conectTimeout);
        httpClientPolicy.setReceiveTimeout(requestTimeout);

        if (StringUtils.isNotBlank(proxyHost) && proxyPort != null) {
            httpClientPolicy.setProxyServerType(ProxyServerType.HTTP);
            httpClientPolicy.setProxyServer(proxyHost);
            httpClientPolicy.setProxyServerPort(proxyPort);
        }

        http.setClient(httpClientPolicy);

        Map<String, Object> requestContext = client.getRequestContext();
        requestContext.put(CONNECT_TIMEOUT, httpClientPolicy.getConnectionTimeout());
        requestContext.put(REQUEST_TIMEOUT, httpClientPolicy.getReceiveTimeout()); // Timeout in millis

        // Creating HTTP headers
//        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        // Add HTTP headers to the web service request
        if (headers != null) {
            requestContext.put(Message.PROTOCOL_HEADERS, headers);
        }

        if (address.startsWith("https") || customSSLSocketFactory != null) {

            HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

            if (customSSLSocketFactory == null) {
                customSSLSocketFactory = new BlindSSLSocketFactory();
            }
            TLSClientParameters tlsCP = new TLSClientParameters();
            // you can set up the SSLSocketFactory on the TLSClientParameter
            tlsCP.setSSLSocketFactory(customSSLSocketFactory);
            tlsCP.setDisableCNCheck(true);

            // setup the TLSClientParameters on the HttpConduit
            httpConduit.setTlsClientParameters(tlsCP);
        }

        if (configInterceptors != null) {
            configInterceptors.addInterceptor(client);
        }
        if (!logOn) {
            removeLoggingInterceptors(client);
        }
        if (configWebServiceClient!=null) {
            configWebServiceClient.configPort(port);
        }

        return port;
    }

    public <E> E configureWsaPort(Class<E> serviceCalzz, String address, String from, String to, String login, String password) {
        return configureWsaPort(serviceCalzz, address, from, to, login, password, AuthorizationTypeEnum.Digest, false, false, null);
    }

    public <E> E configureWsaPort(Class<E> serviceCalzz, String address, String from, String to, String login, String password, boolean logOut) {
        return configureWsaPort(serviceCalzz, address, from, to, login, password, AuthorizationTypeEnum.Digest, false, logOut, null);
    }

    public <E> E configurePort(Class<E> serviceCalzz, IEndpointCfg endpointCfg) {
        return configurePort(serviceCalzz, endpointCfg, null);
    }

    public <E> E configurePort(Class<E> serviceCalzz, IEndpointCfg endpointCfg, Map<String, List<String>> headers) {

        if (endpointCfg.getConnectTimeout() != null) {
            conectTimeout = endpointCfg.getConnectTimeout().longValue();
        }
        if (endpointCfg.getRequestTimeout() != null) {
            requestTimeout = endpointCfg.getRequestTimeout().longValue();
        }

        if (StringUtils.isNotBlank(endpointCfg.getWsaFrom())) {
            return configureWsaPort(serviceCalzz, endpointCfg.getAddress(), endpointCfg.getWsaFrom(), endpointCfg.getWsaTo(),
                    endpointCfg.getUser(), endpointCfg.getPassword(), AuthorizationTypeEnum.valueOf(endpointCfg.getAuthType()),
                    endpointCfg.getWithNonce() != null ? endpointCfg.getWithNonce() : false, endpointCfg.getLogOn(), headers,
                    endpointCfg.getProxyHost(), endpointCfg.getProxyPort(), endpointCfg.getProxyUser(), endpointCfg.getProxyPassword());
        } else {
            return configureBasicPort(serviceCalzz, endpointCfg.getAddress(), endpointCfg.getUser(), endpointCfg.getPassword(),
                    AuthorizationTypeEnum.valueOf(endpointCfg.getAuthType()), endpointCfg.getLogOn(), headers,
                    endpointCfg.getProxyHost(), endpointCfg.getProxyPort(), endpointCfg.getProxyUser(), endpointCfg.getProxyPassword()
            );
        }
    }

    public <E> E configureWsaPort(Class<E> serviceCalzz, String address, String from, String to, String login, String password,
                                  AuthorizationTypeEnum authType, boolean withNonce, boolean logOn, Map<String, List<String>> header) {
        return configureWsaPort(serviceCalzz, address, from, to, login, password, authType, withNonce, logOn, header, null, null, null, null);
    }

    public <E> E configureWsaPort(Class<E> serviceCalzz, String address, String from, String to, String login, String password,
                                  AuthorizationTypeEnum authType, boolean withNonce, boolean logOn, Map<String, List<String>> headers,
                                  String proxyHost, Integer proxyPort, String proxyUser, String proxyPassword) {
        return (E) Proxy.newProxyInstance(serviceCalzz.getClassLoader(), new Class[]{serviceCalzz}, (proxy, method, args) -> {
            E wsaPort;
            long time = System.nanoTime();
            try {
                wsaPort = createWsaPort(serviceCalzz, address, from, to, login, password, authType, withNonce, logOn, headers, proxyHost, proxyPort, proxyUser, proxyPassword);
            } finally {
                BigDecimal duration = new BigDecimal((System.nanoTime() - time) / (1000.0 * 1000 * 1000)).setScale(3, RoundingMode.HALF_UP);
                LoggerFactory.getLogger(WebServiceClientUtil.class).debug("createWsaPort dla {}/{}: {}[s]", serviceCalzz.getSimpleName(), method.getName(), duration);
            }
            return method.invoke(wsaPort, args);
        });
    }

    private <E> E createWsaPort(Class<E> serviceCalzz, String address, String from, String to, String login, String password,
                                AuthorizationTypeEnum authType, boolean withNonce, boolean logOn, Map<String, List<String>> headers,
                                String proxyHost, Integer proxyPort, String proxyUser, String proxyPassword) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(serviceCalzz);
        //ustalenie adresu wywołania
        factory.setAddress(address);

        List features = new ArrayList<>(factory.getFeatures());

        // WS-ADDRESSING
        WSAddressingFeature wsAddressingFeature = new WSAddressingFeature();
        wsAddressingFeature.setAddressingRequired(false);
        features.add(wsAddressingFeature);

        WSPolicyFeature wsPolicyFeature = new WSPolicyFeature();
        wsPolicyFeature.setIgnoreUnknownAssertions(true);
        features.add(wsPolicyFeature);

        factory.setFeatures(features);
//        for (Feature f : factory.getFeatures()) {
//        }

        E port = (E) factory.create();
        Client client = ClientProxy.getClient(port);
        HTTPConduit http = (HTTPConduit) client.getConduit();

//        if (address.startsWith("https")) {
//            if (customSSLSocketFactory == null) {
//                customSSLSocketFactory = new BlindSSLSocketFactory();
//            }
//            TLSClientParameters tlsCP = new TLSClientParameters();
//            // you can set up the SSLSocketFactory on the TLSClientParameter
//            tlsCP.setSSLSocketFactory(customSSLSocketFactory);
//
//            // setup the TLSClientParameters on the HttpConduit
//            http.setTlsClientParameters(tlsCP);
//        }

        Map<String, Object> requestContext = client.getRequestContext();

        // get Message Addressing Properties instance
        AddressingProperties maps = new AddressingProperties();

        // set To property
        if (!StringUtils.isEmpty(to)) {
            AttributedURIType valueTo = new AttributedURIType();
            valueTo.setValue(to);
            maps.setTo(valueTo);
        }

        // set From property
        if (!StringUtils.isEmpty(from)) {
            EndpointReferenceType refFrom = new EndpointReferenceType();
            AttributedURIType valueFrom = new AttributedURIType();
            valueFrom.setValue(from);
            refFrom.setAddress(valueFrom);
            maps.setFrom(refFrom);
        }

        //wyzerowanie
        maps.setReplyTo(new EndpointReferenceType());
        maps.setFaultTo(new EndpointReferenceType());
        //        maps.setFrom(new EndpointReferenceType());

        // associate MAPs with request context
        requestContext.put(CLIENT_ADDRESSING_PROPERTIES, maps);

        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(conectTimeout);
        httpClientPolicy.setReceiveTimeout(requestTimeout);

        if (StringUtils.isNotBlank(proxyHost) && proxyPort != null) {
            httpClientPolicy.setProxyServerType(ProxyServerType.HTTP);
            httpClientPolicy.setProxyServer(proxyHost);
            httpClientPolicy.setProxyServerPort(proxyPort);
        }

        http.setClient(httpClientPolicy);

        requestContext.put(CONNECT_TIMEOUT, httpClientPolicy.getConnectionTimeout());
        requestContext.put(REQUEST_TIMEOUT, httpClientPolicy.getReceiveTimeout()); // Timeout in millis

        // WS-SECURITY
        Map outProps = new HashMap();
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        outProps.put(WSHandlerConstants.USER, login);
        if (AuthorizationTypeEnum.Digest.equals(authType)) {
            outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_DIGEST);
        } else {
            outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        }

        // Automatically adds a Base64 encoded message nonce and a created timestamp
        if (withNonce) {
//            outProps.put(WSHandlerConstants.ADD_UT_ELEMENTS, WSConstants.NONCE_LN + " " + WSConstants.CREATED_LN);
            outProps.put(WSHandlerConstants.ADD_USERNAMETOKEN_NONCE, "true");
            outProps.put(WSHandlerConstants.ADD_USERNAMETOKEN_CREATED, "true");
        }

        outProps.put(WSHandlerConstants.PW_CALLBACK_REF, new KeystorePasswordCallback(login, password));

        WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
        client.getOutInterceptors().add(wssOut);

        if (address.startsWith("https") || customSSLSocketFactory != null) {

            HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

            if (customSSLSocketFactory == null) {
                customSSLSocketFactory = new BlindSSLSocketFactory();
            }
            TLSClientParameters tlsCP = new TLSClientParameters();
            // you can set up the SSLSocketFactory on the TLSClientParameter
            tlsCP.setSSLSocketFactory(customSSLSocketFactory);
            tlsCP.setDisableCNCheck(true);

            // setup the TLSClientParameters on the HttpConduit
            httpConduit.setTlsClientParameters(tlsCP);
        }

        if (configInterceptors != null) {
            configInterceptors.addInterceptor(client);
        }
        if (!logOn) {
            removeLoggingInterceptors(client);
        }

        if (configWebServiceClient!=null) {
            configWebServiceClient.configPort(port);
        }

        return port;
    }

    private void removeLoggingInterceptors(Client client) {
        client.getInInterceptors().removeIf(AbstractLoggingInterceptor.class::isInstance);
        client.getInFaultInterceptors().removeIf(AbstractLoggingInterceptor.class::isInstance);
        client.getOutInterceptors().removeIf(AbstractLoggingInterceptor.class::isInstance);
        client.getOutFaultInterceptors().removeIf(AbstractLoggingInterceptor.class::isInstance);
    }

}
