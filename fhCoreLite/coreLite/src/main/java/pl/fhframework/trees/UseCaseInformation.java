package pl.fhframework.trees;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.annotations.ElementPresentedOnTree;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.cloud.config.ExposedMenuElement;
import pl.fhframework.core.cloud.config.ExposedUseCaseDefinition;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.security.annotations.SystemRole;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.model.security.SystemUser;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UseCaseInformation implements ITreeElement {

    @Getter
    @Setter
    private String ref;
    @Getter
    @Setter
    private String label;
    @Getter
    @Setter
    private String icon;
    @Getter
    @Setter
    private int position;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    @JsonIgnore
    private boolean dynamic;

    @Getter
    private String cloudServerName;

    @Getter
    @Setter
    private AtomicBoolean activityToken;

    /**
     * Should this menu element be exposed to cloud clients
     */
    @Getter
    @Setter
    private boolean cloudExposed;

    @Getter
    @Setter
    private String inputFactory;

    @Getter
    @Setter
    @JsonIgnore
    private List<String> modes;

    @Getter
    @JsonIgnore
    private Set<String> allowedBusinessRoles = new HashSet<>();

    @Getter
    @Setter
    @JsonIgnore
    private Set<String> allowedSystemFunctions = new HashSet<>();

    @Getter
    @Setter
    @JsonIgnore
    private Set<String> allowedSystemRoles = new HashSet<>();


    public UseCaseInformation() {
    }

    public static UseCaseInformation newCloudInstance(String cloudServerName, AtomicBoolean activityToken, ExposedMenuElement menuElement, List<ExposedUseCaseDefinition> serverUseCases) {
        UseCaseInformation newElement = new UseCaseInformation();
        newElement.cloudServerName = cloudServerName;
        newElement.ref = menuElement.getRef();
        newElement.label = menuElement.getLabel();
        newElement.cloudExposed = false;
        newElement.dynamic = false;
        newElement.activityToken = activityToken;
        ExposedUseCaseDefinition useCaseDefinition = serverUseCases.stream().filter(uc -> uc.getClassName().equals(menuElement.getRef())).findFirst().get();
        newElement.allowedBusinessRoles = useCaseDefinition.getAllowedBusinessRoles();
        return newElement;
    }

    UseCaseInformation(Class<?> clazz) {
        ElementPresentedOnTree element = clazz.getAnnotation(ElementPresentedOnTree.class);
        this.ref = clazz.getName();
        this.label = element.label();
        this.icon = element.icon();
        this.position = element.position();
        this.description = element.description();
        this.cloudExposed = element.cloudExposed();
        this.dynamic = false;
        this.activityToken = new AtomicBoolean(true);
        this.allowedSystemFunctions = getAllowedSystemFunctions(clazz);
        this.allowedSystemRoles = getAllowedSystemRoles(clazz);
    }

    UseCaseInformation(XmlAttributeReader xmlAttributeReader) {
        this.ref = xmlAttributeReader.getAttributeValue("ref");
        this.label = xmlAttributeReader.getAttributeValue("label");
        this.icon = xmlAttributeReader.getAttributeValue("icon");
        this.position = xmlAttributeReader.getNumberOrDefault("coords", 50);
        this.description = xmlAttributeReader.getAttributeValue("description");
        this.inputFactory = xmlAttributeReader.getAttributeValue("inputFactory");
        this.cloudExposed = xmlAttributeReader.getAttributeValueOrDefault("cloudExposed", "false").equals("true");
        if(xmlAttributeReader.getAttributeValue("mode") != null) {
            this.modes = Arrays.asList(xmlAttributeReader.getAttributeValue("mode").split(","));
        }

        try {
            if (this.ref == null || this.ref.isEmpty()) {
                this.dynamic = true;
            } else {
                FhCL.classLoader.loadClass(this.ref);
                this.dynamic = false;
            }
        } catch (ClassNotFoundException e) {
            this.dynamic = true;
        }
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    public boolean availableFor(AuthorizationManager authorizationManager, SystemUser systemUser, List<String> activeModes) {
        if(!isAvailableForMode(activeModes)) {
            return false;
        }

        if (!CollectionUtils.isEmpty(allowedSystemFunctions) || !CollectionUtils.isEmpty(allowedSystemRoles)) {
            // System Functions
            if (!CollectionUtils.isEmpty(allowedSystemFunctions)) {
                String moduleUUID = null;
                Optional<UseCaseInfo> useCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(ref);
                if (useCaseInfo.isPresent()) {
                    moduleUUID = useCaseInfo.get().getSubsystem().getProductUUID();
                }
                for (String allowedSystemFunction : allowedSystemFunctions) {
                    if (authorizationManager.hasFunction(systemUser.getBusinessRoles(), allowedSystemFunction, moduleUUID)) {
                        return true;
                    }
                }
            }
            // System Roles
            return !allowedSystemRoles.isEmpty() && systemUser.hasAnyRole(allowedSystemRoles);
        } else if (!CollectionUtils.isEmpty(allowedBusinessRoles)) {
            // Business Roles
            return systemUser.hasAnyRole(allowedBusinessRoles);
        } else {
            // no functions nor roles are required
            return !authorizationManager.isRoleBasedAuthorization() || !systemUser.isGuest();
        }
    }

    @Override
    public List<ITreeElement> getSubelements() {
        return Collections.emptyList();
    }

    @Override
    public boolean isGrouping() {
        return false;
    }

    public static Set<String> getAllowedSystemFunctions(Class<?> clazz) {
        SystemFunction[] annotations = clazz.getAnnotationsByType(SystemFunction.class);
        return Arrays.stream(annotations)
                .map(SystemFunction::value)
                .collect(Collectors.toSet());
    }

    public static Set<String> getAllowedSystemRoles(Class<?> clazz) {
        SystemRole[] annotations = clazz.getAnnotationsByType(SystemRole.class);
        return Arrays.stream(annotations)
                .map(SystemRole::value)
                .collect(Collectors.toSet());
    }

}
