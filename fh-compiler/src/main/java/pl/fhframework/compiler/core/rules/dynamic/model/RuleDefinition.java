package pl.fhframework.compiler.core.rules.dynamic.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.rules.dynamic.model.RuleElement;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.dynamic.model.StatementsList;
import pl.fhframework.core.rules.service.RuleConsts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@Getter
@Setter
@XmlRootElement(name = "RuleDefinition", namespace = RuleConsts.RULE_XSD)
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class RuleDefinition implements StatementsList {
    @XmlElementRef
    private List<Statement> statements = new LinkedList<>();

    public Optional<RuleElement> findStatementAndPop(String id) {
        return findStatement(id, statements, this, true);
    }

    Optional<RuleElement> findStatement(String id) {
        return findStatement(id, statements, this, false);
    }

    private Optional<RuleElement> findStatement(String id, Collection<? extends RuleElement> statements, StatementsList surroundParent, boolean pop) {

        for (RuleElement ruleElement : statements) {
            Statement statement = (Statement) ruleElement;

            if (surroundParent != null) {
                if (statement.getOrGenerateId().equals(id)) {
                    if (pop) {
                        statements.remove(statement);
                    }
                    statement.setSurroundParent(surroundParent);
                    return Optional.of(statement);
                }
            }

            for (Map.Entry<String, RuleElement> entry : statement.getComplexValues().entrySet()) {
                if (entry.getValue().getOrGenerateId().equals(id)) {
                    if (pop) {
                        statement.processValueChange(entry.getKey(), (RuleElement) null);
                    }
                    entry.getValue().setParent(statement);
                    entry.getValue().setSurroundParent(null);
                    return Optional.of(entry.getValue());
                }
            }
            Optional<RuleElement> foundStatement = findStatement(id, statement.getComplexValues().values(), null, pop);
            if (foundStatement.isPresent()) {
                return foundStatement;
            }

            if (statement instanceof StatementsList) {
                StatementsList statementsList = (StatementsList) statement;
                foundStatement = findStatement(id, statementsList.getStatements(), statementsList, pop);
                if (foundStatement.isPresent()) {
                    return foundStatement;
                }
            }
        }

        return Optional.empty();
    }
}
