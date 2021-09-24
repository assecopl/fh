package pl.fhframework.core.rules.dynamic.model.predicates;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.dynamic.model.*;
import pl.fhframework.core.rules.dynamic.model.dataaccess.From;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.*;
import java.util.*;
import java.util.function.Function;

/**
 * Created by pawel.ruta on 2017-08-18.
 */
@Getter
@Setter
@XmlRootElement(name = "ExistsIn", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ExistsInCondition extends ComplexCondition {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "ExistsInBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TAG_QUERY_NAME = "ExistsInQueryBlock";

    @XmlTransient
    @JsonIgnore
    public static final String COLLECTION_FIELD_NAME = "collection";

    @XmlTransient
    @JsonIgnore
    public static final String ITER_FIELD_NAME = "iter";

    @XmlTransient
    @JsonIgnore
    public static final String WITH_FIELD_NAME = "with";

    @XmlTransient
    @JsonIgnore
    public static final String WHEN_FIELD_NAME = "when";

    @XmlAttribute
    private String collection;

    @XmlAttribute
    private String iter;

    @XmlElement(name = "With")
    private Condition with;

    @XmlElement(name = "When")
    private Condition when;

    @Override
    protected String getTagName() {
        if (isQuery()) {
            return ExistsInCondition.TAG_QUERY_NAME;
        }
        return ExistsInCondition.TAG_NAME;
    }

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = super.convertToBlockly(formatter);

        List<BlocklyField> fields = new ArrayList<>();
        if (!isQuery()) {
            fields.add(new BlocklyField(COLLECTION_FIELD_NAME, formatter.apply(this.getCollection())).setEditorType(BlocklyField.EditorType.COMBO));
            fields.add(new BlocklyField(From.ITER_FIELD_NAME, this.getIter()));
            block.setFields(fields);
        }
        else {
            addStatements(block, formatter);
        }

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        ExistsInCondition existsInCondition = new ExistsInCondition();
        existsInCondition.convertFromBlocklyInternal(parsedBlock);

        Optional<String> collection = parsedBlock.getFieldValue(From.COLLECTION_FIELD_NAME);
        collection.ifPresent(existsInCondition::setCollection);
        Optional<String> iter = parsedBlock.getFieldValue(From.ITER_FIELD_NAME);
        iter.ifPresent(existsInCondition::setIter);

        return existsInCondition;
    }

    public static ExistsInCondition of(String collection, String iter, Predicate predicate) {
        ExistsInCondition existsInCondition = new ExistsInCondition();
        existsInCondition.setCollection(collection);
        existsInCondition.setIter(iter);
        existsInCondition.setWith(new Condition());
        if (predicate != null) {
            existsInCondition.getWith().setPredicate(predicate);
        }

        return existsInCondition;
    }

    public static ExistsInCondition of(From query) {
        ExistsInCondition existsInCondition = new ExistsInCondition();
        existsInCondition.getStatements().add(query);

        return existsInCondition;
    }

    @Override
    public void processValueChange(String name, String value) {
        switch (name) {
            case COLLECTION_FIELD_NAME:
                this.setCollection(value);
                break;
            case ITER_FIELD_NAME:
                this.setIter(value);
                break;
        }
    }

    @Override
    public void processValueChange(String name, RuleElement value) {
        if (WITH_FIELD_NAME.equals(name)) {
            if (value != null) {
                if (getWith() == null) {
                    setWith(new Condition());
                }
                getWith().setPredicate((Predicate) value);
            }
            else {
                setWith(null);
            }
        }
        if (WHEN_FIELD_NAME.equals(name)) {
            if (value != null) {
                if (getWhen() == null) {
                    setWhen(new Condition());
                }
                getWhen().setPredicate((Predicate) value);
            }
            else {
                setWhen(null);
            }
        }
    }

    @Override
    public Map<String, RuleElement> getComplexValues() {
        Map<String, RuleElement> complexValues = new HashMap<>();

        if (getWith() != null && getWith().getPredicate() != null) {
            complexValues.put(WITH_FIELD_NAME, getWith().getPredicate());
        }
        if (getWhen() != null && getWhen().getPredicate() != null) {
            complexValues.put(WHEN_FIELD_NAME, getWhen().getPredicate());
        }

        return complexValues;
    }

    @Override
    public Class getInputType(String name) {
        if ("statements".equals(name)) {
            return StatementsList.class;
        }

        return Statement.class;
    }

    public boolean isQuery() {
        if (with == null && collection == null && iter == null) {
            return true;
        }

        return false;
    }
}

