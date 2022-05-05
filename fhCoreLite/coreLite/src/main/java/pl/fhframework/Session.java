package pl.fhframework;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.IApplicationInformation;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.session.scope.SessionScopeBeanContainer;
import pl.fhframework.configuration.FHConfiguration;
import pl.fhframework.model.security.SystemUser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
public abstract class Session {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final int ERROR_INFORMATION_LIMIT = 10;

    protected SystemUser systemUser;

    private SessionScopeBeanContainer scopeBeanContainer = new SessionScopeBeanContainer();

    @Setter(AccessLevel.NONE)
    @Autowired
    private FHConfiguration fhConfiguration;

    private SessionDescription description;
    private Instant creationTimestamp;

    // frequently used, expensive generation
    private String creationTimestampString;
    private String creationDateString;

    @Autowired
    private IApplicationInformation applicationInformation;

    // I18n
    private Locale language;

    @Autowired(required = false)
    private AuthorizationManager authorizationManager;

    public Session(SessionDescription description) {
        this.description = description;
        creationTimestamp = Instant.now();
        creationTimestampString = TIMESTAMP_FORMATTER.format(LocalDateTime.ofInstant(creationTimestamp, ZoneId.systemDefault()));
        creationDateString = DATE_FORMATTER.format(LocalDateTime.ofInstant(creationTimestamp, ZoneId.systemDefault()));
    }

    public boolean isDevModeActive() {
        return fhConfiguration.isDevModeActive();
    }

    public boolean isProdModeActive() {
        return fhConfiguration.isProdModeActive();
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public boolean hasAccessTo(String functionName, String moduleUUID) {
        return getAuthorizationManager() != null && getSystemUser() != null && getAuthorizationManager().hasFunction(getSystemUser().getBusinessRoles(), functionName, moduleUUID);
    }

    public abstract boolean isUserContext();

    @Getter
    @Setter
    public static class TimeoutData {
        private Instant now;
        private Instant lastEventTimestamp;
        private String counterElementId;
        private int maxInactivityMinutes;

        public TimeoutData(Instant lastEventTimestamp, String counterElementId, int maxInactivityMinutes) {
            this.now = Instant.now();
            this.lastEventTimestamp = lastEventTimestamp;
            this.counterElementId = counterElementId;
            this.maxInactivityMinutes = maxInactivityMinutes;
        }
    }

    /**
     * Resolved client address
     *
     * @return Full client address with port
     */
    public String getClientAddress() {
        return ClientAddressProvider.getClientAddress(description);
    }
}
