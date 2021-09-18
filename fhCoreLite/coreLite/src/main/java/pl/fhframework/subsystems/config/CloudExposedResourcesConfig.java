package pl.fhframework.subsystems.config;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Cloud exposed resources config.
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class CloudExposedResourcesConfig {

    @XmlAttribute
    private boolean fileUpload;

    @XmlAttribute
    private boolean fileDownload;

    @XmlElement(name = "antMatcher")
    private List<String> antMatchers = new ArrayList<>();
}
