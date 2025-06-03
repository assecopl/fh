package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFilter;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.compiler.core.model.DynamicModelManager;
import pl.fhframework.compiler.core.model.DynamicModelMetadata;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCaseModelUtils;
import pl.fhframework.compiler.core.uc.dynamic.model.VariableType;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GenericExpressionConverter;
import pl.fhframework.core.generator.ModelElementType;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.model.Model;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-23.
 */
public class EnumsTypeProviderBase implements ITypeProvider {
    public static String ENUM_PREFIX = "ENUM";

    @Autowired
    private DynamicClassRepository dynamicClassRepository;

    @Autowired
    private UseCaseModelUtils typeUtils;

    @Autowired
    protected GenericExpressionConverter genericExpressionConverter;

    private List<MethodDescriptor> cachedMethods;
    private Map<String, MethodDescriptor> cachedMethodsMap;
    private Map<String, String> cachedAliasToFullNameMapping;
    private Map<String, String> cachedFullNameToAliasMapping;

    private boolean refreshNeeded = true;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private int version;

    @Override
    public Type getSupportedType() {
        return DynamicModelManager.ENUM_HINT_TYPE;
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

    private List<MethodDescriptor> getMethods(List<IClassInfo> enumsList, boolean internal) {
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

                MultiValueMap<String, EnumDescriptor> allEnumFullSygn = new LinkedMultiValueMap<>();
                MultiValueMap<String, String> allEnumNamesWithPackage = new LinkedMultiValueMap<>();

                enumsList.forEach(iClassInfo -> {
                    fillMethods(iClassInfo, allEnumFullSygn, allEnumNamesWithPackage);
                    // Map<methodName_paramTypes, methodName>
                });

                if (internal) {
                    cachedMethods = new LinkedList<>();
                    cachedMethodsMap = new LinkedHashMap<>();
                    cachedAliasToFullNameMapping = new HashMap<>();
                    cachedFullNameToAliasMapping = new HashMap<>();
                }

                allEnumFullSygn.forEach((methodFullSygn, methodList) -> {
                    methodList.forEach(method -> {
                        String methodName;
                        if (allEnumNamesWithPackage.get(getClassNameLC(method.getClassInfo())).size() > 1) {
                            methodName = DynamicClassName.forStaticBaseClass(method.getReturnType()).toFullClassName().replace('.', '_') + "_" + getClassNameLC(method.getClassInfo());
                        } else {
                            methodName = getClassNameLC(method.getClassInfo());
                        }

                        MethodDescriptor methodDescriptor = createDescriptor(method, methodName, true, getEnumDescriptorConstructor(), getDynamicEnumDescriptorConstructor());
                        MethodDescriptor valueOfDescriptor = createDescriptor(method, methodName + ValueOfDescriptor.NAME, true, getValueOfDescriptorConstructor(), getDynamicValueOfDescriptorConstructor());

                        if (internal) {
                            cachedMethods.add(methodDescriptor);
                            cachedMethods.add(valueOfDescriptor);
                            cachedMethodsMap.put(methodName, methodDescriptor);
                            cachedMethodsMap.put(methodName + ValueOfDescriptor.NAME, valueOfDescriptor);
                            // add also a full class as not hinted but respected
                            String fullName = DynamicClassName.forStaticBaseClass(method.getReturnType()).toFullClassName();
                            methodDescriptor = createDescriptor(method, fullName, false, getEnumDescriptorConstructor(), getDynamicEnumDescriptorConstructor());
                            valueOfDescriptor = createDescriptor(method, getValuesFullMethodName(fullName, methodName), false, getValueOfDescriptorConstructor(), getDynamicValueOfDescriptorConstructor());
                            cachedMethods.add(methodDescriptor);
                            cachedMethods.add(valueOfDescriptor);
                            cachedMethodsMap.put(fullName, methodDescriptor);
                            cachedMethodsMap.put(getValuesFullMethodName(fullName, methodName), valueOfDescriptor);
                            cachedAliasToFullNameMapping.put(methodName, fullName);
                            cachedAliasToFullNameMapping.put(methodName + ValueOfDescriptor.NAME, getValuesFullMethodName(fullName, methodName));
                            cachedFullNameToAliasMapping.put(fullName, methodName);
                            cachedFullNameToAliasMapping.put(getValuesFullMethodName(fullName, methodName), methodName + ValueOfDescriptor.NAME);
                        }
                        else {
                            methods.add(methodDescriptor);
                            methods.add(valueOfDescriptor);
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

    private String getValuesFullMethodName(String fullName, String methodName) {
        return fullName + "$" + ValueOfDescriptor.NAME + "." + methodName + ValueOfDescriptor.NAME;
    }

    protected DescriptorConstructor getEnumDescriptorConstructor() {
        return EnumDescriptor::new;
    }

    protected DynamicDescriptorConstructor getDynamicEnumDescriptorConstructor() {
        return EnumDescriptor::new;
    }

    protected DescriptorConstructor getValueOfDescriptorConstructor() {
        return ValueOfDescriptor::new;
    }

    protected DynamicDescriptorConstructor getDynamicValueOfDescriptorConstructor() {
        return ValueOfDescriptor::new;
    }

    private MethodDescriptor createDescriptor(EnumDescriptor method, String methodName, boolean hintable, DescriptorConstructor constructor, DynamicDescriptorConstructor dynamicConstructor) {
        if (method.getClassInfo().isDynamic()) {
            return (MethodDescriptor) dynamicConstructor.apply(method.getClassInfo(), DynamicModelManager.ENUM_HINT_TYPE,
                    methodName,
                    false,
                    hintable,
                    method.getModelElementType(),
                    method.getMetadata(),
                    getModelUtils());
        }
        return (MethodDescriptor) constructor.apply(method.getClassInfo(), DynamicModelManager.ENUM_HINT_TYPE,
                methodName,
                method.getReturnType(),
                method.getGenericReturnType(),
                method.getParameterTypes(),
                false,
                hintable,
                method.getModelElementType(),
                getModelUtils());
    }

    private void fillMethods(IClassInfo iClassInfo, MultiValueMap<String, EnumDescriptor> allEnumsFullSygn, MultiValueMap<String, String> allEnumNamesWithPackage) {
        String simpleName = getClassNameLC(iClassInfo);
        allEnumNamesWithPackage.computeIfAbsent(simpleName, name -> new LinkedList<>());
        if (!allEnumNamesWithPackage.get(simpleName).contains(iClassInfo.getClassName().toFullClassName())) {
            allEnumNamesWithPackage.get(simpleName).add(iClassInfo.getClassName().toFullClassName());
        }
        if (iClassInfo.isDynamic()) {
            allEnumsFullSygn.add(iClassInfo.getClassName().toFullClassName(), new EnumDescriptor(iClassInfo, DynamicModelManager.ENUM_HINT_TYPE,
                    simpleName, false, true, ModelElementType.BUSINESS_PROPERTY, getMetadata(iClassInfo.getClassName()), getModelUtils()));
        } else {
            Class clazz = getStaticClass((ClassInfo) iClassInfo);

            allEnumsFullSygn.add(iClassInfo.getClassName().toFullClassName(), new EnumDescriptor(iClassInfo, DynamicModelManager.ENUM_HINT_TYPE,
                    simpleName,
                    clazz,
                    clazz,
                    new Class[0],
                    false,
                    true,
                    ModelElementType.BUSINESS_PROPERTY,
                    getModelUtils()));
        }
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

    public String getFullEnumName(String name) {
        lock.readLock().lock();

        try {
            if (refreshNeeded) {
                rebuildMethods();
            }
            return getEnumName(name, cachedAliasToFullNameMapping);
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getShortEnumName(String fullName) {
        lock.readLock().lock();

        try {
            if (refreshNeeded) {
                rebuildMethods();
            }
            return getEnumName(fullName, cachedFullNameToAliasMapping);
        } finally {
            lock.readLock().unlock();
        }
    }

    private String getEnumName(String prevName, Map<String, String> aliasMapping) {
            String newName = aliasMapping.get(prevName);
            if (newName == null && prevName.contains(".")) {
                DynamicClassName splitName = DynamicClassName.forClassName(prevName);
                return aliasMapping.get(splitName.getPackageName()) + "." + splitName.getBaseClassName();
            }
            return newName;
    }

    @Override
    public boolean isGroupingElement() {
        return true;
    }

    public List<IClassInfo> listClasses() {
        List<IClassInfo> classInfos = new ArrayList<>();
        classInfos.addAll(ReflectionUtils.getAnnotatedClasses(Model.class, Object.class).stream().
                filter(Enum.class::isAssignableFrom).
                map(aClass -> new ClassInfo(false, DynamicClassName.forStaticBaseClass(aClass), aClass.getName())).
                collect(Collectors.toList()));

        classInfos.addAll(dynamicClassRepository.listClasses(DynamicClassFilter.ALL_CLASSES, DynamicClassArea.MODEL).stream().
                filter(iClassInfo ->
                    iClassInfo.isDynamic() && ((DynamicModelMetadata)dynamicClassRepository.getMetadata(iClassInfo.getClassName())).getDynamicClass().isEnumeration()
                ).collect(Collectors.toList()));

        return classInfos;
    }

    protected Class getStaticClass(ClassInfo classInfo) {
        return ReflectionUtils.tryGetClassForName(classInfo.getFullClassName());
    }

    protected String getClassNameLC(IClassInfo classInfo) {
        return StringUtils.firstLetterToLower(classInfo.getClassName().getBaseClassName());
    }

    public Set<DynamicClassName> resolveCalledEnums(String expression) {
        return searchCalledEnums(expression, true).stream().map(r -> DynamicClassName.forClassName(r.getName()).getOuterClassName()).collect(Collectors.toSet());
    }

    public List<GenericExpressionConverter.SymbolInExpression> searchCalledEnums(String expression, boolean forDependecies) {
        return genericExpressionConverter.searchCalledSymbols(expression, ENUM_PREFIX, forDependecies, true);
    }

    @Getter
    @Setter
    protected static class EnumDescriptor extends MethodDescriptor {
        private boolean initialized;

        private IClassInfo classInfo;

        private DynamicModelMetadata metadata;

        private UseCaseModelUtils typeUtils;

        public EnumDescriptor(IClassInfo classInfo, Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isStatic, boolean hintable, ModelElementType modelElementType, UseCaseModelUtils typeUtils) {
            super(declaringClass, name, returnType, genericReturnType, parameterTypes, isStatic, hintable, modelElementType);
            this.classInfo = classInfo;
            this.typeUtils = typeUtils;
            this.initialized = true;
        }

        public EnumDescriptor(IClassInfo iClassInfo, Class<?> declaringClass, String name, boolean isStatic, boolean hintable, ModelElementType modelElementType, DynamicModelMetadata metadata, UseCaseModelUtils typeUtils) {
            this(iClassInfo, declaringClass, name, null, null, new Class[0], isStatic, hintable, modelElementType,typeUtils);
            this.metadata = metadata;
            this.initialized = false;
        }

        @Override
        public boolean matches(String methodName, Class<?>[] paramClasses) {
            return getName().equals("get" + StringUtils.firstLetterToUpper(methodName));
        }

        @Override
        public String getName() {
            return "get" + StringUtils.firstLetterToUpper(super.getName());
        }

        public String getOriginalName() {
            return super.getName();
        }

        @Override
        public String getConvertedMethodName(String methodName) {
            return getConvertedMethodName(methodName, true);
        }

        @Override
        public String getConvertedMethodName(String methodName, boolean withGenerics) {
            return AbstractJavaCodeGenerator.toTypeLiteral(getReturnType());
        }

        @Override
        public Class<?> getReturnType() {
            if (!initialized) {
                init();
            }
            return super.getReturnType();
        }

        @Override
        public Type getGenericReturnType() {
            if (!initialized) {
                init();
            }
            return super.getGenericReturnType();
        }

        protected void init() {
            synchronized (this) {
                if (!initialized) {
                    try {
                        if (metadata != null && metadata.getDynamicClass() != null) {
                            Type enumType = typeUtils.getType(VariableType.of(metadata.getDynamicClassName().toFullClassName(), TypeMultiplicityEnum.Element));
                            setGenericReturnType(transformReturnType(enumType));
                            setReturnType(ReflectionUtils.getRawClass(enumType));
                        } else {
                            setReturnType(Void.class);
                            setGenericReturnType(Void.class);
                        }
                        initialized = true;
                    } catch (Exception e) {
                        initialized = true;
                        FhLogger.error(String.format("Enum '%s' contains fatal error", getName()), e);
                        setReturnType(Void.class);
                        setGenericReturnType(Void.class);
                    }
                }
            }
        }

        protected Type transformReturnType(Type enumType) {
            return enumType;
        }
    }

    @Getter
    @Setter
    public static class ValueOfDescriptor extends EnumDescriptor {
        public static final String NAME = "Values";

        public ValueOfDescriptor(IClassInfo classInfo, Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isStatic, boolean hintable, ModelElementType modelElementType, UseCaseModelUtils typeUtils) {
            super(classInfo, declaringClass, name, List.class, ReflectionUtils.createCollectionType(List.class, genericReturnType), parameterTypes, isStatic, hintable, modelElementType, typeUtils);
        }

        public ValueOfDescriptor(IClassInfo classInfo, Class<?> declaringClass, String name, boolean isStatic, boolean hintable, ModelElementType modelElementType, DynamicModelMetadata metadata, UseCaseModelUtils typeUtils) {
            super(classInfo, declaringClass, name, isStatic, hintable, modelElementType, metadata, typeUtils);
        }

        @Override
        public String getConvertedMethodName(String methodName) {
            return getConvertedMethodName(methodName, true);
        }

        @Override
        public String getConvertedMethodName(String methodName, boolean withGenerics) {
            return String.format("%s.asList(%s.values()",
                    AbstractJavaCodeGenerator.toTypeLiteral(Arrays.class),
                    AbstractJavaCodeGenerator.toTypeLiteral(ReflectionUtils.getGenericTypeInGenericType(getGenericReturnType(), 0)));
        }

        @Override
        public String getMethodAccessFormat() {
            return "%s";
        }

        @Override
        public boolean matches(String methodName, Class<?>[] paramClasses) {
            return getOriginalName().equals(methodName);
        }

        @Override
        public String getName() {
            return getOriginalName();
        }

        @Override
        protected Type transformReturnType(Type enumType) {
            return ReflectionUtils.createCollectionType(List.class, enumType);
        }
    }

    protected UseCaseModelUtils getModelUtils() {
        return typeUtils;
    }

    protected DynamicModelMetadata getMetadata(DynamicClassName className) {
        return dynamicClassRepository.getMetadata(className);
    }

    protected interface DescriptorConstructor<T> {
        T apply(IClassInfo classInfo, Class<?> declaringClass, String name, Class<?> returnType, Type genericReturnType, Class<?>[] parameterTypes, boolean isStatic, boolean hintable, ModelElementType modelElementType, UseCaseModelUtils typeUtils);
    }

    protected interface DynamicDescriptorConstructor<T> {
        T apply(IClassInfo iClassInfo, Class<?> declaringClass, String name, boolean isStatic, boolean hintable, ModelElementType modelElementType, DynamicModelMetadata metadata, UseCaseModelUtils typeUtils);
    }

    @Override
    public Type resolvePartsType(Type ofType, String parts) {
        MethodDescriptor methodDescriptor = getResolvedMethod(ofType, parts);
        if (methodDescriptor != null) {
            return methodDescriptor.getReturnType();
        }
        return ITypeProvider.super.resolvePartsType(ofType, parts);
    }

    @Override
    public MethodDescriptor getResolvedMethod(Type ofType, String parts) {
        lock.readLock().lock();

        if (refreshNeeded) {
            rebuildMethods();
        }
        try {
            if (!StringUtils.isNullOrEmpty(parts)) {
                if (cachedMethodsMap.containsKey(parts)) {
                    return cachedMethodsMap.get(parts);
                }
                if (cachedMethodsMap.containsKey(StringUtils.firstLetterToUpper(parts))) {
                    return cachedMethodsMap.get(StringUtils.firstLetterToUpper(parts));
                }
            }
            return ITypeProvider.super.getResolvedMethod(ofType, parts);
        } finally {
            lock.readLock().unlock();
        }
    }
}
