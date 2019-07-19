package pl.fhframework.model.forms;

import java.util.Arrays;

/**
 * Created by krzysztof.kobylarek on 2016-10-13.
 * All visual components [tags] in one place for convenience.
 */
public enum Tag {
    
    NONE("none"), // indicates absence of tag in set to not trigger exception
    FORM_ELEMENT("FormElement"),
    FORM("Form", Attributes.LABEL, Attributes.CONTAINER, Attributes.MODAL, Attributes.LAYOUT, Attributes.BLOCKED),
    TABLE("Table"),
    TABLE_ROW("TableRow"),
    TABLE_CELL("TableCell"),
    RADIO_OPTION("RadioOption"),
    RADIO_OPTION_GROUP("RadioOptionsGroup"),
    READ_ONLY("ReadOnly"),
    OUTPUT_LABEL("OutputLabel"),
    CANVAS("Canvas");

    private String tagTypeName = "";
    private Attributes[] attributes;

    Tag(String tagName){
        this.tagTypeName=tagName;
    }
    Tag(String tagName, Attributes... attributes){
        this(tagName);
        this.attributes = attributes;
    }

    public String getTagTypeName(){
        return tagTypeName;
    }

    public static Tag getTagByTypeName(String tagTypeName) {
        return Arrays.stream(Tag.values()).filter( (t) -> t.getTagTypeName().equals(tagTypeName))
                .findFirst().orElse(NONE);
    }

    @Override
    public String toString() {
        return name();
    }

    public enum Attributes {
        HEIGHT("height"),
        LABEL("label"), CONTAINER("container"),
        LAYOUT("layout"), MODAL("modal"),
        BLOCKED("blocked");
        String attrName;
        Attributes (String attrName) {
            this.attrName = attrName;
        }
    }
}
