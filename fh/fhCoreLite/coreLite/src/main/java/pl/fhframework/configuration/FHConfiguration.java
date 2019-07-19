package pl.fhframework.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import org.springframework.core.io.PathResource;
import pl.fhframework.core.FhException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import pl.fhframework.core.io.FhResource;

import java.util.Arrays;
import java.util.Optional;

import lombok.Getter;

@Component
public class FHConfiguration {
    @Autowired
    private Environment environment;

    @Value("${cache.cluster.name:fhCache}")
    private String clusterName;

    @Value("${cache.cluster.force:false}")
    private boolean forceClusterCache;

    @Getter
    @Value("${fhframework.dynamic.output.directory}")
    private String dynamicArtsDirectory;

    @Getter
    @Value("${fhframework.designer.usingPrivateModules:false}")
    private boolean designerUsingPrivateModules;

    public static final String DEFAULT_MAIN_CONTAINER = "mainForm";

    //TODO: move some of this to Spring configuration (application.properties)

    private static final String HOST_WINDOWS_ENV_PROPERTY = "COMPUTERNAME";
    private static final String HOST_UNIX_ENV_PROPERTY = "HOSTNAME";
    private static final String NODENAME_DEFAULT = "UNKNOWN";
    private static final String NODENAME_PROPERTY = "fhframework.nodeName";

    private static final String JSON_OUTPUT_DIR_PROPERTY = "pl.fhframework.outputDirectory";

    private static final String NODENAME = Arrays.asList(
            System.getProperty(NODENAME_PROPERTY),
            System.getenv(HOST_UNIX_ENV_PROPERTY),
            System.getenv(HOST_WINDOWS_ENV_PROPERTY),
            NODENAME_DEFAULT).stream().filter(s -> s != null).findFirst().get();

    private static final String SEND_ERRORS_TO_CLIENT = "fhframework.sendErrorsToClient";

    public static final String SUBSYTEM_PATH_PREFIX = "fhframework.path.";

    public static final String SUBSYTEM_BASE_PATH = "fhframework.basePath";

    /**
     * Gets optionally overriden subsystem path of XMl files.
     * @param subsystemName subsystem path
     * @return subsystem overriden path (optional)
     */
    public static Optional<FhResource> getOverridenSubsystemPath(String subsystemName, boolean optional) {
        String subsystemPath = System.getProperty(SUBSYTEM_PATH_PREFIX + subsystemName);
        if (subsystemPath != null && !subsystemPath.isEmpty()) {
            return Optional.of(FhResource.get(new PathResource(Paths.get(subsystemPath))));
        }

        // try to find subsystem directory in base path
        String basePath = System.getProperty(SUBSYTEM_BASE_PATH);
        if (basePath != null && !basePath.isEmpty()) {
            Path subsystemDirPath = Paths.get(basePath).resolve(subsystemName);
            if (Files.isDirectory(subsystemDirPath)) {
                return Optional.of(FhResource.get(new PathResource(subsystemDirPath)));
            }
        }

        if (optional) {
            // if optional - return empty
            return Optional.empty();
        } else {
            // if not optional - fail
            throw new FhException(String.format("Must specify -D%s=DIR_WITH_SUBSYSTEM_DIRS pointing direcotry " +
                            "with %s present OR -D%s%s=SUBSYSTEM_DIR to set path to %s subsystem's XML resources.",
                    SUBSYTEM_BASE_PATH, subsystemName, SUBSYTEM_PATH_PREFIX, subsystemName, subsystemName));
        }
    }

    /**
     * Gets name of this cluster node.
     * @return name of the node
     */
    public static String getNodeName() {
        return NODENAME;
    }

    /**
     * Gets path to output directory of client-server communication JSON's. If null, no output should be writen.
     * @return output directory of client-server communication JSON's
     */
    public static String getJsonOutputDirectory() {
        return System.getProperty(JSON_OUTPUT_DIR_PROPERTY);
    }

    /**
     * Checks if server should send its errors to client.
     * @return if server should send its errors to client.
     */
    public static boolean isSendingErrorsToClient() {
        return "true".equalsIgnoreCase(System.getProperty(SEND_ERRORS_TO_CLIENT, "true"));
    }

    public boolean isDevModeActive() {
        return isModeActive("dev");
    }

    public boolean isProdModeActive() {
        return isModeActive("prod");
    }

    private boolean isModeActive(String mode) {
        for (String activeProfile : environment.getActiveProfiles()) {
            if (activeProfile.equals(mode)) {
                return true;
            }
        }
        return false;
    }

    public String getClusterName() {
        if (!clusterName.isEmpty() && (!isDevModeActive() || forceClusterCache)) {
            return clusterName;
        }
        return NODENAME;
    }
}
