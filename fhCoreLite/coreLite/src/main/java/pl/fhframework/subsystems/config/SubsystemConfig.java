package pl.fhframework.subsystems.config;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Subsystem's configuration that is kept in module.xml file and may change at runtime.
 */
@Data
@XmlRootElement(name = "subsystem")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubsystemConfig {

    @XmlElement(name = "cloud")
    private SubsystemCloudConfig cloudConfig = new SubsystemCloudConfig();
}
