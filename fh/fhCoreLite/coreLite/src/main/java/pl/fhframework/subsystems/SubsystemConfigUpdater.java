package pl.fhframework.subsystems;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhSubsystemException;
import pl.fhframework.core.cloud.event.LocalServerDefinitionChangedEvent;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.subsystems.config.SubsystemConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.time.Instant;

/**
 * Reader of subsystem's config files.
 */
@Service
public class SubsystemConfigUpdater {

    public static final String MODULE_FILE_NAME = "module.xml";

    private static final int FILE_CHECK_FREQUENCY = 5000;

    private static JAXBContext jaxbContext;
    static {
        try {
            jaxbContext = JAXBContext.newInstance(SubsystemConfig.class);
        } catch (JAXBException e) {
            throw new FhSubsystemException(e);
        }
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = FILE_CHECK_FREQUENCY)
    public void updateAllSubsystemsConfig() {
        for (Subsystem subsystem : ModuleRegistry.getLoadedModules()) {
            updateSubsystemConfig(subsystem);
        }
    }

    public void updateSubsystemConfig(Subsystem subsystem) {
        boolean configChanged = updateConfigIfNeeded(subsystem);
        boolean menuChanged = false; // TODO: check menu here instead of on access
        if (configChanged || menuChanged) {
            eventPublisher.publishEvent(new LocalServerDefinitionChangedEvent(subsystem));
        }
    }

    public static boolean updateConfigIfNeeded(Subsystem subsystem) {
        FhResource file = FhResource.get(FileUtils.resolve(subsystem.getBasePath(), MODULE_FILE_NAME));
        if (file.exists()) {
            Instant currentTimestamp = FileUtils.getLastModified(file);
            if (subsystem.getConfigTimestamp() != null && subsystem.getConfigTimestamp().equals(currentTimestamp)) {
                return false;
            } else {
                if (subsystem.getConfigTimestamp() == null) {
                    FhLogger.info(SubsystemConfigUpdater.class, "{} of subsystem {} detected - updating configuration", MODULE_FILE_NAME, subsystem.getName());
                } else {
                    FhLogger.info(SubsystemConfigUpdater.class, "{} of subsystem {} has changed - updating configuration", MODULE_FILE_NAME, subsystem.getName());
                }
                try (InputStream fileInput = file.getInputStream()) {
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    subsystem.setConfig((SubsystemConfig) unmarshaller.unmarshal(fileInput));
                    subsystem.setConfigTimestamp(currentTimestamp);
                    return true;
                } catch (Exception e) {
                    throw new FhSubsystemException("Error reading subsystem's config file " + file.toString(), e);
                }
            }
        } else {
            if (subsystem.getConfigTimestamp() == null) {
                return false;
            } else {
                FhLogger.info(SubsystemConfigUpdater.class, "{} of subsystem {} has been deleted - updating configuration", MODULE_FILE_NAME, subsystem.getName());
                subsystem.setConfig(null);
                subsystem.setConfigTimestamp(null);
                return true;
            }
        }
    }
}
