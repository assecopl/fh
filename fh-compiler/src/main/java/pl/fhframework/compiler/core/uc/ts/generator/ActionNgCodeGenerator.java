package pl.fhframework.compiler.core.uc.ts.generator;

import pl.fhframework.compiler.core.generator.AbstractNgClassCodeGenerator;
import pl.fhframework.compiler.core.generator.FhServicesTypeProvider;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.RulesTypeProvider;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.generator.model.rule.RuleMm;
import pl.fhframework.compiler.core.generator.ts.TsDependency;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;
import pl.fhframework.compiler.core.generator.model.usecase.*;
import pl.fhframework.core.FhException;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ActionNgCodeGenerator extends AbstractNgClassCodeGenerator {
    private UseCaseNgCodeGenerator codeGenerator;
    private int additionalIndent = 0;

    public ActionNgCodeGenerator(UseCaseNgCodeGenerator codeGenerator, MetaModelService metaModelService) {
        super(codeGenerator.getModuleMetaModel(), codeGenerator.getClassDependency(), codeGenerator.getMetaModel(), metaModelService);
        this.codeGenerator = codeGenerator;
    }

    public void generateBody(GenerationContext methodSection, String methodName, ActionMm<?> action, Set<String> asyncActions) {
        action.getCommands().forEach(command -> {
            boolean condition = false;
            if (isExpression(command.getCondition())) {
                condition = true;
                String conditionExp = convertExpression(command.getCondition(), command);
                methodSection.addLineWithIndent(1 + additionalIndent, "if (%s) {", conditionExp);
                additionalIndent++;
            }
            if (command instanceof RunMm) {
                generateLink(methodSection, (RunMm) command, asyncActions);
            }
            // todo:
            else if (command instanceof ShowMessageMm) {

            }
            else if (command instanceof ShowFormMm) {
                generateShowForm(methodSection, (ShowFormMm) command);
            }
            // todo:
            else if (command instanceof CallFunctionMm) {
                generateCallFunction(methodSection, (CallFunctionMm) command);
            }
            if (condition) {
                additionalIndent--;
                methodSection.addLineWithIndent(1 + additionalIndent,"}");
            }
        });
        if (action instanceof FinishMm) {
            methodSection.addLineWithIndent(1 + additionalIndent, "this.exit().%s(%s);", methodName, getArguments(action.getParameterDefinitions()));
        }
    }

    private void generateShowForm(GenerationContext methodSection, ShowFormMm command) {
        TsDependency formDependency = getTsDependency(moduleMetaModel.getDependency(command.getForm().getId()));
        addImport(formDependency);

        String formName = formDependency.getName();
        String showFormModelName = JavaNamesUtils.getFieldName(formName + "Model");
        String showFormModelType = formName + ".Model";
        if (!StringUtils.isNullOrEmpty(command.getForm().getModelType())) {
            TsDependency modelTypeDependency = getTsDependency(moduleMetaModel.getDependency(command.getForm().getModelType()));
            addImport(modelTypeDependency);
            showFormModelType = modelTypeDependency.getName();
        }

        if (isExpression(command.getModelProperty())) {
            methodSection.addLineWithIndent(1 + additionalIndent, "let %s = %s;", showFormModelName, /* todo: spel */ convertExpression(command.getModelProperty(), command, getMetaModel()));
        } else {
            methodSection.addLineWithIndent(1 + additionalIndent, "let %s = new %s();", showFormModelName, showFormModelType);
        }

        // todo: generate assign value
        /*
        showForm.getFormDataElements().forEach(parameter -> {
            if (!StringUtils.isNullOrEmpty(parameter.getValue())) {
                generateAssignValue(methodSection, bindingContext, showFormModelName.concat(".").concat(parameter.getName()), parameter.getValue(), false, null);
            }
        });
        */
        Map<String, ActionLinkMm> actionsMap = (Map<String, ActionLinkMm>) command.getActionLinks().stream().collect(Collectors.toMap(ActionLinkMm::getFormEvent, Function.identity()));
        addImport(FhNgCore.Bind);
        String events = String.format("new class extends %s<%s> implements %s.Events {\n%s\n" + GenerationContext.indent(1 + additionalIndent, "}()"),
                FhNgCore.Bind.getName(),
                codeGenerator.getClassBaseName(),
                formName,
                command.getForm().getEvents().stream().map(event -> {
                            ActionLinkMm link = actionsMap.get(event.getActionName());
                            String linkExpression = "";
                            if (link != null) {
                                linkExpression = String.format("this.caller.%s(%s)",
                                        codeGenerator.getReservedName(link.getTarget().getId()),
                                        calculateArguments(link.getParameters(), link, getBindCallerModifier()));
                            }
                            return GenerationContext.indent(2 + additionalIndent, String.format("%s = (%s) => {%s};", event.getActionName(), getArgumentsSignature(event.getArguments()), linkExpression));
                        }
                ).collect(Collectors.joining("\n")));
        // todo: variants
        methodSection.addLineWithIndent(1 + additionalIndent, "this.showForm(%s, %s, %s);", formName, showFormModelName, events);
    }

    private void generateCallFunction(GenerationContext methodSection, CallFunctionMm command) {
        // todo:
        switch (command.getActivityType()) {
            case AssignValue:
                generateAssignValue(methodSection, command);
                break;
            case NewInstance:
                generateNewInstance(methodSection, command);
                break;
            case ExpressionEval:
                generateExpressionEval(methodSection, command);
                break;
            case DataRead:
                //generateDataRead(methodSection, command, ucHolder, dependenciesContext);
                break;
            case Validate:
                //generateValidate(methodSection, command, ucHolder, dependenciesContext);
                break;
            case RunRule:
                generateRunRuleService(methodSection, command, RulesTypeProvider.RULE_PREFIX, command.getRuleId());
                break;
            case RunService:
                generateRunRuleService(methodSection, command, FhServicesTypeProvider.SERVICE_PREFIX, command.getServiceId());
                break;
            case DataWrite:
                //generateStoreWrite(methodSection, command, ucHolder, dependenciesContext);
                break;
            case DataRefresh:
                //generateStoreRefresh(methodSection, command, ucHolder, dependenciesContext);
                break;
            case DataDelete:
                //generateStoreDelete(methodSection, command, ucHolder, dependenciesContext);
                break;
            default:
                throw new FhException("Unknown CallFunction " + command.getActivityType());
        }
    }

    private void generateAssignValue(GenerationContext methodSection, CallFunctionMm command) {
        methodSection.addLineWithIndent(1 + additionalIndent, "%s;", generateAssignValue(command.getReturnHolder(), command.getExpression(), command, command.isLocalVariable()));
    }

    private void generateNewInstance(GenerationContext methodSection, CallFunctionMm command) {
        Type type = getExpressionType(command.getExpression().getExpression(), command, getMetaModel());
        addImport(type);
        methodSection.addLineWithIndent(1 + additionalIndent, "%s = new %s();", convertExpression(command.getExpression(), command), getType(VariableType.of(type).asParameterDefinition()));
    }

    private void generateRunRuleService(GenerationContext methodSection, CallFunctionMm command, String prefix, String methodId) {
        RuleMm rule = command.getRule(); // local rule
        String expression;
        if (rule == null) {
            expression = convertExpression(new ExpressionMm(String.format("%s.%s(%s)", prefix, methodId, getParameters(command.getParameters()))), command);
        }
        else {
            //todo:
            expression = "null";
        }

        if (command.isLocalVariable()) {
            methodSection.addLineWithIndent(1 + additionalIndent, "let %s = %s;", convertExpression(command.getReturnHolder(), command), expression);
        }
        else {
            methodSection.addLineWithIndent(1 + additionalIndent, "%s;", expression);
        }
    }

    private String generateAssignValue(ExpressionMm leftSide, ExpressionMm rightSide, WithExpression element, boolean localVariable) {
        String rightSideExpression = convertExpression(rightSide, element);
        if (localVariable) {
            return String.format("let %s = %s", leftSide.getExpression(), rightSideExpression);
        }
        String leftSideExpression = convertExpression(leftSide, element);
        return String.format("%s = %s", leftSideExpression, rightSideExpression);
    }

    private void generateExpressionEval(GenerationContext methodSection, CallFunctionMm command) {
        methodSection.addLineWithIndent(1 + additionalIndent, "%s;", convertExpression(command.getExpression(), command));
    }

    private void generateLink(GenerationContext methodSection, RunMm link, Set<String> asyncActions) {
        if (link.getActivityType() == ActivityTypeEnum.RunAction ||
                link.getActivityType() == ActivityTypeEnum.GoToExit) {
            generateRunAction(methodSection, link, asyncActions);
        } else if (link.getActivityType() == ActivityTypeEnum.RunUseCase) {
            generateRunUseCase(methodSection, link);
        }
    }

    private void generateRunAction(GenerationContext methodSection, RunMm link, Set<String> asyncActions) {
        ActionInfo target = (ActionInfo) link.getTarget();
        String await = asyncActions.contains(target.getId()) ? "await " : "";
        methodSection.addLineWithIndent(1 + additionalIndent, "%sthis.usecase.%s(%s);", await, codeGenerator.getReservedName(target.getId()), calculateArguments(link.getParameters(), link));
        if (isExpression(link.getCondition()) && FinishInfo.class.isInstance(target)) {
            methodSection.addLineWithIndent(1 + additionalIndent, "return;");
        }

    }

    private void generateRunUseCase(GenerationContext methodSection, RunMm link) {
        UseCaseInfoMm runUseCase = (UseCaseInfoMm) link.getTarget();
        methodSection.addLineWithIndent(1 + additionalIndent, "this.usecase.%s(%s);", codeGenerator.getReservedName(runUseCase.getId()), calculateArguments(link.getParameters(), link));
    }

    @Override
    protected void generateClassBody() {
        throw new UnsupportedOperationException();
    }
}
