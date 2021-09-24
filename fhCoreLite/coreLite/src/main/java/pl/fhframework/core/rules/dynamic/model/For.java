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
@XmlRootElement(name = "For", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"iter", "start", "end", "incr", "statements"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class For extends ForBaseLoop implements Loop {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "ForLoopBlock";

    @XmlTransient
    @JsonIgnore
    public static final String ITER_FIELD_NAME = "iter";

    @XmlTransient
    @JsonIgnore
    public static final String START_FIELD_NAME = "start";

    @XmlTransient
    @JsonIgnore
    public static final String END_FIELD_NAME = "end";

    @XmlTransient
    @JsonIgnore
    public static final String INCR_FIELD_NAME = "incr";

    @XmlAttribute
    private String start;

    @XmlAttribute
    private String end;

    @XmlAttribute
    private String incr;

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());
        block.setType(For.TAG_NAME);
        block.setX(this.getX());
        block.setY(this.getY());

        List<BlocklyField> fields = new ArrayList<>();
        fields.add(new BlocklyField(For.ITER_FIELD_NAME, this.getIter()));
        fields.add(new BlocklyField(For.START_FIELD_NAME, formatter.apply(this.getStart())).setEditorType(BlocklyField.EditorType.COMBO));
        fields.add(new BlocklyField(For.END_FIELD_NAME, formatter.apply(this.getEnd())).setEditorType(BlocklyField.EditorType.COMBO));
        fields.add(new BlocklyField(For.INCR_FIELD_NAME, formatter.apply(this.getIncr())).setEditorType(BlocklyField.EditorType.COMBO));
        block.setFields(fields);

        addStatements(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        For forLoop = new For();
        forLoop.setId(parsedBlock.getId());
        forLoop.setX(parsedBlock.getX());
        forLoop.setY(parsedBlock.getY());

        Optional<String> start = parsedBlock.getFieldValue(For.START_FIELD_NAME);
        start.ifPresent(forLoop::setStart);

        Optional<String> incr = parsedBlock.getFieldValue(For.INCR_FIELD_NAME);
        incr.ifPresent(forLoop::setIncr);

        Optional<String> iter = parsedBlock.getFieldValue(For.ITER_FIELD_NAME);
        iter.ifPresent(forLoop::setIter);

        Optional<String> end = parsedBlock.getFieldValue(For.END_FIELD_NAME);
        end.ifPresent(forLoop::setEnd);

        return forLoop;
    }

    @Override
    public void processValueChange(String name, String value) {
        switch (name) {
            case For.START_FIELD_NAME:
                this.setStart(value);
                break;
            case For.END_FIELD_NAME:
                this.setEnd(value);
                break;
            case For.INCR_FIELD_NAME:
                this.setIncr(value);
                break;
            case For.ITER_FIELD_NAME:
                this.setIter(value);
                break;
        }
    }
}