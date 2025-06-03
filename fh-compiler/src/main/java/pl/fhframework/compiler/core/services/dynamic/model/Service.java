package pl.fhframework.compiler.core.services.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.services.meta.ServiceInfo;
import pl.fhframework.compiler.core.services.service.ServiceConsts;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Permission;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Service", namespace = ServiceConsts.SERVICE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "label", "description", "serviceType", "endpointName", "endpointUrl", "baseUri", "operations","permissions","categories"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Service implements ISnapshotEnabled, Serializable, Cloneable {

    @XmlAttribute
    @XmlID
    private String id;

    @XmlAttribute
    private String label;

    @XmlAttribute
    private String description;

    /**
     * XSD reference
     */
    @XmlAttribute
    @JsonIgnore
    private String xmlns;

    @XmlAttribute
    private ServiceTypeEnum serviceType;

    @XmlElement(name = "EndpointName")
    private String endpointName;

    @XmlElement(name = "EndpointUrl")
    private String endpointUrl;

    @XmlElement(name = "BaseUri")
    private String baseUri;

    @XmlElementRef
    private List<Operation> operations = new ArrayList<>();

    @XmlElements({
            @XmlElement(name = "Permission", type = Permission.class)
    })
    @XmlElementWrapper(name = "Permissions")
    private List<Permission> permissions = new LinkedList<>();

    @XmlElementWrapper(name = "Categories")
    @XmlElement(name = "Category")
    private List<String> categories = new LinkedList<>();

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private ServiceInfo serviceInfo;

    public String getName() {
        return getId() + "." + StringUtils.firstLetterToLower(DynamicClassName.forClassName(getId()).getBaseClassName());
    }
}
