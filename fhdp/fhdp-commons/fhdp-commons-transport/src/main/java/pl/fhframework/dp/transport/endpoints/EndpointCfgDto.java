package pl.fhframework.dp.transport.endpoints;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import pl.fhframework.dp.commons.base.model.IEndpointCfg;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.transport.enums.EndpointCheckFrequencyEnum;

import java.time.LocalDateTime;

/**
 * Created by jacekb.
 */
@Document(indexName = "#{@indexNamePrefix}_endpoint_cfg")
@Setting(settingPath = "/settings/settings.json")
@Getter @Setter
public class EndpointCfgDto implements IEndpointCfg, IPersistentObject<String> {
    @Id
    private String id;
    private String systemName;
    private String serviceName;
    private String address;
    private String password;
    private String user;
    private String authType;
    private String wsaFrom;
    private String wsaTo;
    private String wsaReply;
    private Boolean withNonce;
    private String serverThumbprint;
    private Integer connectTimeout;
    private Integer requestTimeout;
    private Boolean logOn;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
	private LocalDateTime lastCheck;
    private String proxyHost;
    private Integer proxyPort;
    private String proxyUser;
    private String proxyPassword;
	private Boolean checkOk;
	private String checkDescription;
    private Boolean autoCheck;
    private Integer autoCheckFrequency;
    private EndpointCheckFrequencyEnum endpointCheckFrequencyEnum;
    private String checkExpression;
    private String checkProcessId;

    /**
     * Once this constructor is used items in the Elasticsearch won't be overridden
     */
    public EndpointCfgDto() {
    }

    public EndpointCfgDto(String systemName, String serviceName) {
        this.systemName = systemName;
        this.serviceName = serviceName;
        this.id = systemName.concat("_").concat(serviceName);
    }

    /**
     * Basic constructor
     * @param systemName
     * @param serviceName
     * @param address
     * @param user
     * @param password
     * @param authType
     * @param wsaFrom
     * @param wsaTo
     * @param wsaReply
     * @param withNonce
     * @param serverThumbprint
     * @param connectTimeout
     * @param requestTimeout
     * @param logOn
     */
    public EndpointCfgDto(String systemName, String serviceName, String address, String user, String password, String authType, String wsaFrom, String wsaTo, String wsaReply, Boolean withNonce, String serverThumbprint, Integer connectTimeout, Integer requestTimeout, Boolean logOn) {
        this( systemName,  serviceName,  address,  user,  password, authType,  wsaFrom,  wsaTo,  wsaReply,  withNonce,  serverThumbprint,  connectTimeout,  requestTimeout,  logOn, null, null, null, null);
    }

    /**
     * systemName & serviceName cannot be null
     * if the user is null the key of Elasticsearch is generated
     * @param systemName
     * @param serviceName
     * @param address
     * @param user
     * @param password
     * @param authType
     * @param wsaFrom
     * @param wsaTo
     * @param wsaReply
     * @param withNonce
     * @param serverThumbprint
     * @param connectTimeout
     * @param requestTimeout
     * @param logOn
     * @param proxyHost
     * @param proxyPort
     * @param proxyUser
     * @param proxyPassword
     */
    public EndpointCfgDto(String systemName, String serviceName, String address, String user, String password, String authType, String wsaFrom, String wsaTo, String wsaReply, Boolean withNonce, String serverThumbprint, Integer connectTimeout, Integer requestTimeout, Boolean logOn, String proxyHost, Integer proxyPort, String proxyUser, String proxyPassword) {
        this.id = systemName.concat("_").concat(serviceName);
        this.systemName = systemName;
        this.serviceName = serviceName;
        this.address = address;
        this.password = password;
        this.user = user;
        this.authType = authType;
        this.wsaFrom = wsaFrom;
        this.wsaTo = wsaTo;
        this.wsaReply = wsaReply;
        this.withNonce = withNonce;
        this.serverThumbprint = serverThumbprint;
        this.connectTimeout = connectTimeout;
        this.requestTimeout = requestTimeout;
        this.logOn = logOn;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EndpointCfgDto that = (EndpointCfgDto) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
