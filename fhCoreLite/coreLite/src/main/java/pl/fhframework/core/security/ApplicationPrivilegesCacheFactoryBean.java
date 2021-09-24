package pl.fhframework.core.security;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.security.AuthorizationManager.Function;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.subsystems.ModuleRegistry;

import java.util.List;
import java.util.Map;

/**
 * @author Tomasz.Kozlowski (created on 24.10.2018)
 */
@Component
public class ApplicationPrivilegesCacheFactoryBean implements FactoryBean<ApplicationPrivilegesCache> {

    private static final String PRIVILEGES = "privileges";
    private static final String ALLOW_ALL = "allow-all";
    private static final String MODULE = "module";
    private static final String UUID = "uuid";
    private static final String PERMISSIONS = "permissions";
    private static final String PERMISSION = "permission";
    private static final String TYPE = "type";
    private static final String FUNCTION = "function";
    private static final String ALLOW = "ALLOW";
    private static final String DENY = "DENY";

    @Value("${fhframework.security.provider.application-privileges:}")
    private String appPrivilegesConfigFile;

    @Override
    public ApplicationPrivilegesCache getObject() {
        return createApplicationPrivilegesCache();
    }

    @Override
    public Class<?> getObjectType() {
        return ApplicationPrivilegesCache.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * Creates and returns an application privileges cache.
     * @return Created cache with application privileges.
     */
    private ApplicationPrivilegesCache createApplicationPrivilegesCache() {
        ApplicationPrivilegesCache cache = new ApplicationPrivilegesCache();
        try {
            Map<String, Object> content = readFile(appPrivilegesConfigFile);
            parsePrivileges(cache, content);
        } catch (Exception e) {
            FhLogger.error(String.format("Exception during reading file %s (%s)", appPrivilegesConfigFile, e.getCause()));
            // if there is a problem with file then allow all functions
            cache.setGlobalPrivilege(Boolean.TRUE);
            FhLogger.info(this.getClass(), "Application privileges was set to allow all");
        }
        return cache;
    }

    private static Map<String, Object> readFile(String filePath) {
        Resource resource = new ClassPathResource(filePath);
        YamlMapFactoryBean yamlFactory = new YamlMapFactoryBean();
        yamlFactory.setResources(resource);
        return yamlFactory.getObject();
    }

    @SuppressWarnings("unchecked")
    private static void parsePrivileges(ApplicationPrivilegesCache cache, Map<String, Object> content) {
        Object allowAll = content.get(ALLOW_ALL);
        if (allowAll != null) {
            if (allowAll instanceof Boolean && (Boolean)allowAll) {
                cache.setGlobalPrivilege(Boolean.TRUE);
                FhLogger.debug(ApplicationPrivilegesCacheFactoryBean.class, "Application privileges was set as allow all");
                return;
            }
        }

        List<Object> privileges = (List<Object>) content.get(PRIVILEGES);
        if (privileges != null) {
            for (Object module : privileges) {
                try {
                    parseModule(cache, (Map) ((Map) module).get(MODULE));
                } catch (Exception e) {
                    FhLogger.warn("Cannot parse application privilege for module: {}", module);
                }
            }
        } else {
            cache.setGlobalPrivilege(Boolean.TRUE);
        }
    }

    @SuppressWarnings("unchecked")
    private static void parseModule(ApplicationPrivilegesCache cache, Map module) {
        String moduleUUID = (String) module.get(UUID);
        if (StringUtils.isNullOrEmpty(moduleUUID)) {
            throw new IllegalStateException("Module UUID is null or empty");
        }

        List<Object> permissions = (List<Object>) module.get(PERMISSIONS);
        for (Object permission : permissions) {
            try {
                parsePermission(cache, moduleUUID, (Map)((Map) permission).get(PERMISSION));
            } catch (Exception e) {
                FhLogger.warn("Cannot parse application privilege: {}", permission);
            }
        }
    }

    private static void parsePermission(ApplicationPrivilegesCache cache, String moduleUUID, Map permission) {
        String type = (String) permission.get(TYPE);
        String functionName = (String) permission.get(FUNCTION);

        Boolean denial = null;
        switch (type) {
            case ALLOW: denial = Boolean.FALSE;
                break;
            case DENY: denial = Boolean.TRUE;
                break;
        }

        if (denial == null || StringUtils.isNullOrEmpty(functionName)) {
            throw new IllegalStateException("Permission variables are null or empty");
        }

        String moduleLabel = ModuleRegistry.getModuleProductLabel(moduleUUID);
        cache.addFunction(
                Function.of(functionName, moduleUUID, moduleLabel, denial)
        );

        FhLogger.debug(ApplicationPrivilegesCacheFactoryBean.class, "Loaded application privilege: {} ; {} ; {}", moduleUUID, functionName, type);
    }

}