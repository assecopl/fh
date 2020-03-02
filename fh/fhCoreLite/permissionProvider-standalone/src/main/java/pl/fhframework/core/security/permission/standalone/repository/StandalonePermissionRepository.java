package pl.fhframework.core.security.permission.standalone.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.model.IPermission;
import pl.fhframework.core.security.permission.standalone.model.Permission;
import pl.fhframework.core.security.permission.standalone.model.PermissionRegistry;
import pl.fhframework.core.security.provider.repository.PermissionRepository;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Tomasz Kozlowski (created on 16.10.2019)
 */
@Service
public class StandalonePermissionRepository implements PermissionRepository {

    @Value("${fhframework.security.permission.standalone.file:database/permissions.xml}")
    private String filePath;
    private File file;
    private PermissionRegistry registry;

    @PostConstruct
    public synchronized void init() {
        file = new File(filePath);
        if (file.exists()) {
            registry = readRegistryFile();
        } else {
            registry = new PermissionRegistry();
        }
    }

    @Override
    public IPermission createInstance() {
        return new Permission();
    }

    @Override
    public synchronized List<IPermission> findForBusinessRole(String businessRoleName) {
        return registry.getPermissions().stream()
                .filter(permission -> permission.getBusinessRoleName().equalsIgnoreCase(businessRoleName))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized List<IPermission> findForModuleAndFunction(String moduleUUID, Collection<String> functions) {
        return registry.getPermissions().stream()
                .filter(permission -> permission.getModuleUUID().equals(moduleUUID))
                .filter(permission -> functions.contains(permission.getFunctionName()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized IPermission save(IPermission permission) {
        registry.getPermissions().add(cast(permission));
        writeRegistryFile();
        return permission;
    }

    @Override
    public synchronized void saveAll(List<IPermission> permissions) {
        permissions.forEach(permission -> registry.getPermissions().add(cast(permission)));
        writeRegistryFile();
    }

    @Override
    public synchronized void delete(IPermission permission) {
        registry.getPermissions().remove(cast(permission));
        writeRegistryFile();
    }

    /** Read registry file */
    private PermissionRegistry readRegistryFile() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PermissionRegistry.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (PermissionRegistry) unmarshaller.unmarshal(file);
        } catch(JAXBException e) {
            throw new RuntimeException("Error during reading permissions XML file");
        }
    }

    /** Write registry file */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void writeRegistryFile() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(registry.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            File directory = file.getParentFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }
            marshaller.marshal(registry, file);
        } catch(JAXBException e) {
            FhLogger.errorSuppressed("Error during writing permissions XML file", e);
        }
    }

    private Permission cast(IPermission permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission parameter is null !");
        } else if (!(permission instanceof Permission)) {
            throw new IllegalArgumentException(
                    String.format("permission parameter is not an instance of %s ! [permission type: %s]",
                            Permission.class.getName(), permission.getClass().getName())
            );
        } else {
            return (Permission) permission;
        }
    }

}
