package pl.fhframework.docs.forms.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import pl.fhframework.docs.forms.model.example.NodeExample;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TreeNodeExmapleService {

    public List<NodeExample> createNodeExample() {
        List<NodeExample> customNodes = new ArrayList<>();
        NodeExample node1 = new NodeExample(1);
        node1.setName("node1");
        node1.setIcon("fa fa-file");

        NodeExample subnode2 = new NodeExample(2);
        subnode2.setName("test");
        subnode2.setIcon("fa fa-file-pdf-o");

        NodeExample subnode3 = new NodeExample(3);
        subnode3.setName("subnode3");
        subnode3.setIcon("fa fa-file-pdf-o");

        NodeExample node4 = new NodeExample(4);
        node4.setName("node4");
        node4.setIcon("fa fa-file-text-o");
        node1.getChildren().add(node4);
        node1.getChildren().add(subnode3);

        NodeExample node5 = new NodeExample(5);
        node5.setName("node5");
        node5.getChildren().add(subnode2);
        node5.setIcon("fa fa-file");

        NodeExample node6 = new NodeExample(6);
        node6.setName("node6");
        node6.setIcon("fa fa-file");

        NodeExample node7 = new NodeExample(7);
        node7.setName("node7");
        subnode2.getChildren().add(node7);
        node7.setIcon("fa fa-file");

        customNodes.add(node1);
        customNodes.add(node5);
        customNodes.add(node6);

        return customNodes;
    }

}
