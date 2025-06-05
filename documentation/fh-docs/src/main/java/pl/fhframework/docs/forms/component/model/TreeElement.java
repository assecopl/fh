package pl.fhframework.docs.forms.component.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.docs.forms.model.example.FileSystemNodeExample;
import pl.fhframework.docs.forms.model.example.NodeExample;
import pl.fhframework.docs.forms.service.TreeNodeExmapleService;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TreeElement extends ComponentElement {
    private int counter = 0;
    private int counterArea = 0;
    private List<NodeExample> nodes;
    private String onClickedMessage = "Component clicked 0 times.";
    private NodeExample selectedNode;
    private FileSystemNodeExample selectedFile;
    private List<FileSystemNodeExample> fileSystem = CollectionsUtils.asNewList(
            new FileSystemNodeExample(0, "C:", FileSystemNodeExample.MAX_FILES, true),
            new FileSystemNodeExample(1, "D:", FileSystemNodeExample.MAX_FILES, true),
            new FileSystemNodeExample(2, "E:", FileSystemNodeExample.MAX_FILES, true));
    private List<FileSystemNodeExample> fileSystem2 = CollectionsUtils.asNewList(
            new FileSystemNodeExample(0, "A:", FileSystemNodeExample.MAX_FILES, true),
            new FileSystemNodeExample(1, "B:", FileSystemNodeExample.MAX_FILES, true),
            new FileSystemNodeExample(2, "F:", FileSystemNodeExample.MAX_FILES, true));
    private List<FileSystemNodeExample> fileSystem3 = CollectionsUtils.asNewList(
            new FileSystemNodeExample(0, "G:", FileSystemNodeExample.MAX_FILES, true),
            new FileSystemNodeExample(1, "H:", FileSystemNodeExample.MAX_FILES, true),
            new FileSystemNodeExample(2, "I:", FileSystemNodeExample.MAX_FILES, true));
    private List<FileSystemNodeExample> fileSystem4 = CollectionsUtils.asNewList(
            new FileSystemNodeExample(0, "J:", FileSystemNodeExample.MAX_FILES, true),
            new FileSystemNodeExample(1, "K:", FileSystemNodeExample.MAX_FILES, true),
            new FileSystemNodeExample(2, "L:", FileSystemNodeExample.MAX_FILES, true));

    private List<List<FileSystemNodeExample>> multiFileSystemDirect = Arrays.asList(fileSystem, fileSystem2);
    private List<Files> multiFileSystem = Arrays.asList(new Files(fileSystem3), new Files(fileSystem4));
    public TreeElement(TreeNodeExmapleService treeNodeService){
        this.nodes = treeNodeService.createNodeExample();
    }

    public void incrementCounter() {
        counter++;
    }

    public void incrementAreaCounter() {
        counterArea++;
    }

    @Data
    public static class Files {
        private final List<FileSystemNodeExample> files;
    }
}
