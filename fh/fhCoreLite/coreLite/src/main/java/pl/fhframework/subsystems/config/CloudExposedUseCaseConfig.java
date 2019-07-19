package pl.fhframework.subsystems.config;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Cloud exposed use case config.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CloudExposedUseCaseConfig {

    @XmlAttribute(required = true)
    private String className;
}
