package pl.fhframework.compiler.core.rules.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXParseException;
import pl.fhframework.compiler.core.generator.*;
import pl.fhframework.compiler.core.i18n.MessagesTypeProvider;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.model.generator.DynamicModelClassJavaGenerator;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.rules.dynamic.model.Rule;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.rules.meta.RuleInfo;
import pl.fhframework.compiler.core.services.DynamicFhServiceManager;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.aspects.snapshots.SnapshotsModelAspect;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableContext;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.core.FhAuthorizationException;
import pl.fhframework.core.FhBindingException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.generator.GenericExpressionConverter;
import pl.fhframework.core.generator.IExpressionConverter;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.rules.dynamic.model.*;
import pl.fhframework.core.rules.dynamic.model.dataaccess.From;
import pl.fhframework.core.rules.dynamic.model.predicates.CompareCondition;
import pl.fhframework.core.rules.dynamic.model.predicates.ExistsInCondition;
import pl.fhframework.core.rules.service.RulesServiceImpl;
import pl.fhframework.core.services.MethodPointer;
import pl.fhframework.core.services.service.FhServicesService;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.Parameter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.validation.IValidationMessages;
import pl.fhframework.validation.IValidationResults;
import pl.fhframework.validation.ValidationRuleBase;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-02.
 */
@Service
@Primary
public class RulesServiceExtImpl extends RulesServiceImpl implements RulesServiceExt, IExpressionConverter {
    @Autowired
    DynamicClassRepository dynamicClassRepository;

    @Autowired
    private UseCaseModelUtils modelUtils;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RuleValidator ruleValidator;

    @Autowired
    List<ITypeProvider> typeProviderList;

    @Autowired
    private RulesTypeProvider rulesTypeProvider;

    @Autowired(required = false)
    private DataBaseService dataBaseService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private FhServicesService fhServicesService;

    @Autowired
    private GenericExpressionConverter genericExpressionConverter;

    @Autowired
    private EnumsTypeProvider enumsTypeProvider;

    @Value("classpath:schema/Rule.xsd")
    private Resource ruleXsd;


    public static final Class[] JAXB_CONTENT_CLASSES = {Rule.class};

    private static ThreadLocal<Map<String, MethodPointer>> ruleLookupCache = new ThreadLocal<>();

    @GeneratedDynamicClass("pl.fhframework.core.Context")
    public static abstract class Context {
    }

