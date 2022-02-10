package pl.fhframework.dp.commons.fh.adm.ui;

import lombok.Getter;
import pl.fhframework.core.security.ISystemFunctionId;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Tomasz.Kozlowski (created on 2018-02-01)
 */
@Getter
public class Node implements Comparable<Node> {

    private final String name;
    private final String fullName;
    private final String moduleUUID;
    private final boolean isRoot;
    private final Set<Node> children;

    public Node(String name, String parentName, String moduleUUID, boolean isRoot) {
        this.name = name;
        this.fullName = parentName == null ? name : parentName + ISystemFunctionId.SEPARATOR + name;
        this.moduleUUID = moduleUUID;
        this.isRoot = isRoot;
        this.children = new TreeSet<>();
    }

    public Node(String name, String parentName, String moduleUUID) {
        this(name, parentName, moduleUUID, false);
    }

    @Override
    public int compareTo(Node node) {
        if (node != null) {
            int result = fullName.compareTo(node.fullName);
            if (result != 0) return result;
            return moduleUUID.compareTo(node.moduleUUID);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(fullName, node.fullName) &&
               Objects.equals(moduleUUID, node.moduleUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, moduleUUID);
    }

}
