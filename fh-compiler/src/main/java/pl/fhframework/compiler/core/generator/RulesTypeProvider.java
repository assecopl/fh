package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.compiler.cache.UserSessionCache;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFilter;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.compiler.core.rules.DynamicRuleManager;
import pl.fhframework.compiler.core.rules.DynamicRuleMetadata;
import pl.fhframework.compiler.core.rules.dynamic.model.RuleType;
import pl.fhframework.compiler.core.tools.JavaNamesUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.rules.AccessibilityRule;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.rules.FilteringRule;
import pl.fhframework.core.rules.ValidationRule;
import pl.fhframework.core.security.PermissionModificationListener;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-23.
 */
@Component
public class RulesTypeProvider implements ITypeProvider, PermissionModificationListener{
    // TODO: CORE_LITE_CHANGE to RuleMetadataRegistry.RULE_PREFIX
    public static String RULE_PREFIX = "RULE";

    @Autowired
    private
    DynamicClassRepository dynamicClassRepository;

    @Autowired
    private
    UseCaseModelUtils typeUtils;

    @Autowired(required = false)
    private UserSessionCache userSessionCache;

    private int version;

    private List<MethodDescriptor> cachedMethods;
    private Map<String, MethodDescriptor> cachedMethodsMap;
    private Map<String, String> cachedAliasToFullNameMapping;
    private Map<String, String> cachedFullNameToAliasMapping;

    private boolean refreshNeeded = true;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Type getSupportedType() {
        return DynamicRuleManager.RULE_HINT_TYPE;
    }

    @Override
    public List<MethodDescriptor> getMethods(Type ofType) {
        return getMethods(ofType, false);
    }

