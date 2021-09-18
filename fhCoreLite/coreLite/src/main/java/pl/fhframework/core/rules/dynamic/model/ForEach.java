package pl.fhframework.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "ForEach", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"iter", "collection", "statements"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ForEach extends ForBaseLoop implements Loop {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "ForEachBlock";

    @XmlTransient
    @JsonIgnore
    public static final String ITER_FIELD_NAME = "iter";

    @XmlTransient
    @JsonIgnore
    public static final String COLLECTION_FIELD_NAME = "collection";

    @XmlAttribute
    private String collection;

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(ForEach.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(ForEach.ITER_FIELD_NAME, this.getIter()));
        fields.add(new BlocklyField(ForEach.COLLECTION_FIELD_NAME, formatter.apply(this.getCollection())).setEditorType(BlocklyField.EditorType.COMBO));
        block.setFields(fields);

        addStatements(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        ForEach forEachLoop = new ForEach();
        forEachLoop.setId(parsedBlock.getId());
        forEachLoop.setX(parsedBlock.getX());
        forEachLoop.setY(parsedBlock.getY());

        Optional<String> iter = parsedBlock.getFieldValue(ForEach.ITER_FIELD_NAME);
        iter.ifPresent(forEachLoop::setIter);

        Optional<String> collection = parsedBlock.getFieldValue(ForEach.COLLECTION_FIELD_NAME);
        collection.ifPresent(forEachLoop::setCollection);

        return forEachLoop;
    }

    @Override
    public void processValueChange(String name, String value) {
        switch (name) {
            case ForEach.ITER_FIELD_NAME:
                this.setIter(value);
                break;
            case ForEach.COLLECTION_FIELD_NAME:
                this.setCollection(value);
                break;
        }
    }
}