    public Rule readRule(URL url) {
        try {
            SnapshotsModelAspect.turnOff();
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTENT_CLASSES);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final Rule rule = (Rule) jaxbUnmarshaller.unmarshal(url);

            return rule;
        } catch (JAXBException e) {
            throw new RuntimeException("Error reading XML file", e);
        } finally {
            SnapshotsModelAspect.turnOn();
        }
    }

    public Rule readRule(RuleInfo ruleInfo) {
        try {
            SnapshotsModelAspect.turnOff();
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTENT_CLASSES);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final Rule rule = (Rule) jaxbUnmarshaller.unmarshal(ruleInfo.getUrl());
            rule.setRuleInfo(ruleInfo);

            return rule;
        } catch (JAXBException e) {
            throw new RuntimeException("Error reading XML file", e);
        } finally {
            SnapshotsModelAspect.turnOn();
        }
    }

    public String asString(Rule rule) {
        StringWriter sw = new StringWriter();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTENT_CLASSES);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(rule, sw);
        } catch (JAXBException e) {
            throw new RuntimeException("Error while marshaling Rule", e);
        }

        return sw.toString();
    }

    public Rule fromString(String ruleXml) {
        StringReader sr = new StringReader(ruleXml);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTENT_CLASSES);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            final Rule rule = (Rule) jaxbUnmarshaller.unmarshal(sr);

            return rule;
        } catch (JAXBException e) {
            throw new RuntimeException("Error creating Rule object", e);
        }
    }

    public void validate(String ruleXml, IValidationResults validationResults) {
        Rule rule;
        try {
            validateWithXsd(ruleXml, validationResults);
            rule = fromString(ruleXml);
        } catch (Exception e) {
            validationResults.addCustomMessage(ruleXml, "ruleXml", String.format($("luna.designer.rule.incorrect"), FhLogger.getCauseMessage(e)), PresentationStyleEnum.ERROR);
            return;
        }
        validate(rule, validationResults);
    }

    public void fillTypes(Rule rule) {
        // todo:
    }

    @Override
    public <T> T runRule(String ruleName, Object... args) {
        MethodPointer ruleReference = getCachedRule(ruleName, args); // no overloading by type (arguments count must differ)

        Class<?>[] paramClasses = Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return BindingParser.NullType.class;
                    }
                    else {
                        return arg.getClass();
                    }})
                .collect(Collectors.toList())
                .toArray(new Class<?>[0]);

        Object rule;

        Method ruleMethod;

        if (ruleReference != null) {
            rule = ruleReference.getObject();
            ruleMethod = ruleReference.getObjectMethod();
        }
        else {
            // get first and only method
            DynamicClassName dynamicMethodName = DynamicClassName.forClassName(ruleName);
            DynamicClassName dynamicClassName = DynamicClassName.forClassName(dynamicMethodName.getPackageName());
            Class<?> ruleClass = getRuleClass(dynamicClassName);

            rule = applicationContext.getBean(ruleClass);

            ruleMethod = Arrays.stream(ruleClass.getMethods()).
                    filter(method -> !Modifier.isStatic(method.getModifiers()) &&
                            Modifier.isPublic(method.getModifiers()) &&
                            !method.isBridge() &&
                            method.getName().equals(dynamicMethodName.getBaseClassName())).
                    filter(method -> {
                        Class<?>[] foundParamClasses = method.getParameterTypes();
                        if (foundParamClasses.length != paramClasses.length) {
                            return false;
                        }
                        // each argument's class matches
                        for (int i = 0; i < paramClasses.length; i++) {
                            if (!ReflectionUtils.isAssignablFrom(foundParamClasses[i], paramClasses[i]) && !BindingParser.NullType.class.isAssignableFrom(paramClasses[i])) {
                                return false;
                            }
                        }
                        return true;
                    }).findFirst().get();

            putInRuleCache(ruleName, args, rule, ruleMethod);
        }

        try {
            return (T) ruleMethod.invoke(rule, args);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof FhAuthorizationException) {
                throw (FhAuthorizationException) e.getTargetException();
            }
            throw new FhException(String.format("Error while running rule '%s'", ruleName), e.getTargetException());
        } catch (Exception e) {
            throw new FhException(String.format("Error while running rule '%s'", ruleName), e);
        }
    }

    public <T> T runRule(Rule rule, String... params) {
        MethodPointer ruleReference = getCachedRule(rule.getId(), params); // no overloading by type (arguments count must differ)

        Object ruleObj;

        Method ruleMethod;

        if (ruleReference != null) {
            ruleObj = ruleReference.getObject();
            ruleMethod = ruleReference.getObjectMethod();
        }
        else {
            DynamicClassName dynamicClassName = DynamicClassName.forClassName(rule.getId());
            Class<?> ruleClass = getRuleClass(dynamicClassName);

            ruleObj = applicationContext.getBean(ruleClass);

            // get first and only method
            ruleMethod = Arrays.stream(ruleClass.getMethods()).filter(method -> !Modifier.isStatic(method.getModifiers())).findFirst().get();
        }

        try {
            return (T) ruleMethod.invoke(ruleObj, getRuleArgs(params));
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof FhAuthorizationException) {
                throw (FhAuthorizationException) e.getTargetException();
            }
            throw new FhException(String.format("Error while running rule '%s'", rule.getId()), e.getTargetException());
        } catch (Exception e) {
            throw new FhException(String.format("Error while running rule '%s'", rule.getId()), e);
        }
    }

    private Class<?> getRuleClass(DynamicClassName dynamicClassName) {
        if (dynamicClassRepository.isRegisteredDynamicClass(dynamicClassName)) {
            return dynamicClassRepository.getOrCompileDynamicClass(dynamicClassName);
        } else {
            return dynamicClassRepository.getStaticClass(dynamicClassName);
        }
    }

    public Object[] getRuleArgs(String... params) {
        List<Object> argsList = new LinkedList<>();

        if (params != null) {
            Arrays.stream(params).forEach(param -> {
                argsList.add(getPelValue(param));
            });
        }

        return argsList.toArray();
    }

    public Object getPelValue(String valueExpr) {
        ExpressionContext expressionContext = new ExpressionContext();
        expressionContext.setDefaultBindingRoot("this", Context.class);
        expressionContext.addBindingRoot(RulesTypeProvider.RULE_PREFIX, "#ruleService", DynamicRuleManager.RULE_HINT_TYPE);
        expressionContext.addBindingRoot(FhServicesTypeProvider.SERVICE_PREFIX, "#fhServicesService", DynamicFhServiceManager.SERVICE_HINT_TYPE);
        expressionContext.addBindingRoot(EnumsTypeProvider.ENUM_PREFIX, "", EnumsTypeProviderSpring.ENUM_HINT_TYPE);
        expressionContext.addBindingRoot(MessagesTypeProvider.MESSAGE_HINT_PREFIX, "#messageService.getAllBundles()", MessagesTypeProvider.MESSAGE_HINT_TYPE);

        ExpressionSpelGenerator expressionSpelGenerator = new ExpressionSpelGenerator(null, expressionContext, typeProviderList.toArray(new ITypeProvider[]{}));

        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable("ruleService", this);
        context.setVariable("messageService", messageService);
        context.setVariable("fhServicesService", fhServicesService);

        ExpressionParser parser = new SpelExpressionParser();
        String spel = expressionSpelGenerator.createExecutorOrGetterInline(valueExpr, expressionContext).getExpression();

        return parser.parseExpression(spel).getValue(context);
    }

    @Override
    public boolean isRuleRunnable(String ruleName) {
        DynamicClassName dynamicClassName = DynamicClassName.forClassName(ruleName);
        if (dynamicClassRepository.isRegisteredDynamicClass(dynamicClassName)) {
            DynamicRuleMetadata ruleMetadata = dynamicClassRepository.getMetadata(dynamicClassName);
            IValidationResults extValidationResults = applicationContext.getBean(IValidationResults.class);
            validate(ruleMetadata.getRule(), extValidationResults);

            return !extValidationResults.hasAtLeastErrors();
        }
        return true;
    }

    public String getDefaultInputValue(ParameterDefinition parameterDefinition) {
        Type simpleType = DynamicModelClassJavaGenerator.TYPE_MAPPER.get(parameterDefinition.getType());
        if (simpleType != null && !parameterDefinition.isCollection() && !parameterDefinition.isPageable()) {
            if (Short.class.equals(simpleType)) {
                return "RULE.toShort(0)";
            }
            if (Integer.class.equals(simpleType)) {
                return "0";
            }
            if (Boolean.class.equals(simpleType)) {
                return "true";
            }
            if (Float.class.equals(simpleType)) {
                return "0.0f";
            }
            if (Long.class.equals(simpleType)) {
                return "0L";
            }
            if (Double.class.equals(simpleType)) {
                return "0.0d";
            }
            if (BigDecimal.class.equals(simpleType)) {
                return "RULE.toBigDecimal('0.0')";
            }
            if (LocalDate.class.equals(simpleType)) {
                return "RULE.dateOf(2001, 12, 31)";
            }
            if (Date.class.equals(simpleType)) {
                return "RULE.timeOf(2001, 12, 31, 23, 59, 59)";
            }
            if (String.class.equals(simpleType)) {
                return "'text'";
            }
        }

        return "null";
    }

    public void validate(Rule rule, IValidationResults validationResults) {
        ruleValidator.validate(rule, validationResults, this);
        DynamicClassName ruleDC = DynamicClassName.forClassName(rule.getId());
        if (validationResults.hasAtLeastErrors()) {
            dynamicClassRepository.setValid(ruleDC, false);
        } else {
            dynamicClassRepository.setValid(ruleDC, true);
        }
    }

    public void validateParameterDefinition(ParameterDefinition parameter, Rule rule, IValidationResults validationResults) {
        ruleValidator.validateParameterDefinition(parameter, rule, validationResults);
    }

    public void validateRunParameters(Rule rule, List<Parameter> parameters, IValidationResults validationResults) {
        ruleValidator.validateRunParameters(rule, parameters, validationResults, this);
    }

    @Override
    public Set<DynamicClassName> resolveCalledRules(String expression) {
        return searchCalledRules(expression, true).stream().map(r -> DynamicClassName.forClassName(r.getName())).collect(Collectors.toSet());
    }

    @Override
    public String shortNameToFullName(String shortRuleName) {
        return rulesTypeProvider.getFullRuleName(shortRuleName);
    }

    public List<GenericExpressionConverter.SymbolInExpression> searchCalledRules(String expression, boolean forDependecies) {
        return getGenericExpressionConverter().searchCalledSymbols(expression, RulesTypeProvider.RULE_PREFIX, forDependecies);
    }

    @Override
    public String convertToShortNames(String expression) {
        return getGenericExpressionConverter().convertSymbolNames(expression, RulesTypeProvider.RULE_PREFIX, getRulesTypeProvider()::getShortRuleName);
    }

    @Override
    public String convertToFullNames(String expression) {
        return getGenericExpressionConverter().convertSymbolNames(expression, RulesTypeProvider.RULE_PREFIX, getRulesTypeProvider()::getFullRuleName);
    }

    public void executeDdlFunctions(String ddl) {
        if (dataBaseService != null) {
            dataBaseService.executeDdlFunctions(ddl);
        }
    }

    @Override
    public Collection<? extends VariableContext> getAvailableVars(Statement statement, Rule rule) {
        List<VariableContext> vars = new LinkedList<>();

        vars.add(new VariableContext(RulesTypeProvider.RULE_PREFIX, VariableType.of(DynamicRuleManager.RULE_HINT_TYPE)));

        vars.add(new VariableContext(FhServicesTypeProvider.SERVICE_PREFIX, VariableType.of(DynamicFhServiceManager.SERVICE_HINT_TYPE)));

        if (rule.getRuleType() == RuleType.ValidationRule) {
            vars.add(new VariableContext(ValidationRuleBase.VALIDATION_MSG_PREFIX, VariableType.of(IValidationMessages.class)));
        }

        vars.add(new VariableContext(MessagesTypeProvider.MESSAGE_HINT_PREFIX, VariableType.of(MessagesTypeProvider.MESSAGE_HINT_TYPE)));

        vars.add(new VariableContext(EnumsTypeProvider.ENUM_PREFIX, VariableType.of(DynamicModelManager.ENUM_HINT_TYPE)));

        vars.addAll(rule.getInputParams().stream().map(VariableContext::of).collect(Collectors.toList()));

        vars.addAll(rule.getOutputParams().stream().map(VariableContext::of).collect(Collectors.toList()));

        getContextVars(statement, rule.getRuleDefinition().getStatements(), vars);

        return vars;
    }

    @Override
    public Collection<VariableContext> getInputVars() {
        List<VariableContext> vars = new LinkedList<>();

        vars.add(new VariableContext(RulesTypeProvider.RULE_PREFIX, VariableType.of(DynamicRuleManager.RULE_HINT_TYPE)));

        vars.add(new VariableContext(FhServicesTypeProvider.SERVICE_PREFIX, VariableType.of(DynamicFhServiceManager.SERVICE_HINT_TYPE)));

        vars.add(new VariableContext(MessagesTypeProvider.MESSAGE_HINT_PREFIX, VariableType.of(MessagesTypeProvider.MESSAGE_HINT_TYPE)));

        vars.add(new VariableContext(EnumsTypeProvider.ENUM_PREFIX, VariableType.of(DynamicModelManager.ENUM_HINT_TYPE)));

        return vars;
    }

    @Override
    public List<RuleMethodDescriptor> getValidationRules() {
        List<RuleMethodDescriptor> rulesList = rulesTypeProvider.getMethods(DynamicRuleManager.RULE_HINT_TYPE).stream().filter(MethodDescriptor::isHintable).
                map(RuleMethodDescriptor.class::cast).filter(rule -> rule.getRuleType() == RuleType.ValidationRule).collect(Collectors.toList());
        rulesList.sort(Comparator.comparing(RuleMethodDescriptor::getName));

        return rulesList;
    }

    @Override
    public List<RuleMethodDescriptor> getBusinessRules() {
        List<RuleMethodDescriptor> rulesList = rulesTypeProvider.getMethods(DynamicRuleManager.RULE_HINT_TYPE).stream().filter(MethodDescriptor::isHintable).
                map(RuleMethodDescriptor.class::cast).collect(Collectors.toList());
        rulesList.sort(Comparator.comparing(RuleMethodDescriptor::getName));

        return rulesList;
    }

    @Override
    public List<RuleType> getRuleTypes() {
            return Arrays.asList(RuleType.values());
    }

    @Override
    public void startRuleLookupCache() {
        ruleLookupCache.set(new HashMap<>());
    }

    @Override
    public void stopRuleLookupCache() {
        ruleLookupCache.set(null);
    }

    private MethodPointer getCachedRule(String ruleName, Object[] args) {
        Map<String, MethodPointer> ruleCache = ruleLookupCache.get();
        if (ruleCache != null) {
            return ruleCache.get(getRuleCacheId(ruleName, args));
        }

        return null;
    }

    private void putInRuleCache(String ruleName, Object[] args, Object rule, Method ruleMethod) {
        Map<String, MethodPointer> ruleCache = ruleLookupCache.get();
        if (ruleCache != null) {
            ruleCache.put(getRuleCacheId(ruleName, args), MethodPointer.of(rule, ruleMethod));
        }
    }

    private String getRuleCacheId(String ruleName, Object[] args) {
        return ruleName + "_&_" + args.length; // no overloading by type (arguments count must differ)
    }

    private boolean getContextVars(Statement statement, Collection<? extends RuleElement> elements, List<VariableContext> vars) {
        for (RuleElement element : elements) {
            List<VariableContext> varsCopy = vars;

            if (StatementsList.class.isInstance(element)) {
                varsCopy = new LinkedList<>(vars);
            }

            if (element.getOrGenerateId().equals(statement.getOrGenerateId())) {
                return true;
            }
            if (Var.class.isInstance(element)) {
                Var var = Var.class.cast(element);
                if (ruleValidator.isKnownType(var.getType()) && !StringUtils.isNullOrEmpty(var.getName())) {
                    vars.add(VariableContext.of(new ParameterDefinition(var.getType(), var.getName(), var.getMultiplicity())));
                }
            }
            else if (For.class.isInstance(element)) {
                For forStatement = For.class.cast(element);
                if (!StringUtils.isNullOrEmpty(forStatement.getIter())) {
                    vars.add(new VariableContext(forStatement.getIter(), VariableType.of(Integer.class)));
                }
            }
            else if (ForEach.class.isInstance(element)) {
                ForEach forEach = ForEach.class.cast(element);
                if (!StringUtils.isNullOrEmpty(forEach.getIter())) {
                    Optional<Type> typeOpt = getExpressionType(forEach.getCollection(), vars);
                    typeOpt.ifPresent(type -> vars.add(new VariableContext(forEach.getIter(), VariableType.of(getCollectionArgumentClass(typeOpt)))));
                }
            }
            else if (From.class.isInstance(element)) {
                From from = From.class.cast(element);
                if (!StringUtils.isNullOrEmpty(from.getIter())) {
                    if (ruleValidator.isKnownType(from.getType())) {
                        vars.add(VariableContext.of(new ParameterDefinition(from.getType(), from.getIter(), TypeMultiplicityEnum.Element)));
                    }
                    else if (!StringUtils.isNullOrEmpty(from.getCollection())) {
                        Optional<Type> typeOpt = getExpressionType(from.getCollection(), vars);
                        vars.add(new VariableContext(from.getIter(), VariableType.of(getCollectionArgumentClass(typeOpt))));
                    }
                }
            }
            else if (ExistsInCondition.class.isInstance(element)) {
                ExistsInCondition existsInCondition = ExistsInCondition.class.cast(element);
                if (!StringUtils.isNullOrEmpty(existsInCondition.getIter())) {
                    Optional<Type> typeOpt = getExpressionType(existsInCondition.getCollection(), vars);
                    typeOpt.ifPresent(type -> vars.add(new VariableContext(existsInCondition.getIter(), VariableType.of(getCollectionArgumentClass(typeOpt)))));
                }
            }

            boolean found = getContextVars(statement, element.getComplexValues().values(), vars);
            if (found) {
                return true;
            }

            if (StatementsList.class.isInstance(element)) {
                found = getContextVars(statement, StatementsList.class.cast(element).getStatements(), vars);
                if (found) {
                    return true;
                }
                else {
                    vars.clear();
                    vars.addAll(varsCopy);
                }
            }
        }

        return false;
    }

    Class getCollectionArgumentClass(Optional<Type> typeOptional) {
        if (typeOptional.isPresent() && ReflectionUtils.isAssignablFrom(Collection.class, typeOptional.get())) {
            return ReflectionUtils.getGenericArgumentsRawClasses(typeOptional.get())[0];
        }
        else {
            return Object.class;
        }
    }

    private ExpressionContext getBindingContext(Collection<VariableContext> contextVars) {
        ExpressionContext expressionContext = new ExpressionContext();
        expressionContext.setDefaultBindingRoot("this", Context.class);

        for (VariableContext variableContext : contextVars) {
            try {
                Type type = modelUtils.getType(variableContext.getVariableType());
                expressionContext.addTwoWayBindingRoot(variableContext.getName(), variableContext.getName(), type);
                if (ValidationRuleBase.VALIDATION_MSG_PREFIX.equals(variableContext.getName())) {
                    expressionContext.addTwoWayBindingRoot(ValidationRuleBase.VALIDATION_MSG_PREFIX, "getValidationResults()", IValidationMessages.class);
                }
            } catch (FhException pe) {
                continue;
            }
        }

        return expressionContext;
    }

    Optional<Type> getExpressionType(String expression, Collection<VariableContext> contextVars) {
        try {
            return Optional.of(new BindingParser(getBindingContext(contextVars), typeProviderList).getBindingReturnType(expression));
        } catch (FhUnsupportedExpressionTypeException | FhInvalidExpressionException | FhBindingException e) {
            return Optional.empty();
        }
    }

    private void validateWithXsd(String ruleXml, IValidationResults validationResults) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(ruleXsd.getURL());
            schema.newValidator().validate(new StreamSource(new StringReader(ruleXml)));
        } catch (SAXParseException e) {
            validationResults.addCustomMessage(ruleXml, "ruleXml", String.format($("luna.designer.rule.incorrect_line"), e.getLineNumber(), e.getColumnNumber(), e.getMessage()), PresentationStyleEnum.ERROR);
        } catch (Exception e) {
            validationResults.addCustomMessage(ruleXml, "ruleXml", String.format($("luna.designer.rule.incorrect"), FhLogger.getCauseMessage(e)), PresentationStyleEnum.ERROR);
        }
    }

    protected RulesTypeProvider getRulesTypeProvider() {
        return rulesTypeProvider;
    }

    protected GenericExpressionConverter getGenericExpressionConverter() {
        return genericExpressionConverter;
    }

    private String $(String key) {
        return messageService.getAllBundles().getMessage(key);
    }

    public Set<DynamicClassName> provideDependencies(Rule rule) {
        Set<DynamicClassName> dependencies = new HashSet<>();

        rule.getInputParams().forEach(parameterDefinition -> {
            if (!isPredefinedType(parameterDefinition.getType())) {
                dependencies.add(DynamicClassName.forClassName(parameterDefinition.getType()));
            }
        });

        rule.getOutputParams().forEach(parameterDefinition -> {
            if (!isPredefinedType(parameterDefinition.getType())) {
                dependencies.add(DynamicClassName.forClassName(parameterDefinition.getType()));
            }
        });

        rule.getRuleDefinitions().forEach(ruleDefinition -> {
            provideDependencies(ruleDefinition.getStatements(), dependencies);
        });
        return dependencies;
    }

    private void provideDependencies(Collection<? extends RuleElement> statements, Set<DynamicClassName> dependencies) {
        statements.forEach(statement -> {
            if (StatementsList.class.isInstance(statement)) {
                provideDependencies(((StatementsList)statement).getStatements(), dependencies);
            }
            if (SimpleStatement.class.isInstance(statement)) {
                dependencies.addAll(resolveCalledRules(SimpleStatement.class.cast(statement).getValue()));
                dependencies.addAll(enumsTypeProvider.resolveCalledEnums(SimpleStatement.class.cast(statement).getValue()));
                dependencies.addAll(fhServicesService.resolveCalledServices(SimpleStatement.class.cast(statement).getValue()));
            }
            if (If.class.isInstance(statement)) {
                provideDependencies(((If) statement).getCondition().getStatements(), dependencies);
            }
            else if (Else.class.isInstance(statement)) {
                Else elseTag = (Else) statement;
                if (elseTag.getCondition() != null) {
                    provideDependencies(elseTag.getCondition().getStatements(), dependencies);
                }
            }
            else if (While.class.isInstance(statement)) {
                provideDependencies(((While) statement).getCondition().getStatements(), dependencies);
            }
            else if (For.class.isInstance(statement)) {
                For forTag = (For) statement;
                dependencies.addAll(resolveCalledRules(forTag.getStart()));
                dependencies.addAll(resolveCalledRules(forTag.getIncr()));
                dependencies.addAll(resolveCalledRules(forTag.getEnd()));

                dependencies.addAll(enumsTypeProvider.resolveCalledEnums(forTag.getStart()));
                dependencies.addAll(enumsTypeProvider.resolveCalledEnums(forTag.getIncr()));
                dependencies.addAll(enumsTypeProvider.resolveCalledEnums(forTag.getEnd()));

                dependencies.addAll(fhServicesService.resolveCalledServices(forTag.getStart()));
                dependencies.addAll(fhServicesService.resolveCalledServices(forTag.getIncr()));
                dependencies.addAll(fhServicesService.resolveCalledServices(forTag.getEnd()));
            }
            else if (ForEach.class.isInstance(statement)) {
                dependencies.addAll(resolveCalledRules(((ForEach) statement).getCollection()));
                dependencies.addAll(enumsTypeProvider.resolveCalledEnums(((ForEach) statement).getCollection()));
                dependencies.addAll(fhServicesService.resolveCalledServices(((ForEach) statement).getCollection()));
            }
            else if (CompareCondition.class.isInstance(statement)) {
                CompareCondition compareCondition = (CompareCondition) statement;
                provideDependencies(Collections.singleton(compareCondition.getLeft()), dependencies);
                provideDependencies(Collections.singleton(compareCondition.getRight()), dependencies);
                dependencies.addAll(resolveCalledRules(compareCondition.getDistance()));
                dependencies.addAll(enumsTypeProvider.resolveCalledEnums(compareCondition.getDistance()));
            }
            else if (From.class.isInstance(statement)) {
                From fromTag = (From) statement;
                dependencies.addAll(resolveCalledRules(fromTag.getCollection()));
                dependencies.addAll(resolveCalledRules(fromTag.getHolder()));

                dependencies.addAll(enumsTypeProvider.resolveCalledEnums(fromTag.getCollection()));
                dependencies.addAll(enumsTypeProvider.resolveCalledEnums(fromTag.getHolder()));

                dependencies.addAll(fhServicesService.resolveCalledServices(fromTag.getCollection()));
                dependencies.addAll(fhServicesService.resolveCalledServices(fromTag.getHolder()));
                if (!StringUtils.isNullOrEmpty(fromTag.getType())) {
                    dependencies.add(DynamicClassName.forClassName(fromTag.getType()));
                }
            }
            else if (Var.class.isInstance(statement)) {
                String type = Var.class.cast(statement).getType();
                if (!isPredefinedType(type)) {
                    dependencies.add(DynamicClassName.forClassName(type));
                }
            }
            provideDependencies(statement.getComplexValues().values(), dependencies);
        });
    }

    private boolean isPredefinedType(String type) {
        return DynamicModelClassJavaGenerator.TYPE_MAPPER.get(type) != null;
    }

}