    @Override
    public List<MethodDescriptor> getMethods(Type ofType, boolean onlyPermitted) {
        lock.readLock().lock();

        try {
            List<MethodDescriptor> methods = cachedMethods;

            if (refreshNeeded) {
                methods = rebuildMethods();
            }

            if (onlyPermitted) {
                // hiding rules without permission from combo
                if (userSessionCache != null) {
                    return userSessionCache.getRulesListCache().getRulesList(version, methods);
                }
            }

            return new ArrayList<>(methods);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toTypeLiteral() {
        return null;
    }

    public void refresh() {
        refreshNeeded = true;
    }

    private void clear() {
        // todo: clearing all cache is not optimal !!!
        cachedMethods = null;
        cachedMethodsMap = null;
        cachedAliasToFullNameMapping = null;
        cachedFullNameToAliasMapping = null;
    }

    private List<MethodDescriptor> rebuildMethods() {
        return getMethods(listClasses(), true);
    }

    public List<MethodDescriptor> getMethodsOfConreteClass(Class clazz) {
        return getMethods(Collections.singletonList(new ClassInfo(false, DynamicClassName.forClassName(clazz.getName()), clazz.getName())), false);
    }

    private List<MethodDescriptor> getMethods(List<IClassInfo> rulesList, boolean internal) {
        if (internal) {
            lock.readLock().unlock();
            lock.writeLock().lock();
        }
        try {
            List<MethodDescriptor> methods = new ArrayList<>();

            if (refreshNeeded || !internal) {
                if (internal) {
                    clear();
                }

                MultiValueMap<String, RuleMethodDescriptor> allRuleFullSygn = new LinkedMultiValueMap<>();
                MultiValueMap<String, String> allRuleNamesWithPackage = new LinkedMultiValueMap<>();

                rulesList.forEach(iClassInfo -> {
                    fillMethods(iClassInfo, allRuleFullSygn, allRuleNamesWithPackage);
                    // Map<methodName_paramTypes, methodName>
                });

                if (internal) {
                    cachedMethods = new LinkedList<>();
                    cachedMethodsMap = new LinkedHashMap<>();
                    cachedAliasToFullNameMapping = new HashMap<>();
                    cachedFullNameToAliasMapping = new HashMap<>();
                }

                allRuleFullSygn.forEach((methodFullSygn, ruleMethodList) -> {
                    ruleMethodList.forEach(ruleMethod -> {
                        String methodName;
                        if (allRuleNamesWithPackage.get(ruleMethod.getName()).size() > 1) {
                            methodName = ruleMethod.getDynamicClassName().toFullClassName().replace('.', '_') + "_" + ruleMethod.getName();
                        } else {
                            methodName = ruleMethod.getName();
                        }

                        RuleMethodDescriptor ruleMethodDescriptor = new RuleMethodDescriptor(DynamicRuleManager.RULE_HINT_TYPE,
                                methodName,
                                methodName,
                                ruleMethod.isRuleStatic(),
                                false,
                                true,
                                ruleMethod.getDynamicClassName(),
                                getModelUtils(),
                                ruleMethod.getMethod(),
                                ruleMethod.getMetadata(),
                                ruleMethod.getRuleType());

                        if (internal) {
                            cachedMethods.add(ruleMethodDescriptor);
                            cachedMethodsMap.put(methodName, ruleMethodDescriptor);
                            // add also a full class as not hinted but respected
                            String fullName = ruleMethod.getDynamicClassName().toFullClassName() + "." + ruleMethod.getName();
                            ruleMethodDescriptor = new RuleMethodDescriptor(DynamicRuleManager.RULE_HINT_TYPE,
                                    fullName,
                                    methodName,
                                    ruleMethod.isRuleStatic(),
                                    false,
                                    false,
                                    ruleMethod.getDynamicClassName(),
                                    getModelUtils(),
                                    ruleMethod.getMethod(),
                                    ruleMethod.getMetadata(),
                                    ruleMethod.getRuleType());
                            cachedMethods.add(ruleMethodDescriptor);
                            cachedMethodsMap.put(fullName, ruleMethodDescriptor);
                            cachedAliasToFullNameMapping.put(methodName, fullName);
                            cachedFullNameToAliasMapping.put(fullName, methodName);
                        }
                        else {
                            methods.add(ruleMethodDescriptor);
                        }
                    });
                });

                if (internal) {
                    version++;
                    refreshNeeded = false;
                }
            }
            if (internal) {
                return cachedMethods;
            }
            else {
                return methods;
            }
        }
        finally {
            if (internal) {
                lock.readLock().lock();
                lock.writeLock().unlock();
            }
        }
    }

    private void fillMethods(IClassInfo iClassInfo, MultiValueMap<String, RuleMethodDescriptor> allRuleFullSygn, MultiValueMap<String, String> allRuleNamesWithPackage) {
        Map<String, RuleMethodContainer> methodNamesList = new HashMap<>();
        if (iClassInfo.isDynamic()) {
            DynamicRuleMetadata metadata = getMetadata(iClassInfo.getClassName());

            String simpleName = JavaNamesUtils.normalizeMethodName(iClassInfo.getClassName().getBaseClassName());

            StringJoiner stringJoiner = new StringJoiner("_");
            stringJoiner.add(simpleName);

            if (metadata.getRule() != null) {
                String paramTypes = metadata.getRule().getInputParams().stream().map(ParameterDefinition::getType).collect(Collectors.joining("_"));
                if (!StringUtils.isNullOrEmpty(paramTypes)) {
                    stringJoiner.add(paramTypes);
                }
            }
            methodNamesList.put(stringJoiner.toString(), new RuleMethodContainer(simpleName, metadata));
        } else {
            Class clazz = getStaticClass(iClassInfo.getClassName());
            Arrays.stream(clazz.getDeclaredMethods()).
                    filter(method -> Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())).
                    forEach(method -> {
                        StringJoiner stringJoiner = new StringJoiner("_");
                        stringJoiner.add(method.getName());

                        String paramTypes = Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining("_"));
                        if (!StringUtils.isNullOrEmpty(paramTypes)) {
                            stringJoiner.add(paramTypes);
                        }

                        methodNamesList.put(stringJoiner.toString(), new RuleMethodContainer(method.getName(), method, getRuleType(clazz)));
                    });
        }
        methodNamesList.forEach((methodFullSygn, methodContainer) -> {
            allRuleNamesWithPackage.computeIfAbsent(methodContainer.getSimpleName(), name -> new LinkedList<>());
            if (!allRuleNamesWithPackage.get(methodContainer.getSimpleName()).contains(iClassInfo.getClassName().toFullClassName())) {
                allRuleNamesWithPackage.get(methodContainer.getSimpleName()).add(iClassInfo.getClassName().toFullClassName());
            }
            allRuleFullSygn.add(methodFullSygn, new RuleMethodDescriptor(DynamicRuleManager.RULE_HINT_TYPE,
                    methodContainer.getSimpleName(),
                    methodContainer.getSimpleName(),
                    !iClassInfo.isDynamic(),
                    false,
                    true,
                    iClassInfo.getClassName(),
                    getModelUtils(),
                    methodContainer.getMethod(),
                    methodContainer.getMetadata(),
                    methodContainer.getRuleType()));
        });
    }

