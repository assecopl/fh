package pl.fhframework.dp.commons.fh.outline;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.dp.commons.fh.declaration.message.DeclarationMessageHelper;
import pl.fhframework.dp.transport.enums.MessageDirectionEnum;
import pl.fhframework.dp.transport.msg.DeclarationMessageDto;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.services.FhService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02.09.2019
 */

@Slf4j
@FhService
public class OutlineService {

    @Value("${fhdp.direction:true}")
    public boolean isDirection;

    private Map<String, ElementCT> elements = new HashMap<>();
    private Map<String, String> mappings = new HashMap<>();

    @Autowired
    private MessageService messageService;

    public List<ElementCT> generateOutline(String docType) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Outline.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        String fileName = ("/outlines/" + docType + "_OUTLINE.xml");
        InputStream is = OutlineService.class.getResourceAsStream(fileName);
        Outline ret = (Outline) unmarshaller.unmarshal(is);
        if(ret.getElement() != null) {
            buildMaps(ret.getElement().get(0).getElement());
            return ret.getElement().get(0).getElement();
        } else {
            return new ArrayList<>();
        }
    }

    private void buildMaps(List<ElementCT> list) {
        if(list == null) return;
        for(ElementCT el: list) {
            elements.put(el.getId(), el);
            if(el.getMappings() != null) {
                List<String> mappingsList = el.getMappings().getPointer();
                for(String p: mappingsList) {
                    mappings.put(p.toLowerCase(), el.getId());
                }
            }
            buildMaps(el.getElement());
        }
    }

    /**
     * Based on mappings in outline, retrieves element matching pointer from data model.
     * Finds the nearest sub pointer, if given is not defined
     * @param pointer
     * @return found element or null.
     */
    public ElementCT findElementFromPointer(String pointer) {
        ElementCT ret = null;
        String p = pointer.toLowerCase();
        String id = null;
        do {
            id = mappings.get(p);
            if(id == null) {
                p = reducePointer(p);
            }
        } while(id == null && !p.isEmpty());
        if(id != null) {
            ret = elements.get(id);
        }
        return ret;
    }

    private String reducePointer(String p) {
        String[] parts = p.split(Pattern.quote("."));
        List<String> partsList = Arrays.asList(parts);
        int lastIndex = partsList.size() - 1;
        if(lastIndex > 0) {
            return String.join(".", partsList.subList(0, lastIndex));
        } else {
            return "";
        }
    }

    public List<TreeElement<ElementCT>> buildLeftMenu(List<ElementCT> elementCTS) {
        return buildIndexedLeftMenu(elementCTS, null, null);
    }

    public List<TreeElement<ElementCT>> buildIndexedLeftMenu(List<ElementCT> elementCTS, Integer index, String indexedObjectName) {
        return buildIndexedLeftMenu(elementCTS, index, indexedObjectName, null);
    }

    public List<TreeElement<ElementCT>> buildIndexedLeftMenu(List<ElementCT> elementCTS, Integer index, String indexedObjectName, Integer parentIndex) {
        List<TreeElement<ElementCT>> leftMenuElements = new ArrayList<>();
        for (ElementCT elementCT : elementCTS) {
            buildLeftMenuElement(null, elementCT, leftMenuElements, index, indexedObjectName, true, parentIndex);
        }
        return leftMenuElements;
    }

    protected void buildLeftMenuElement(String icon, ElementCT model, List<TreeElement<ElementCT>> leftMenuElements, Integer index,
                                        String indexedObjectName, boolean showIndex,Integer parentIndex) {
        TreeElement<ElementCT> ret;
        if (index != null) {
            if(parentIndex != null)
                ret = new IndexedTreeElement<>(translateLabel(model.getLabel()), icon, model, index, indexedObjectName, showIndex, parentIndex);
            else
                ret = new IndexedTreeElement<>(translateLabel(model.getLabel()), icon, model, index, indexedObjectName, showIndex);
        } else {
            ret = new TreeElement<>(translateLabel(model.getLabel()), icon, model);
        }
        if (!model.getElement().isEmpty()) {
            for (ElementCT m : model.getElement()) {
                buildLeftMenuElement(null, m, ret.getChildren(), index, indexedObjectName, false, parentIndex);
            }
        }
        leftMenuElements.add(ret);
    }

    public void addLeftMenuElement(String parentId, List<TreeElement<ElementCT>> menu, List<TreeElement<ElementCT>> newElement) {
        TreeElement<ElementCT> parent = findLeftMenuElementByParent(parentId, menu);
        if (parent != null) {
            for (TreeElement<ElementCT> child : newElement) {
                parent.addChild(child);
            }
        }
    }

    public void removeLeftMenuElementChild(String parentId, List<TreeElement<ElementCT>> menu){
        TreeElement<ElementCT> parent = findLeftMenuElementByParent(parentId, menu);
        if (parent != null) {
            parent.getChildren().clear();
        }
    }

    @Deprecated
    public void groupLeftMenuElement(String elementId, List<TreeElement<ElementCT>> menu) {
        groupLeftMenuElement(elementId, menu, "");
    }

    public void groupLeftMenuElement(String elementId, List<TreeElement<ElementCT>> menu, String groupedObjectName) {
        TreeElement<ElementCT> parent = findLeftMenuElementByParent(elementId, menu);
        if (parent == null) {
            return;
        }
        if (parent.getChildren().size() > 10) {
            groupLeftMenuElementChild(parent, 1000, 1, groupedObjectName);
        }
    }

    private void groupLeftMenuElementChild(TreeElement<ElementCT> elementToGroup, int groupSize, int firstElementIndex, String groupedObjectName) {
        if (elementToGroup == null || (groupSize == 10 && elementToGroup.getChildren().size() <= 10)) {
            return;
        }
        while (groupSize > 10 && elementToGroup.getChildren().size() <= groupSize) {
            groupSize = groupSize / 10;
        }

        List<TreeElement<ElementCT>> childToGroup = elementToGroup.getChildren();
        elementToGroup.setChildren(new ArrayList<>());
        TreeElement<ElementCT> parentGroupElement = null;
        for (int i = 0; i < childToGroup.size(); i++) {

            if (i % groupSize == 0) {
                if (parentGroupElement != null && groupSize > 10) { // group element child before go to next
                    groupLeftMenuElementChild(parentGroupElement, groupSize / 10,
                        firstElementIndex + i - groupSize,
                        groupedObjectName);
                }
                boolean isLastGroup = i + groupSize >= childToGroup.size();
                int lastElementIndex = isLastGroup ? (childToGroup.size() - 1) % groupSize : groupSize - 1;
                parentGroupElement = new GroupTreeElement<>(firstElementIndex + i, lastElementIndex, groupedObjectName, elementToGroup.getObj());
                elementToGroup.addChild(parentGroupElement);
            }
            parentGroupElement.addChild(childToGroup.get(i));
        }

        if (parentGroupElement != null && groupSize > 10) {
            groupLeftMenuElementChild(parentGroupElement, groupSize / 10,
                firstElementIndex + childToGroup.size() - childToGroup.size() % groupSize,
                groupedObjectName);
        }
    }

    public TreeElement<ElementCT> findLeftMenuElementByParent(String parentElementId, List<TreeElement<ElementCT>> menu){
        TreeElement<ElementCT> parent;
        for (TreeElement<ElementCT> el : menu) {
            if (el.getObj().getId().equals(parentElementId)) {
                parent =  el;
            } else {
                parent = findLeftMenuElementByParent(parentElementId, el.getChildren());
            }
            if (parent != null){
                return parent;
            }
        }
        return null;
    }

    public TreeElement<ElementCT> findLeftMenuElementByIndex(int index, List<TreeElement<ElementCT>> menu){
        TreeElement<ElementCT> parent;
        for (TreeElement<ElementCT> el : menu) {
            if (el instanceof IndexedTreeElement && ((IndexedTreeElement)el).getIndex() == index) {
                parent =  el;
            } else {
                parent = findLeftMenuElementByIndex(index, el.getChildren());
            }
            if (parent != null){
                return parent;
            }
        }
        return null;
    }

    protected String translateLabel(String label) {
        if (label != null && label.length() > 2 && label.startsWith("$.")) {
            return messageService.getAllBundles().getMessage(label.substring(2));
        } else {
            return label;
        }
    }

    public List<TreeElement<DeclarationMessageDto>> buildMessagesLeftMenu(List<DeclarationMessageDto> messageDtoList) {
        List<TreeElement<DeclarationMessageDto>> leftMenuElements = new ArrayList<>();

        // find tree main nodes (roots)
        List<TreeElement<DeclarationMessageDto>> mainNodes = new ArrayList<>();

        for (DeclarationMessageDto message : messageDtoList) {
            if(message.getMetadata().getResponseTo() == null) {
                mainNodes.add(new TreeElement<>(generateMessageTreeElementName(message), generateMessageTreeElementIcon(message), message));
            }
        }

        // find tree sub-nodes
        for(TreeElement<DeclarationMessageDto> mainNode: mainNodes) {
            leftMenuElements.add(buildMessageTree(mainNode, messageDtoList));
        }

        return leftMenuElements;
    }

    TreeElement<DeclarationMessageDto> buildMessageTree(TreeElement<DeclarationMessageDto> root, List<DeclarationMessageDto> fullList) {

        List<DeclarationMessageDto> currentNodeChildren = new ArrayList<>();

        for (DeclarationMessageDto message : fullList) {
            if(root.getObj().getId().equals(message.getMetadata().getResponseTo())) {
                currentNodeChildren.add(message);
            }
        }

        if(currentNodeChildren.size() == 0) {
            return root;
        } else {
            for (DeclarationMessageDto currentChild : currentNodeChildren) {
                root.addChild(buildMessageTree(new TreeElement<>(
                    generateMessageTreeElementName(currentChild),
                    generateMessageTreeElementIcon(currentChild), currentChild), fullList));
            }
        }
        return root;
    }

    private String generateMessageTreeElementName(DeclarationMessageDto message) {

        if(message == null) {
            return null;
        }

        MessageDirectionEnum direction = DeclarationMessageHelper.getDirectionFromDto(message);
        String treeElementName = message.getName();

        if(message.getDirection() != null) {
            if(message.getDirection() != null && MessageDirectionEnum.OUT.equals(message.getDirection())) {
                treeElementName = String.format("%s  %s", treeElementName, "[className='ml-1'][icon='fas fa-sign-out-alt'][/className]");
            } else {
                treeElementName = String.format("%s %s", "[icon='fas fa-sign-in-alt']", treeElementName);
            }
        }

        if(direction != null) {
            if(isDirection) {
                treeElementName += " (";
                treeElementName += messageService.getAllBundles().getMessage(
                    "enum.pl.fhframework.dp.transport.enums.MessageDirectionEnum." + direction.name());
                treeElementName += ")";
            }
            return treeElementName;
        } else {
            log.debug("Message with id: " + message.getId() + "and repositoryId: " + message.getRepositoryId() + "do not have specified direction in otherMetadata - generating label without direction");
        }

        return treeElementName;
    }

    private String generateMessageTreeElementIcon(DeclarationMessageDto message) {

        if(message == null) {
            return null;
        }

        switch (DeclarationMessageHelper.getDirectionFromDto(message)) {
            case OUT: {
                return "fas fa-sign-out-alt";
            }
            case IN: {
                return "fas fa-sign-in-alt";
            }
            default:
                return "fas fa-sign-in-alt";
        }
    }

    private String generateMessageTreeElementIcon(MessageDirectionEnum messageDirectionEnum) {

        if(messageDirectionEnum == null) {
            return null;
        }

        switch (messageDirectionEnum) {
            case OUT: {
                return "[icon='fas fa-sign-out-alt']";
            }
            case IN: {
                return "[icon='fas fa-sign-in-alt']";
            }
            default:
                return "[icon='fas fa-sign-in-alt']";
        }
    }
}
