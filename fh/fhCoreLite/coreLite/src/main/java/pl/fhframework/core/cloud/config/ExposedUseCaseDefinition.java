package pl.fhframework.core.cloud.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.HashSet;
import java.util.Set;

/**
 * Definition of a use case exposed to cloud server's clients.
 */
@ToString
@Getter
@Setter
public class ExposedUseCaseDefinition {

    private String className;

    private Set<String> allowedBusinessRoles = new HashSet<>();

    private Set<String> serviceInterfaces = new HashSet<>();

    // json representation of service descriptor if available
    private String serviceDescriptor;

    public static ExposedUseCaseDefinition newInstance(String className, Set<String> allowedBusinessRoles, Set<String> serviceInterfaces) {
        ExposedUseCaseDefinition uc = new ExposedUseCaseDefinition();
        uc.className = className;
        uc.allowedBusinessRoles = allowedBusinessRoles;
        uc.serviceInterfaces = serviceInterfaces;
        return uc;
    }

    public static ExposedUseCaseDefinition newInstance(String className, Set<String> allowedBusinessRoles, Set<String> serviceInterfaces, String serviceDescriptor) {
        ExposedUseCaseDefinition uc = newInstance(className, allowedBusinessRoles, serviceInterfaces);
        uc.serviceDescriptor = serviceDescriptor;
        return uc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExposedUseCaseDefinition that = (ExposedUseCaseDefinition) o;

        if (className != null ? !className.equals(that.className) : that.className != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return className != null ? className.hashCode() : 0;
    }
}
