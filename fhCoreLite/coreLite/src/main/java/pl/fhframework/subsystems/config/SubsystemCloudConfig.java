package pl.fhframework.subsystems.config;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration of subsystem's cloud.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class SubsystemCloudConfig {

    @XmlElementWrapper(name = "exposedUseCases")
    @XmlElement(name = "useCase")
    private List<CloudExposedUseCaseConfig> cloudExposedUseCases = new ArrayList<>();

    @XmlElementWrapper(name = "exposedRestServices")
    @XmlElement(name = "restService")
    private List<CloudExposedUseCaseConfig> cloudExposedRestServices = new ArrayList<>();

    @XmlElement(name = "exposedResources")
    private CloudExposedResourcesConfig cloudExposedResources = new CloudExposedResourcesConfig();

}
