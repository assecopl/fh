/***********************************************************
 * Opis: patrz niżej w komentarzach javadoc.
 ***********************************************************
 * Osoba odpowiedzialna: Kamil Pliszka
 * Data utworzenia: 2017-10-11 13:58
 ***********************************************************/

package pl.fhframework.compiler.core.uc.dynamic.model.element;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Permission")
@XmlAccessorType(XmlAccessType.FIELD)
public class Permission implements ISnapshotEnabled, Cloneable {
    @XmlAttribute
    private String name;

    private Permission(Permission other) {
        this.name = other.name;
    }

    @Override
    public Object clone() {
        return new Permission(this);
    }

    public static Permission of(String name) {
        Permission permission = new Permission();
        permission.setName(name);
        return permission;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                '}';
    }
}
