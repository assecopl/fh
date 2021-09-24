package pl.fhframework.core.rules.dynamic.model.dataaccess;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.dynamic.model.*;
import pl.fhframework.core.rules.service.RuleConsts;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "From", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class From extends StatementWithList implements DataAccess {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "FromBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TAG_DB_NAME = "QueryDbBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TYPE_FIELD_NAME = "type";

    @XmlTransient
    @JsonIgnore
    public static final String COLLECTION_FIELD_NAME = "collection";

    @XmlTransient
    @JsonIgnore
    public static final String ITER_FIELD_NAME = "iter";

    @XmlTransient
    @JsonIgnore
    public static final String HOLDER_FIELD_NAME = "holder";

    @XmlTransient
    @JsonIgnore
    public static final String PAGEABLE_FIELD_NAME = "pageable";


    @XmlAttribute
    private String type;

    @XmlAttribute
    private String collection;

    @XmlAttribute
    private String iter;

    @XmlAttribute
    private String holder;

    @XmlAttribute
    private Boolean pageable = Boolean.FALSE;


    @XmlElementRef
    private List<Statement> statements = new LinkedList<>();

    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private Class typeClass;

    public From() {
    }

    public From(From other) {
        this.type = other.type;
        this.collection = other.collection;
        this.iter = other.iter;
        this.holder = other.holder;
        this.pageable = other.pageable;
        this.statements = new LinkedList<>(other.statements);
    }

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());

        List<BlocklyField> fields = new ArrayList<>();
        if (this.getCollection() != null) {
            block.setType(From.TAG_NAME);
            fields.add(new BlocklyField(From.COLLECTION_FIELD_NAME, formatter.apply(this.getCollection())).setEditorType(BlocklyField.EditorType.COMBO));
        }
        else {
            block.setType(From.TAG_DB_NAME);
            fields.add(new BlocklyField(From.TYPE_FIELD_NAME, this.getType()).setEditorType(BlocklyField.EditorType.COMBO_FIXED).setFieldType(Type.class));
        }
        block.setX(this.getX());
        block.setY(this.getY());

        fields.add(new BlocklyField(From.ITER_FIELD_NAME, this.getIter()));
        fields.add(new BlocklyField(From.HOLDER_FIELD_NAME, formatter.apply(this.getHolder())).setEditorType(BlocklyField.EditorType.COMBO));
        fields.add(new BlocklyField(From.PAGEABLE_FIELD_NAME, this.getPageable().toString()).setEditorType(BlocklyField.EditorType.FIXED).setFieldType(boolean.class));
        block.setFields(fields);

        addStatements(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        From from = new From();
        from.setId(parsedBlock.getId());
        from.setX(parsedBlock.getX());
        from.setY(parsedBlock.getY());

        Optional<String> type = parsedBlock.getFieldValue(From.TYPE_FIELD_NAME);
        type.ifPresent(from::setType);
        Optional<String> collection = parsedBlock.getFieldValue(From.COLLECTION_FIELD_NAME);
        collection.ifPresent(from::setCollection);
        Optional<String> iter = parsedBlock.getFieldValue(From.ITER_FIELD_NAME);
        iter.ifPresent(from::setIter);
        Optional<String> holder = parsedBlock.getFieldValue(From.HOLDER_FIELD_NAME);
        holder.ifPresent(from::setHolder);
        Optional<String> pageable = parsedBlock.getFieldValue(From.PAGEABLE_FIELD_NAME);
        pageable.ifPresent(val -> from.setPageable(StringUtils.equalsIgnoreCase("true", val)));

        return from;
    }

    @Override
    public void processValueChange(String name, String value) {
        switch (name) {
            case TYPE_FIELD_NAME:
                this.setType(value);
                break;
            case COLLECTION_FIELD_NAME:
                this.setCollection(value);
                break;
            case ITER_FIELD_NAME:
                this.setIter(value);
                break;
            case HOLDER_FIELD_NAME:
                this.setHolder(value);
                break;
            case PAGEABLE_FIELD_NAME:
                this.setPageable(StringUtils.equalsIgnoreCase("true", value));
                break;
        }
    }

    public static From of(Class type, String iter, DataAccess... conditions) {
        From from = new From();
        from.setTypeClass(type);
        from.setType(type.getName());
        from.setIter(iter);
        if (conditions != null && conditions.length > 0) {
            from.getStatements().addAll((Collection)Arrays.asList(conditions));
        }

        return from;
    }

    public boolean isDbQuery() {
        return StringUtils.isNullOrEmpty(getCollection());
    }

    public <T> Optional<T> getElement(Class<T> elementClass) {
        return statements.stream().filter(elementClass::isInstance).map(elementClass::cast).findFirst();
    }
}