    private Map<String, MethodDescriptor> rebuildMethodsMap() {
        lock.readLock().lock();

        try {
            if (refreshNeeded) {
                rebuildMethods();
            }
            return cachedMethodsMap;
        }
        finally {
            lock.readLock().unlock();
        }
    }

    private RuleType getRuleType(Class clazz) {
        if (clazz.getAnnotation(ValidationRule.class) != null) {
            return RuleType.ValidationRule;
        }
        if (clazz.getAnnotation(BusinessRule.class) != null) {
            return RuleType.BusinessRule;
        }
        if (clazz.getAnnotation(FilteringRule.class) != null) {
            return RuleType.FilteringRule;
        }
        if (clazz.getAnnotation(AccessibilityRule.class) != null) {
            return RuleType.AccessibilityRule;
        }
        return null;
    }

    public String getFullRuleName(String name) {
        lock.readLock().lock();

        try {
            if (refreshNeeded) {
                rebuildMethods();
            }
            Map<String, String> mapping = cachedAliasToFullNameMapping;
            return mapping.get(name);
        }
        finally {
            lock.readLock().unlock();
        }
    }

    public String getShortRuleName(String fullName) {
        lock.readLock().lock();

        try {
            if (refreshNeeded) {
                rebuildMethods();
            }
            Map<String, String> mapping = cachedFullNameToAliasMapping;
            return mapping.get(fullName);
        } finally {
            lock.readLock().unlock();
        }
    }

    public RuleMethodDescriptor getMethodDescription(String methodName) {
        return (RuleMethodDescriptor) rebuildMethodsMap().get(methodName);
    }

    public RuleMethodDescriptor getMethodDescription(String methodName, List<VariableType> variableTypes) {
        List<RuleMethodDescriptor> matched = getMethods(getSupportedType()).stream().map(RuleMethodDescriptor.class::cast).filter(methodDescriptor -> methodName.equals(methodDescriptor.getName())).collect(Collectors.toList());

        if (matched.size() == 1) {
            return matched.get(0);
        }
        else if (matched.size() > 1){
            for (RuleMethodDescriptor rule : matched) {
                if (rule.parametersMatch(variableTypes)) {
                    return rule;
                }
            }

            return matched.get(0);
        }

        return null;
    }

    @Override
    public void onRoleChange(String roleName) {
        lock.writeLock().lock();

        try {
            version++;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    @Getter
    @Setter
    private class RuleMethodContainer {
        private Method method;
        private DynamicRuleMetadata metadata;
        private String simpleName;
        private RuleType ruleType;

        public RuleMethodContainer(String simpleName, Method method, RuleType ruleType) {
            this.simpleName = simpleName;
            this.method = method;
            this.ruleType = ruleType;
        }

        public RuleMethodContainer(String simpleName, DynamicRuleMetadata metadata) {
            this.simpleName = simpleName;
            this.metadata = metadata;
            if (metadata.getRule() != null) {
                this.ruleType = metadata.getRule().getRuleType();
            }
        }
    }

    protected UseCaseModelUtils getModelUtils() {
        return typeUtils;
    }

    protected DynamicRuleMetadata getMetadata(DynamicClassName className) {
        return dynamicClassRepository.getMetadata(className);
    }

    protected List<IClassInfo> listClasses() {
        return dynamicClassRepository.listClasses(DynamicClassFilter.ALL_CLASSES, DynamicClassArea.RULE);
    }

    protected Class getStaticClass(DynamicClassName className) {
        return dynamicClassRepository.getStaticClass(className);
    }
}
