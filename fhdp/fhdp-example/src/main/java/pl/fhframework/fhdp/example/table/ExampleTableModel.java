package pl.fhframework.fhdp.example.table;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ExampleTableModel {

    private List<ListElements> listElements = new ArrayList<>();
    private ListElements selectedListElement;
    private AttributeElement selectedAttributeElement;
    private AccessibilityEnum accessibilityEnum = AccessibilityEnum.EDIT;
    @Getter
    @Setter
    public static class ListElements {
        private long id;
        private String name;
        private String description;
        private List<AttributeElement> attributeElementList;

        public ListElements(){
            attributeElementList = new ArrayList<>();
        }

        public ListElements(long id, String name, String description, List<AttributeElement> attributeElementList) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.attributeElementList = attributeElementList;
        }
    }

    @Getter
    @Setter
    public static class AttributeElement {
        private long id;
        private String name;

        public AttributeElement() {
        }

        public AttributeElement(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
