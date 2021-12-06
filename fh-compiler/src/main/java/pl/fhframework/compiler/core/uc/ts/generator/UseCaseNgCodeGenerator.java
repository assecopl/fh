package pl.fhframework.compiler.core.uc.ts.generator;

import lombok.Getter;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.generator.AbstractNgClassCodeGenerator;
import pl.fhframework.compiler.core.generator.MetaModelService;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.service.ServiceMm;
import pl.fhframework.compiler.core.generator.ts.TsDependency;
import pl.fhframework.compiler.core.uc.dynamic.model.element.TransactionTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.generator.model.usecase.*;
import pl.fhframework.core.generator.GenerationContext;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UseCaseNgCodeGenerator extends AbstractNgClassCodeGenerator {
    public static final String OUTPUT_CALLBACK_SUFIX = "OutputCallback";

    private UseCaseMm useCase;

    @Getter
    private String classBaseName;

    private ActionNgCodeGenerator actionGenerator;

    private Set<String> asyncActions = new HashSet<>();

    public UseCaseNgCodeGenerator(UseCaseMm useCase, ModuleMetaModel moduleMetaModel, MetaModelService metaModelService) {
        super(moduleMetaModel, moduleMetaModel.getDependency(useCase.getId()), useCase, metaModelService);
        this.useCase = useCase;
        classBaseName = getBaseName(this.useCase.getId());
        actionGenerator = new ActionNgCodeGenerator(this, getMetaModelService());
    }

    @Override
    protected void generateClassBody() {
        reserveElementsName(useCase);

        findAsyncActions(useCase);

        generateClassSignature(useCase);

        addModel(useCase);

        addStart(useCase);

        addElements(useCase);

        addImports(actionGenerator);

        /*
        addStoreAccessService();

        calcEventsDirectActionMap(useCase); // different forms can have the same name for event, so don't check direct event actions

        calcNamesMapping(useCase);


        addFormsEvents(useCase);


        generateInnerClasses();

        generateRuleServiceAccess();

        addI18nService();

        addFhServiceAccess();
*/
    }

    private void generateClassSignature(UseCaseMm useCase) {
        addImport(FhNgCore.UseCase);

        String ucInterface;
        StartMm start = useCase.getStart();
        List<FinishMm> exits = useCase.getExits();

        if (start.getParameterDefinitions().isEmpty() && exits.isEmpty()) {
            addImport(FhNgCore.Initial);
            ucInterface = FhNgCore.Initial.getName();
        }
        else {
            String ucOutInterface;

            if (exits.isEmpty()) {
                addImport(FhNgCore.NoCallback);
                ucOutInterface = FhNgCore.NoCallback.getName();
            }
            else {
                ucOutInterface = String.format("%s.%s", classBaseName, addOutputCallback(exits));
            }

            if (start.getParameterDefinitions().size() == 0) {
                addImport(FhNgCore.NoInput);
                ucInterface = String.format("%s<%s>", FhNgCore.NoInput.getName(), ucOutInterface);
            }
            else {
                start.getParameterDefinitions().forEach(this::addImport);
                if (start.getParameterDefinitions().size() == 1) {
                    addImport(FhNgCore.OneInput);
                    ucInterface = String.format("%s<%s, %s>",
                            FhNgCore.OneInput.getName(),
                            getTypeLiterals(start.getParameterDefinitions()),
                            ucOutInterface);
                }
                else {
                    addImport(FhNgCore.IUseCase);
                    ucInterface = String.format("%s<[%s], %s>",
                            FhNgCore.IUseCase.getName(),
                            getTypeLiterals(start.getParameterDefinitions()),
                            ucOutInterface);
                }
            }
        }
        classSignatureSection.addLine("@%s({url: \"%s\"})", FhNgCore.UseCase.getName(), StringUtils.firstLetterToLower(classBaseName));
        classSignatureSection.addLine("export class %s extends %s", classBaseName, ucInterface);

        constructorSignatureSection.addLine("constructor(%s)", provideInjection(useCase.getDependencies(), DynamicClassArea.RULE, DynamicClassArea.SERVICE));
        constructorSection.addLine("super();");
    }

    private void addModel(UseCaseMm useCase) {
        addInputModel(useCase);
        addInternalModel(useCase);
        addOutputModel(useCase);
    }

    private void addStart(UseCaseMm useCase) {
        StartMm start = useCase.getStart();
        generateMethodSignature(methodSection, "start", start.getParameterDefinitions(), null, false, asyncActions.contains(start.getId()));
        start.getParameterDefinitions().forEach(input -> {
            methodSection.addLineWithIndent(1, "this.%s = %s;", input.getName(), input.getName());
        });
        methodSection.addLine();
        initInternalModel(methodSection, useCase);
        methodSection.addLine();
        actionGenerator.generateBody(methodSection, "start", start, asyncActions);
        methodSection.addLine("}");
    }

    private void addElements(UseCaseMm useCase) {
        Set<String> elementsCalledByFormEvent = getEventsCalleeIds();

        addActions(useCase, elementsCalledByFormEvent);
        addRunUseCases(useCase, elementsCalledByFormEvent);
    }

    private void addActions(UseCaseMm useCase, Set<String> elementsCalledByFormEvent) {
        List<ActionMm<?>> actions = new ArrayList<>();
        actions.addAll((List) useCase.getActions());
        actions.addAll(useCase.getExits());

        actions.forEach(action -> {
            String name = getReservedName(action.getId());

            methodSection.addLineIfNeeded();
            if (action instanceof FinishMm && ((FinishMm) action).isDiscardChanges()) {
                addImport(FhNgCore.Cancel);
                methodSection.addLine("@%s", FhNgCore.Cancel.getName());
            }
            if (elementsCalledByFormEvent.contains(action.getId())) {
                addImport(FhNgCore.Action);
                methodSection.addLine("@%s", FhNgCore.Action.getName());
            }
            generateMethodSignature(methodSection, name, action.getParameterDefinitions(), null, false, asyncActions.contains(action.getId()));
            actionGenerator.generateBody(methodSection, name, action, asyncActions);
            methodSection.addLine("}");
        });
    }

    private void addRunUseCases(UseCaseMm useCase, Set<String> elementsCalledByFormEvent) {
        useCase.getRunUsecases().forEach(runUc -> {
            String name = getReservedName(runUc.getId());

            methodSection.addLineIfNeeded();
            if (elementsCalledByFormEvent.contains(runUc.getId())) {
                addImport(FhNgCore.Action);
                methodSection.addLine("@%s", FhNgCore.Action.getName());
            }
            generateMethodSignature(methodSection, name, runUc.getUseCaseInfo().getStart().getParameterDefinitions(), null);
            String command = "runUseCase";
            if (runUc.getTransactionType() == TransactionTypeEnum.Current) {
                command = "runSubUseCase";
            }

            TsDependency ucDependency = getTsDependency(runUc.getUseCaseInfo().getUsecaseId());
            addImport(ucDependency);
            Map<String, ExitLink> callbacksMap = runUc.getExitLinks().stream().collect(Collectors.toMap(ExitLink::getLabel, Function.identity()));
            String callbacks = runUc.getUseCaseInfo().getExits().stream().map(exit -> {
                        ExitLink link = callbacksMap.get(exit.getId());
                        String linkExpression = "";
                        if (link != null) {
                            linkExpression = String.format("this.caller.%s(%s)",
                                    getReservedName(link.getTarget().getId()),
                                    calculateArguments(link.getParameters(), link, getBindCallerModifier()));
                        }
                        return GenerationContext.indent(2, String.format("%s = (%s) => {%s};", getMethodName(exit.getLabel()), getArgumentsSignature(exit.getParameterDefinitions()), linkExpression));
                    }
            ).collect(Collectors.joining("\n"));

            addImport(FhNgCore.Bind);
            methodSection.addLineWithIndent(1, "this.%s(%s, new class extends %s<%s> implements %s.%s {\n%s\n" + GenerationContext.indent(1, "}, %s);"),
                    command, ucDependency.getName(), FhNgCore.Bind.getName(), getClassTsDependency().getName(), ucDependency.getName(), OUTPUT_CALLBACK_SUFIX,
                    callbacks, getArguments(runUc.getUseCaseInfo().getStart().getParameterDefinitions()));
            // todo: runUsecase/runSubUsecase
            methodSection.addLine("}");
        });
    }

    private Set<String> getEventsCalleeIds() {
        Set<String> elementsCalledByFormEvent = new HashSet<>();
        List<ActionMm<?>> actions = new ArrayList<>();
        actions.addAll((List) useCase.getActions());
        actions.addAll(useCase.getExits());

        actions.forEach(action -> action.getOutEventsLinks().forEach(link -> elementsCalledByFormEvent.add(link.getTarget().getId())));

        return elementsCalledByFormEvent;
    }

    private void reserveElementsName(UseCaseMm useCase) {
        reserveName(useCase.getStart().getId(), "start");
        useCase.getActions().forEach(action -> reserveName(action.getId(), getMethodName(action.getLabel())));
        useCase.getExits().forEach(exit -> reserveName(exit.getId(), getMethodName(exit.getLabel())));
        useCase.getRunUsecases().forEach(runUc -> reserveName(runUc.getId(), getMethodName(runUc.getLabel())));
    }

    private String addOutputCallback(List<FinishMm> exits) {
        AdHocTsClassCodeGenerator callback = new AdHocTsClassCodeGenerator(moduleMetaModel, null, getMetaModelService());
        addImport(FhNgCore.UseCaseCallback);
        callback.getClassSignatureSection().addLineWithIndent(1,"export interface %s extends %s", OUTPUT_CALLBACK_SUFIX, FhNgCore.UseCaseCallback.getName());

        exits.forEach(exit -> {
            generateMethodSignature(callback.getMethodSection(), getMethodName(exit.getLabel()), exit.getParameterDefinitions(), null, true);
        });

        getNamespaceSection().addLine(callback.generateClass(false).trim());

        addImports(callback);

        return OUTPUT_CALLBACK_SUFIX;
    }

    private void addInputModel(UseCaseMm useCase) {
        useCase.getStart().getParameterDefinitions().forEach(this::generateField);
    }

    private void addInternalModel(UseCaseMm useCase) {
        useCase.getDataModel().forEach(this::generateField);
    }

    private void addOutputModel(UseCaseMm useCase) {
        useCase.getExits().forEach(exit -> exit.getParameterDefinitions().forEach(this::generateField));
    }

    private void initInternalModel(GenerationContext methodSection, UseCaseMm useCase) {
        List<ParameterDefinition> vars = new ArrayList<>();

        useCase.getExits().forEach(exit -> vars.addAll(exit.getParameterDefinitions()));
        vars.addAll(useCase.getDataModel());

        vars.forEach(var -> {
            // todo: is abstract or interface
            if (var.isCollection() || !isSimpleType(var)) {
                String modelName = getFieldName(var.getName());
                methodSection.addLineWithIndent(1, "this.%s = new %s();", modelName, getType(var));
            }
        });
    }

    protected void generateField(ParameterDefinition field) {
        addImport(field);
        fieldSection.addLine("%s: %s;", getFieldName(field.getName()), getType(field));
    }

    @Override
    protected Optional<String> getNamespaceName() {
        return Optional.of(classBaseName);
    }

    private void findAsyncActions(UseCaseMm useCase) {
        List<ActionMm<?>> actions = new ArrayList<>();
        actions.add(useCase.getStart());
        actions.addAll((List) useCase.getActions());
        actions.addAll(useCase.getExits());

        actions.forEach(action -> {
            if (isAsyncAction(action)) {
                asyncActions.add(action.getId());
            }
        });

        Set<String> newAsyncActions;
        do {
            newAsyncActions = actions.stream()
                    .filter(action -> !asyncActions.contains(action.getId()))
                    .filter(action -> action.getOutRunLinks().stream()
                            .filter(run -> run.getTarget() instanceof ActionInfo)
                            .anyMatch(run -> asyncActions.contains(run.getTarget().getId())))
                    .map(ActionMm::getId).collect(Collectors.toSet());
            asyncActions.addAll(newAsyncActions);
        } while (!newAsyncActions.isEmpty());
    }

    private boolean isAsyncAction(ActionMm<?> action) {
        return action.getCommands().stream().anyMatch(command -> metaModelService.findCallers(command).stream()
                .map(dcn -> moduleMetaModel.getMetadata(dcn.toFullClassName()))
                .filter(ServiceMm.class::isInstance).map(ServiceMm.class::cast)
                .anyMatch(service -> service.getServiceType() == ServiceTypeEnum.RestService || service.getServiceType() == ServiceTypeEnum.RestClient));
    }
}
