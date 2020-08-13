package pl.fhframework.aspects.snapshots.model;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * @author Paweł Ruta
 */
@XmlTransient
@Deprecated // use only ISnapshotEnabled
public class SnapshotEnabledObject implements ISnapshotEnabled, Serializable {

}
