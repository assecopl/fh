package pl.fhframework.dp.commons.fh.adm.ui;

import lombok.RequiredArgsConstructor;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.core.security.ISystemFunctionId;
import pl.fhframework.core.util.StringUtils;

import java.util.*;

/**
 * @author Tomasz Kozlowski (created on 24.02.2021)
 */
@RequiredArgsConstructor
public class FunctionsTreeBuilder {

    private final List<AuthorizationManager.Module> allModules;
    private final Set<AuthorizationManager.Function> allFunctions;

    public Set<Node> buildSystemFunctionsTree() {
        return this.buildSystemFunctionsTree(allFunctions);
    }

    public Set<Node> buildSystemFunctionsTree(Set<AuthorizationManager.Function> functions) {
        final Set<Node> systemFunctionsTree = new TreeSet<>();
        for (AuthorizationManager.Function function : functions) {
            Node module = getModuleNode(systemFunctionsTree, function.getModuleUUID());
            Set<Node> nodes = module.getChildren();
            StringTokenizer tokenizer = new StringTokenizer(function.getName(), ISystemFunctionId.SEPARATOR);
            String parentName = null;
            while (tokenizer.hasMoreElements()) {
                Node node = addChild(nodes, tokenizer.nextToken(), parentName, module.getModuleUUID());
                parentName = node.getFullName();
                nodes = node.getChildren();
            }
        }
        return systemFunctionsTree;
    }

    private Node addChild(Set<Node> nodes, String nodeName, String parentName, String moduleUUID) {
        Optional<Node> result = findNode(nodes, nodeName, moduleUUID);
        Node node;
        if (result.isPresent()) {
            node = result.get();
        } else {
            node = new Node(nodeName, parentName, moduleUUID);
            nodes.add(node);
        }
        return node;
    }

    private Optional<Node> findNode(Set<Node> nodes, String nodeName, String moduleUUID) {
        for (Node node : nodes) {
            if (node.getName().equals(nodeName) && node.getModuleUUID().equals(moduleUUID)) {
                return Optional.of(node);
            }
        }
        return Optional.empty();
    }

    private Node getModuleNode(Set<Node> systemFunctionsTree, String moduleUUID) {
        Node moduleNode = null;
        for (Node node : systemFunctionsTree) {
            if (node.getModuleUUID().equals(moduleUUID)) {
                moduleNode = node;
                break;
            }
        }
        // create if does not exist
        if (moduleNode == null) {
            String productLabel = getModuleLabel(moduleUUID);
            moduleNode = new Node(productLabel, null, moduleUUID, true);
            systemFunctionsTree.add(moduleNode);
        }
        return moduleNode;
    }

    public String getModuleLabel(String moduleUUID) {
        for (AuthorizationManager.Module module : allModules) {
            if (StringUtils.equals(module.getUuid(), moduleUUID)) {
                return module.getName();
            }
        }
        return moduleUUID;
    }

}
