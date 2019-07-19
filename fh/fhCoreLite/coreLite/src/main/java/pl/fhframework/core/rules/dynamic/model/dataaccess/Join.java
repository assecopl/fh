package pl.fhframework.core.rules.dynamic.model.dataaccess;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyBlock;
import pl.fhframework.core.rules.dynamic.blockly.BlocklyField;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.dynamic.model.StatementWithList;
import pl.fhframework.core.rules.dynamic.model.predicates.DefinedCondition;
import pl.fhframework.core.rules.service.RuleConsts;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@XmlRootElement(name = "Join", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Join extends StatementWithList implements DataAccess {
    @XmlTransient
    @JsonIgnore
    public static final String TAG_NAME = "JoinBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TAG_TYPE_NAME = "JoinTypeBlock";

    @XmlTransient
    @JsonIgnore
    public static final String TYPE_FIELD_NAME = "type";

    @XmlTransient
    @JsonIgnore
    public static final String PATH_FIELD_NAME = "path";

    @XmlTransient
    @JsonIgnore
    public static final String ALIAS_FIELD_NAME = "alias";

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String path;

    @XmlAttribute
    private String alias;

    @XmlElementRef
    private List<Statement> statements = new LinkedList<>();

    @SkipSnapshot
    @XmlTransient
    @JsonIgnore
    private Class typeClass;

    public Join() {
    }

    public Join(Join other) {
        this.type = other.type;
        this.path = other.path;
        this.alias = other.alias;
        this.statements = other.statements;
    }

    @Override
    public BlocklyBlock convertToBlockly(Function<String, String> formatter) {
        BlocklyBlock block = new BlocklyBlock();
        block.setId(this.getOrGenerateId());

        List<BlocklyField> fields = new ArrayList<>();
        if (this.getPath() != null) {
            block.setType(Join.TAG_NAME);
            fields.add(new BlocklyField(Join.PATH_FIELD_NAME, formatter.apply(this.getPath())).setEditorType(BlocklyField.EditorType.COMBO));
        }
        else {
            block.setType(Join.TAG_TYPE_NAME);
            fields.add(new BlocklyField(Join.TYPE_FIELD_NAME, this.getType()).setEditorType(BlocklyField.EditorType.COMBO_FIXED).setFieldType(Type.class));
        }
        block.setX(this.getX());
        block.setY(this.getY());

        fields.add(new BlocklyField(Join.ALIAS_FIELD_NAME, this.getAlias()));
        block.setFields(fields);

        addStatements(block, formatter);

        return block;
    }

    public static RuleElement convertFromBlockly(BlocklyBlock parsedBlock) {
        Join from = new Join();
        from.setId(parsedBlock.getId());
        from.setX(parsedBlock.getX());
        from.setY(parsedBlock.getY());

        Optional<String> type = parsedBlock.getFieldValue(Join.TYPE_FIELD_NAME);
        type.ifPresent(from::setType);
        Optional<String> path = parsedBlock.getFieldValue(Join.PATH_FIELD_NAME);
        path.ifPresent(from::setPath);
        Optional<String> alias = parsedBlock.getFieldValue(Join.ALIAS_FIELD_NAME);
        alias.ifPresent(from::setAlias);

        return from;
    }

    @Override
    public void processValueChange(String name, String value) {
        switch (name) {
            case TYPE_FIELD_NAME:
                this.setType(value);
                break;
            case PATH_FIELD_NAME:
                this.setPath(value);
                break;
            case ALIAS_FIELD_NAME:
                this.setAlias(value);
                break;
        }
    }

    public static Join of(Class type, String alias, DefinedCondition onCondition) {
        Join join = new Join();
        join.setTypeClass(type);
        join.setType(type.getName());
        join.setAlias(alias);
        if (onCondition != null) {
            join.getStatements().add(onCondition);
        }

        return join;
    }

    public static Join of(String path, String alias) {
        Join join = new Join();
        join.setPath(path);
        join.setAlias(alias);

        return join;
    }
}
