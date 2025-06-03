/**
 * *********************************************************
 * Author: krzysztof.kozlowski2
 * Created: 2017-10-18
 * **********************************************************
 */
package pl.fhframework.compiler.core.model;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ZipFileMetadata {

    @XmlAttribute
    private String filePath;
    @XmlAttribute
    private String fileSubsystemId;


}
