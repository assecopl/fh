package pl.fhframework.dp.commons.fh.outline;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.services.FhService;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 02.09.2019
 */

@Slf4j
@FhService
public class OutlineService {

    @Value("${fhdp.direction:true}")
    public boolean isDirection;

    @Autowired
    protected MessageService messageService;

    public List<ElementCT> generateOutline(String docType) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(Outline.class);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        String fileName = ("/outlines/" + docType + "_OUTLINE.xml");
        InputStream is = OutlineService.class.getResourceAsStream(fileName);
        Outline ret = (Outline) unmarshaller.unmarshal(is);
        if(ret.getElement() != null) {
            return ret.getElement().get(0).getElement();
        } else {
            return new ArrayList<>();
        }
    }

    public OutlineMapping findMappings(String ...docType){
        OutlineMapping mappings = new OutlineMapping();
        for(String doc: docType) {
            try {
                List<ElementCT> elementCTS = generateOutline(doc);
                buildMaps(elementCTS, mappings);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        return mappings;
    }

    private void buildMaps(List<ElementCT> list, OutlineMapping mapping) {
        if(list == null) return;
        for(ElementCT el: list) {
            mapping.getElements().put(el.getId(), el);
            if(el.getMappings() != null) {
                List<String> mappingsList = el.getMappings().getPointer();
                for(String p: mappingsList) {
                    putMapping(p.toLowerCase(), el.getId(), mapping);
                }
            }
            buildMaps(el.getElement(), mapping);
        }
    }

    private void putMapping(String pointerName, String elementId, OutlineMapping mapping){
        Set<String> elementIdList = mapping.getMappings().get(pointerName);
        if(null == elementIdList) {
            elementIdList = new HashSet<>();
            mapping.getMappings().put(pointerName, elementIdList);
        }
        elementIdList.add(elementId);
    }

    /**
     * Based on mappings in outline, retrieves element matching pointer from data model.
     * Finds the nearest sub pointer, if given is not defined
     * @param pointer
     * @return found element or null.
     */
    public ElementCT findElementFromPointer(String pointer, OutlineMapping mapping) {
        ElementCT ret = null;
        String p = pointer.toLowerCase();
        String id = null;
        List<String> mappingList = findMapping(pointer, mapping);
        if(mappingList != null && mappingList.size() > 0) {
            String firstIdElement = mappingList.get(0);
            ret = mapping.getElements().get(firstIdElement);
        }
        return ret;
    }

    public List<ElementCT> findAllElementFromPointer(String pointer, OutlineMapping mapping){
        List<String> mappingList = findMapping(pointer, mapping);
        if(null != mappingList) {
            return mappingList.stream().map(x -> mapping.getElements().get(x)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<String> findMapping(String pointer, OutlineMapping mapping){
        String pointerName = pointer.toLowerCase();
        Set<String> mappingIdList = new HashSet<>();
        do {
            Set<String> mappingsId = mapping.getMappings().get(pointerName);
            if(mappingsId != null) {
                mappingIdList.addAll(mappingsId);
            }
            pointerName = reducePointer(pointerName);
        } while(!pointerName.isEmpty());
        return new ArrayList<>(mappingIdList);
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

    @Data
    public static class OutlineMapping {
        private Map<String, ElementCT> elements = new HashMap<>();
        private Map<String, Set<String>> mappings = new HashMap<>();
    }
}
