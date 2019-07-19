package pl.fhframework.core.security.provider.management.status;

import lombok.Data;

/**
 * Status of a FH application
 */
@Data
public class ApplicationStatus {

    private int userSessionCount;

    private int errorLogsCount;

    private int warningLogsCount;

    private String businessRoleProviderType;

    private String businessRoleProviderSource;

    private String userAccountProviderType;

    private String userAccountProviderSource;
}
