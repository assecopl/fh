package pl.fhframework.compiler.core.rules.dynamic.generator;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyResolution;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.CompiledClassesHelper;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.rules.builtin.CastUtils;
import pl.fhframework.core.rules.dynamic.model.Statement;
import pl.fhframework.core.rules.dynamic.model.ValidationMessage;
import pl.fhframework.core.rules.dynamic.model.dataaccess.*;
import pl.fhframework.core.rules.dynamic.model.predicates.*;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.validation.IValidationMessages;
import pl.fhframework.validation.ValidationRuleBase;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class BlocklyFhGenerator extends BlocklyJavaGenerator {
    protected final Rule rule;

    public BlocklyFhGenerator(Rule rule, DependenciesContext dependenciesContext) {
        super(rule.getInputParams(), rule.getRuleDefinition().getStatements(), rule.getOutputParams(), dependenciesContext);
        this.rule = rule;
    }

    protected void generateFrom(From from, Map<String, Type> contextVars, GenerationContext fromSection, boolean existsExpression) {
        if (StringUtils.isNullOrEmpty(from.getCollection())) {
            surroundParents.push(from);
            if (from.getPageable() != null && from.getPageable()) {
                generateQueryPageable(from, contextVars, fromSection);
            } else {
                generateQuery(from, contextVars, fromSection, !existsExpression);
                if (existsExpression) {
                    fromSection.addCode(" > 0");
                }
            }
            surroundParents.pop();
        }
        else {
            super.generateFrom(from, contextVars, fromSection, existsExpression);
        }
    }

    @Override
    protected void generateValidationMessage(ValidationMessage validationMessage, Map<String, Type> contextVars, GenerationContext methodSection) {
        if (rule.getRuleType() == RuleType.ValidationRule) {
            methodSection.addLineWithIndent(1, "addCustomMessage(%s, %s, %s, %s);",
                    StringUtils.isNullOrEmpty(validationMessage.getObject()) ? "null" : generateExpression(validationMessage.getObject(), contextVars),
                    StringUtils.isNullOrEmpty(validationMessage.getField()) ? "null" : generateExpression(validationMessage.getField(), contextVars),
                    generateExpression(validationMessage.getMessage(), contextVars),
                    getLevelStr(validationMessage.getLevel()));
        }
    }

    private String getLevelStr(String level) {
        PresentationStyleEnum levelEnum;
        switch (ValidationMessage.ValidationLevelEnum.fromString(level)) {
            case Blocker:
                levelEnum = PresentationStyleEnum.BLOCKER;
                break;
            case Error:
                levelEnum = PresentationStyleEnum.ERROR;
                break;
            case Warning:
                levelEnum = PresentationStyleEnum.WARNING;
                break;
            case Info:
                levelEnum = PresentationStyleEnum.INFO;
                break;
            default:
                throw new FhException("Unknown validation level");
        }
        return String.format("%s.%s", toTypeLiteral(PresentationStyleEnum.class), levelEnum.name());
    }

    private void generateQuery(From from, Map<String, Type> contextVars, GenerationContext fromSection, boolean listResult) {
        String storeMethod = listResult ? "storeFind" : "storeResult";
        GenerationContext fromQuerySection = new GenerationContext();
        generateFromQuery(from, contextVars, new HashSet<>(), fromQuerySection, rule);
        if (!listResult) {
            fromSection.addCode("%s.toIntegerStatic(", toTypeLiteral(CastUtils.class));
        }
        fromSection.addCode("__storeAccessService.%s(%s)",
                storeMethod,
                fromQuerySection.resolveCode());
        if (!listResult) {
            fromSection.addCode(")");
        }
    }

    private void generateQueryPageable(From from, Map<String, Type> contextVars, GenerationContext fromSection) {
        List<Statement> statements = from.getStatements().stream().filter(x -> !(x instanceof Limit || x instanceof Offset)).collect(Collectors.toList());

        GenerationContext countCode = new GenerationContext();
        From count = new From(from);
        count.getStatements().add(new Count());
        generateQuery(count, contextVars, countCode, false);

        contextVars.put("__pageable", Pageable.class);
        contextVars.put("__count", Integer.class);
        fromSection.addLine("new %s<>(__pageable -> {", toTypeLiteral(PageModel.class));
        fromSection.addLine("%s __count = %s;", toTypeLiteral(Integer.class), countCode.resolveCode());
        fromSection.addLine("__pageable = %s.limitPageNumber(__pageable, __count);", toTypeLiteral(CompiledClassesHelper.class), countCode.resolveCode());
        fromSection.addLine("return new %s<>(", toTypeLiteral(PageImpl.class));
        From page = new From(from);
        Limit limit = new Limit();
        limit.setValue("__pageable.getPageSize()");
        page.getStatements().add(limit);
        Offset offset = new Offset();
        offset.setValue("__pageable.getPageSize() * __pageable.getPageNumber()");
        page.getStatements().add(offset);
        SortBy sortBy = page.getElement(SortBy.class).orElse(new SortBy());
        sortBy.setSortOrderExp("__pageable.getSort()");
        page.getStatements().add(sortBy);
        generateQuery(page, contextVars, fromSection, true);
        fromSection.addCode(",\n__pageable,\n__count\n);})");
        contextVars.remove("__pageable");
        contextVars.remove("__count");
    }

    private void generateFromQuery(From from, Map<String, Type> contextVars, Set<String> aliases, GenerationContext fromSection, Rule rule) {
        DependencyResolution resolution = dependenciesContext.resolve(DynamicClassName.forClassName(from.getType()));
        fromSection.addCode("%s.of(%s.class, \"%s\"",
                toTypeLiteral(From.class),
                resolution.getFullClassName(),
                from.getIter());

        Set<String> newAliases = new HashSet<>(aliases);
        newAliases.add(from.getIter());

        from.getStatements().stream().filter(DataAccess.class::isInstance).forEach(dataAccess -> {
            GenerationContext dataAccessCode = new GenerationContext();
            generateDataAccessQuery(dataAccess, contextVars, newAliases, dataAccessCode, rule);
            fromSection.addCode(",\n%s", dataAccessCode.resolveCode());
        });

        fromSection.addCode(")");
    }

    private void generateDataAccessQuery(Statement statement, Map<String, Type> contextVars, Set<String> aliases, GenerationContext methodSection, Rule rule) {
        if (Filter.class.isInstance(statement)) {
            generateFilterQuery(Filter.class.cast(statement), contextVars, aliases, methodSection, rule);
        } else if (SortBy.class.isInstance(statement)) {
            generateSortByQuery(SortBy.class.cast(statement), contextVars, aliases, methodSection, rule);
        } else if (Offset.class.isInstance(statement)) {
            generateOffsetQuery(Offset.class.cast(statement), contextVars, aliases, methodSection);
        } else if (Limit.class.isInstance(statement)) {
            generateLimitQuery(Limit.class.cast(statement), contextVars, aliases, methodSection);
        } else if (Count.class.isInstance(statement)) {
            generateCountQuery(Count.class.cast(statement), contextVars, aliases, methodSection);
        } else {
            throw new FhException(String.format("Unknown data access rule: %s", statement.getClass().getSimpleName()));
        }
    }

    private void generateFilterQuery(Filter filter, Map<String, Type> contextVars, Set<String> aliases, GenerationContext methodSection, Rule rule) {
        From from = (From) surroundParents.peek();
        methodSection.addCode("%s.of(", toTypeLiteral(Filter.class));

        // must be 1 element, otherwise validation error
        if (filter.getStatements().size() > 0) {
            GenerationContext conditionSection = new GenerationContext();
            generatePredicateQuery((DefinedCondition) filter.getStatements().get(0), contextVars, aliases, conditionSection);
            methodSection.addCode(conditionSection.resolveCode());
        }

        methodSection.addCode(")");
    }

    private void generateSortByQuery(SortBy sortBy, Map<String, Type> contextVars, Set<String> aliases, GenerationContext methodSection, Rule rule) {
        From from = (From) surroundParents.peek();

        if (!StringUtils.isNullOrEmpty(sortBy.getSortOrderExp())) {
            methodSection.addCode("%s.of(%s, \"%s\", ", toTypeLiteral(SortBy.class), sortBy.getSortOrderExp(), from.getIter());
        }
        methodSection.addCode("%s.of(%s)", toTypeLiteral(SortBy.class),
                sortBy.getStatements().stream().filter(SortField.class::isInstance).map(SortField.class::cast).
                        map(sortfield -> {
                            String sortFieldCode = String.format("%s.of(\"%s\", \"%s\")",
                                    toTypeLiteral(SortField.class), sortfield.getValue(), sortfield.getDirection());
                            if (sortfield.getWhen() != null && sortfield.getWhen().getPredicate() != null) {
                                GenerationContext whenSection = new GenerationContext();
                                generatePredicate(sortfield.getWhen().getPredicate(), contextVars, whenSection);
                                sortFieldCode = String.format("((%s) ? %s : null)", whenSection.resolveCode(), sortFieldCode);
                            }
                            return sortFieldCode;
                        }).collect(Collectors.joining(",\n")));
        if (!StringUtils.isNullOrEmpty(sortBy.getSortOrderExp())) {
            methodSection.addCode(")");
        }
    }

    private void generateOffsetQuery(Offset offset, Map<String, Type> contextVars, Set<String> aliases, GenerationContext methodSection) {
        methodSection.addCode("%s.of(%s)", toTypeLiteral(Offset.class), generateExpression(offset.getValue(), contextVars));
    }

    private void generateLimitQuery(Limit limit, Map<String, Type> contextVars, Set<String> aliases, GenerationContext methodSection) {
        methodSection.addCode("%s.of(%s)", toTypeLiteral(Limit.class), generateExpression(limit.getValue(), contextVars));
    }

    private void generateCountQuery(Count count, Map<String, Type> contextVars, Set<String> aliases, GenerationContext methodSection) {
        methodSection.addCode("%s.of()", toTypeLiteral(Count.class));
    }

    private void generatePredicateQuery(DefinedCondition predicate, Map<String, Type> contextVars, Set<String> aliases, GenerationContext conditionSection) {
        GenerationContext predicateSection = conditionSection;
        if (predicate.getWhen() != null && predicate.getWhen().getPredicate() != null) {
            predicateSection = new GenerationContext();
        }

        if (ExistsInCondition.class.isInstance(predicate)) {
            generateExistsInConditionQuery(ExistsInCondition.class.cast(predicate), contextVars, aliases, predicateSection);
        } else if (ComplexCondition.class.isInstance(predicate)) {
            generateComplexConditionQuery(ComplexCondition.class.cast(predicate), contextVars, aliases, predicateSection);
        } else if (CompareCondition.class.isInstance(predicate)) {
            generateCompareConditionQuery(CompareCondition.class.cast(predicate), contextVars, aliases, predicateSection);
        } else {
            throw new FhException(String.format("Unknown predicate type: %s", predicate.getClass().getSimpleName()));
        }

        if (predicate.getWhen() != null && predicate.getWhen().getPredicate() != null) {
            GenerationContext whenSection = new GenerationContext();
            generatePredicate(predicate.getWhen().getPredicate(), contextVars, whenSection);
            conditionSection.addCode("((%s) ? %s : null)", whenSection.resolveCode(), predicateSection.resolveCode());
        }
    }

    private void generateExistsInConditionQuery(ExistsInCondition existsInCondition, Map<String, Type> contextVars, Set<String> aliases, GenerationContext conditionSection) {
        existsInCondition = (ExistsInCondition) rule.findStatement(existsInCondition.getOrGenerateId()).get();
        if (existsInCondition.isQuery()) {
            conditionSection.addCode("%s.of(", toTypeLiteral(ExistsInCondition.class));
            GenerationContext fromSection = new GenerationContext();
            generateFromQuery((From)existsInCondition.getStatements().get(0), contextVars, aliases, fromSection, rule);
            conditionSection.addCode(fromSection.resolveCode());
            conditionSection.addCode(")");
        }
        else {
            conditionSection.addCode("%s.of(\"%s\", \"%s\", ", toTypeLiteral(ExistsInCondition.class), existsInCondition.getCollection(), existsInCondition.getIter());

            if (existsInCondition.getWith() != null && existsInCondition.getWith().getPredicate() != null) {
                GenerationContext predicateSection = new GenerationContext();
                generatePredicateQuery((DefinedCondition) existsInCondition.getWith().getPredicate(), contextVars, aliases, predicateSection);
                conditionSection.addCode(predicateSection.resolveCode());
            }
            else {
                conditionSection.addCode("null");
            }
            conditionSection.addCode(")");
        }
    }

    private void generateComplexConditionQuery(ComplexCondition complexCondition, Map<String, Type> contextVars, Set<String> aliases, GenerationContext conditionSection) {
        if (NotCondition.class.isInstance(complexCondition)) {
            conditionSection.addCode("%s.of(", toTypeLiteral(NotCondition.class));
        } else if (AndCondition.class.isInstance(complexCondition)) {
            conditionSection.addCode("%s.of(", toTypeLiteral(AndCondition.class));
        } else if (OrCondition.class.isInstance(complexCondition)) {
            conditionSection.addCode("%s.of(", toTypeLiteral(OrCondition.class));
        }

        List<String> innerCondition = new LinkedList<>();

        complexCondition.getStatements().stream().filter(DefinedCondition.class::isInstance).map(DefinedCondition.class::cast).forEach(statement -> {
            GenerationContext predicateSection = new GenerationContext();
            generatePredicateQuery(statement, contextVars, aliases, predicateSection);
            innerCondition.add(predicateSection.resolveCode());
        });

        conditionSection.addCode(innerCondition.stream().collect(Collectors.joining(",\n")));

        conditionSection.addLine(")");
    }

    private void generateCompareConditionQuery(CompareCondition compareCondition, Map<String, Type> contextVars, Set<String> aliases, GenerationContext conditionSection) {
        CompareOperatorEnum operatorEnum = CompareOperatorEnum.fromString(compareCondition.getOperator());
        conditionSection.addCode("%s.of(\"%s\", %s.%s", toTypeLiteral(CompareCondition.class),
                compareCondition.getLeft().getValue(), toTypeLiteral(CompareOperatorEnum.class), operatorEnum.name());

        String ignoreCase;
        if (compareCondition.getIgnoreCase() != null) {
            ignoreCase = compareCondition.getIgnoreCase().toString();
        }
        else {
            ignoreCase = "null";
        }

        if (compareCondition.getRight() != null && compareCondition.getRight().getValue() != null) {
            String distance;
            if (compareCondition.getDistance() != null && !aliases.contains(compareCondition.getDistance().split("\\.")[0])) {
                distance = generateExpression(compareCondition.getDistance(), contextVars);
            }
            else {
                distance = compareCondition.getDistance() == null ? "null" : "\"" + compareCondition.getDistance() + "\"";
            }
            if (!aliases.contains(compareCondition.getRight().getValue().split("\\.")[0])) {
                conditionSection.addCode(", %s, %s, true, %s", generateExpression(compareCondition.getRight().getValue(), contextVars), ignoreCase, distance);
            } else {
                conditionSection.addCode(", \"%s\", %s, false, %s", compareCondition.getRight().getValue(), ignoreCase, distance);
            }
        }
        conditionSection.addCode(")");
    }

    @Override
    protected Map<String, Type> initVars() {
        Map<String, Type> contextVars = super.initVars();
        if (rule.getRuleType() == RuleType.ValidationRule) {
            contextVars.put(ValidationRuleBase.VALIDATION_MSG_PREFIX, IValidationMessages.class);
        }

        return contextVars;
    }
}
