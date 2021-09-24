package pl.fhframework.model.dto.cloud;

import lombok.Data;

import java.util.Set;

/**
 * Security information propagated to other cloud server.
 */
@Data
public class CloudPropagatedSession {

    private String userLogin;

    private String userAddress;

    private String serverAddress;

    // unique id used mainly for logging
    private String conversationUniqueId;

    private String clientInfo;

    private Set<String> businessRoles;

    /**
     * Prefix for any URL-based resource within this session.
     */
    private String resourcesUrlPrefix;
}
