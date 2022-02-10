package pl.fhframework.compiler.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.rules.meta.RuleInfo;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Permission;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.aspects.snapshots.model.ISnapshotEnabled;
import pl.fhframework.aspects.snapshots.model.SkipSnapshot;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.dynamic.model.StatementsList;
import pl.fhframework.core.rules.service.RuleConsts;
import pl.fhframework.core.util.StringUtils;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Rule", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "label", "description", "inputParams", "outputParams", "ruleDefinitions", "permissions", "categories"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Rule implements ISnapshotEnabled, Serializable, Cloneable {

    @XmlAttribute
    @XmlID
    private String id;

    @XmlAttribute
    private String label;

    @XmlAttribute
    private String description;

    @XmlAttribute
    private TransactionTypeEnum transactionType;

    @XmlAttribute
    private RuleType ruleType;

    @XmlTransient
    @JsonIgnore
    @SkipSnapshot
    private RuleInfo ruleInfo;

    @XmlElement(name = "ParameterDefinition", type = ParameterDefinition.class, namespace = RuleConsts.RULE_XSD)
    @XmlElementWrapper(name = "Input", namespace = RuleConsts.RULE_XSD)
    private List<ParameterDefinition> inputParams = new LinkedList<>();

    @XmlElement(name = "ParameterDefinition", type = ParameterDefinition.class, namespace = RuleConsts.RULE_XSD)
    @XmlElementWrapper(name = "Output", namespace = RuleConsts.RULE_XSD)
    private List<ParameterDefinition> outputParams = new LinkedList<>();

    @XmlElementRef
    private List<RuleDefinition> ruleDefinitions = new ArrayList<>();

    @XmlAttribute
    private boolean plpgsql;

    @XmlElements({
            @XmlElement(name = "Permission", type = Permission.class)
    })
    @XmlElementWrapper(name = "Permissions")
    private List<Permission> permissions = new LinkedList<>();

    @XmlElementWrapper(name = "Categories")
    @XmlElement(name = "Category")
    private List<String> categories = new LinkedList<>();

    @JsonIgnore
    public RuleDefinition getRuleDefinition() {
        if (ruleDefinitions.isEmpty()) {
            return null;
        }
        return ruleDefinitions.get(0);
    }

    public void setRuleDefinition(RuleDefinition ruleDefinition) {
        if (ruleDefinition == null) {
            if (!ruleDefinitions.isEmpty()) {
                ruleDefinitions.remove(0);
            }
        } else {
            if (!ruleDefinitions.isEmpty()) {
                ruleDefinitions.set(0, ruleDefinition);
            } else {
                ruleDefinitions.add(ruleDefinition);
            }
        }
    }

    @JsonIgnore
    public RuleDefinition getOrCreateEmptyRuleDefinition() {
        RuleDefinition ruleDefinition = getRuleDefinition();
        if (ruleDefinition != null && ruleDefinition.getStatements().isEmpty()) {
            return ruleDefinition;
        }
        RuleDefinition newRuleDefinition = new RuleDefinition();
        getRuleDefinitions().add(newRuleDefinition);

        return newRuleDefinition;
    }

    public void clearEmptyRuleDefinition() {
        if (getRuleDefinitions().size() > 1) {
            getRuleDefinitions().removeIf(ruleDefinition -> ruleDefinition.getStatements().isEmpty());
        }
    }

    public Optional<RuleElement> findStatement(String id) {
        for (RuleDefinition ruleDefinition : getRuleDefinitions()) {
            Optional<RuleElement> statement = ruleDefinition.findStatement(id);
            if (statement.isPresent()) {
                return statement;
            }
        }

        return Optional.empty();
    }

    public <T extends Statement> List<T> findElements(Class<T> elementType) {
        List<T> elementsList = new ArrayList<T>();
        for (RuleDefinition ruleDefinition : getRuleDefinitions()) {
            findElements(ruleDefinition.getStatements(), elementType, elementsList);
        }

        return elementsList;
    }

    public String getName() {
        return getId() + "." + StringUtils.firstLetterToLower(DynamicClassName.forClassName(getId()).getBaseClassName());
    }

    private <T extends Statement> void findElements(List<Statement> statementsList, Class<T> elementType, List<T> elementsList) {
        for (Statement statement : statementsList) {
            if (elementType.isAssignableFrom(statement.getClass())) {
                elementsList.add((T) statement);
            }
            if (StatementsList.class.isAssignableFrom(statement.getClass())) {
                findElements(((StatementsList) statement).getStatements(), elementType, elementsList);
            }
        }
    }
}
