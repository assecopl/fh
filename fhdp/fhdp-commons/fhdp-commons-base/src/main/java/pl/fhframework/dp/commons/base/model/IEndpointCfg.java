package pl.fhframework.dp.commons.base.model;


import java.time.LocalDateTime;


public interface IEndpointCfg extends IEndpointCfgDefinition {

    /**
     * Endpoint address
     */
    String getAddress();

    String getUser();

    /**
     * User password
     */
    String getPassword();

    /**
     * Authentication type. e.g. Digest
     *
     */
    String getAuthType();

    /**
     * WSA "from" attribute
     */
    String getWsaFrom();

    /**
     * WSA "to" attribute
     */
    String getWsaTo();

    /**
     * WSA "reply" attribute
     */
    String getWsaReply();

    /**
     * Use nonce in WSi
     */
    Boolean getWithNonce();

    /**
     * SHA1 of SSL server certificate.
     * If not set, any certificate is accepted.
     */
    String getServerThumbprint();

    /**
     * Connection timeout [ms]
     */
    Integer getConnectTimeout();

    /**
     * Request timeout [ms]
     */
    Integer getRequestTimeout();

    /**
     * Set usage tracing on.
     */
    Boolean getLogOn();
    
    /**
     * Time of last service check.
     */
    LocalDateTime getLastCheck();
    
    /**
     * Is service available
     */
    Boolean getCheckOk();
    
    /**
     * Message from last service check
     */
    String getCheckDescription();
    
    /**
     * Set auto check on.
     */
    Boolean getAutoCheck();
    
    /**
     * Auto check frequency
     */
    Integer getAutoCheckFrequency();
    
    /**
     * Groovy script for checking service
     */
    String getCheckExpression();
    
    /**
     * Process ID for auto checking
     */
    String getCheckProcessId();

    /**
     * Proxy host
     */
    public String getProxyHost();

    /**
     * Proxy port
     */
    public Integer getProxyPort();

    /**
     * Proxy user
     */
    public String getProxyUser();

    /**
     * Proxy password
     */
    public String getProxyPassword();

}
