package pl.fhframework;

/**
 * Created by Gabriel on 14.05.2016.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.*;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.Form;
import pl.fhframework.subsystems.ModuleRegistry;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public static abstract class AnyGenericType {} // TODO: FH_Generics

    @AllArgsConstructor
    @Getter
    public static class SimpleParametrizedType implements ParameterizedType {

        private Class<?> rawMainClass;

        private Type paramClass;

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{paramClass};
        }

        @Override
        public Type getRawType() {
            return rawMainClass;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public String getTypeName() {
            return rawMainClass.getName() + "<" + paramClass.getTypeName() + ">";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleParametrizedType that = (SimpleParametrizedType) o;

            if (!rawMainClass.equals(that.rawMainClass)) return false;
            if (!paramClass.equals(that.paramClass)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return rawMainClass.hashCode();
        }
    }

    private static final Map<String, FhResource> CLASS_TO_URL_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class, Class> PRIMITIVES_TO_WRAPPERS = new HashMap<Class, Class>() {{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(short.class, Short.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(char.class, Character.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(void.class, Void.class);
    }};

    private static final Map<String, Class> PRIMITIVES_BY_NAME = new HashMap<String, Class>() {{
        put("boolean", boolean.class);
        put("byte", byte.class);
        put("short", short.class);
        put("int", int.class);
        put("long", long.class);
        put("char", char.class);
        put("double", double.class);
        put("float", float.class);
        put("void", void.class);
    }};

    public static Type extractTypeVariable(Type childType, Type parentType) {
        if (childType instanceof TypeVariable) {
            String typeVarName = TypeVariable.class.cast(childType).getName();
            TypeVariable[] genricParentTypeVars = ReflectionUtils.getRawClass(parentType).getTypeParameters();
            Type[] parentTypeVars = ReflectionUtils.getGenericArguments(parentType);
            for (int varIndex = 0; varIndex < parentTypeVars.length; varIndex++) { // TODO: FH_Generics
                if (parentTypeVars[varIndex] instanceof TypeVariable &&
                        ((TypeVariable) parentTypeVars[varIndex]).getName().equals(typeVarName) ||
                                genricParentTypeVars[varIndex].getName().equals(typeVarName)) {
                    return ReflectionUtils.getGenericArguments(parentType)[varIndex];
                }
            }
            // TODO: FH_Generics
            //throw new FhBindingException("Cannot resolve " + childType.toString() + " in " + parentType.toString());
            return AnyGenericType.class;
        } else if (childType instanceof Class) {
            return ReflectionUtils.resolveToRealClass((Class<?>) childType);
        } else {
            return childType;
        }
    }

    public static Class getGenericTypeInFieldType(Field field, int typeIndex) {
        if (field.getGenericType() instanceof ParameterizedType) {
            Type type = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[typeIndex];
            if (type instanceof Class) {
                return (Class) type;
            } else if (type instanceof TypeVariable) {
                Type[] bounds = ((TypeVariable) type).getBounds();
                if (bounds != null && bounds.length > 0 && bounds[0] instanceof Class) {
                    return (Class) bounds[0];
                }
            }
        }
        return null;
    }

    public static Class<?> getGenericTypeClassInImplementedInterface(Class<?> implementationClass, Class<?> interfaceClass, int typeIndex) {
        ResolvableType implementationType = ResolvableType.forType(implementationClass);
        return getGenericTypeClassInImplementedInterface(implementationType, interfaceClass, typeIndex);
    }

    private static Class<?> getGenericTypeClassInImplementedInterface(ResolvableType implementationType, Class<?> interfaceClass, int typeIndex) {
        while (implementationType != ResolvableType.NONE) {
            for (ResolvableType genericInterface : implementationType.getInterfaces()) {
                if (genericInterface.getRawClass() == interfaceClass) {
                    return genericInterface.resolveGeneric(typeIndex);
                }
                Class<?> extendedInterfacesResult = getGenericTypeClassInImplementedInterface(genericInterface, interfaceClass, typeIndex);
                if (extendedInterfacesResult != null) {
                    return extendedInterfacesResult;
                }
            }
            implementationType = implementationType.getSuperType();
        }
        return null;
    }

    public static Type getGenericTypeInGenericType(Type genericType, int typeIndex) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parametrizedType = (ParameterizedType) genericType;
            return parametrizedType.getActualTypeArguments()[typeIndex];
        } else {
            return null;
        }
    }

    public static Class<?> getGenericTypeClassInSuperclass(Class<?> implementationClass, int typeIndex) {
        return getRawClass(getGenericTypeInSuperclass(implementationClass, typeIndex));
    }

    public static Type getGenericTypeInSuperclass(Class<?> implementationClass, int typeIndex) {
        Type genericSuperclass = implementationClass.getGenericSuperclass();;
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType typedInterface = (ParameterizedType) genericSuperclass;
            return typedInterface.getActualTypeArguments()[typeIndex];
        }
        return null;
    }

    public static Type createCollectionType(Class<? extends Collection> collectionClass, Type elementType) {
        return new SimpleParametrizedType(collectionClass, elementType);
    }

    public static Type createParametrizedType(Class<?> mainClass, Type paramType) {
        return new SimpleParametrizedType(mainClass, paramType);
    }

    public static <A extends Annotation, T> void forEachAnnotatedMethod(T container, Class<A> annotationClazz, BiConsumer<Method, A> consumer) {
        forEachAnnotatedMethod(container.getClass(), annotationClazz, consumer);
    }

    public static <A extends Annotation, T> void forEachAnnotatedMethod(Class<T> containerClazz, Class<A> annotationClazz, BiConsumer<Method, A> consumer) {
        forEachAnnotatedMethod(containerClazz, annotationClazz, consumer, new HashSet<>());
    }

    private static <A extends Annotation, T> void forEachAnnotatedMethod(Class<T> containerClazz, Class<A> annotationClazz, BiConsumer<Method, A> consumer, Set<Method> consumed) {
        Set<Method> newConsumed = new HashSet<>();
        for (Method method : containerClazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Action.class)) {
                if (!method.isSynthetic() && !containsMethod(consumed, method)) {
                    A annotation = method.getDeclaredAnnotation(annotationClazz);
                    consumer.accept(method, annotation);
                    newConsumed.add(method);
                }
            }
        }
        consumed.addAll(newConsumed);
        if (containerClazz.getSuperclass()!=null){
            forEachAnnotatedMethod(containerClazz.getSuperclass(), annotationClazz, consumer, consumed);
        }
        for (Class<?> interf : containerClazz.getInterfaces()) { // e.g. default methods in interfaces
            forEachAnnotatedMethod(interf, annotationClazz, consumer, consumed);
        }
    }

    private static boolean containsMethod(Set<Method> consumed, Method method) {
        return consumed.stream().anyMatch(methodConsumed -> {
            if (methodConsumed.getName().equals(method.getName())) {
                if (methodConsumed.getGenericReturnType() == method.getGenericReturnType()) {
                    Type[] consmedTypes = methodConsumed.getGenericParameterTypes();
                    Type[] types = method.getGenericParameterTypes();
                    if (consmedTypes.length == types.length) {
                        for (int i = 0; i < consmedTypes.length; i++) {
                            if (consmedTypes[i] != types[i]) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
            return false;
        });
    }

    public static Object run(Method method, Object container, Object... AtributeValues) {
        if (method != null) {
            try {
                boolean accessible = method.isAccessible();
                if (!accessible) method.setAccessible(true);
                return method.invoke(container, AtributeValues);
            } catch (IllegalAccessException e) {
                FhLogger.error(e);
            } catch (IllegalArgumentException e) {
                String methodArgs = "";
                for (Class atr : method.getParameterTypes()) methodArgs += ", " + atr.getCanonicalName();

                String requiredArgs = "";
                for (Object atr : AtributeValues) {
                    requiredArgs += ", " + ((atr != null) ? atr.getClass().getCanonicalName() : "null");
                }
                if (!requiredArgs.isEmpty()) requiredArgs = requiredArgs.substring(2);
                if (!methodArgs.isEmpty()) methodArgs = methodArgs.substring(2);
                FhLogger.error("Method '{}.{}({})' Is incompatible with the expected in form xml file: {}({})!",
                        container.getClass().getName(), method.getName(), methodArgs, method.getName(), requiredArgs);
            } catch (InvocationTargetException ex){
                if (ex.getTargetException() instanceof FhAuthorizationException) {
                    throw (FhException) ex.getTargetException();
                }
                if (!(ex.getTargetException() instanceof FhDescribedException)) {
                    FhLogger.errorSuppressed("Method '{}.{}' throwed {}!", container.getClass().getName(), method.getName(), ex.getTargetException().getClass(), ex.getTargetException());
                }
                if (ex.getTargetException() instanceof FhMessageException || ex.getTargetException() instanceof FhDescribedNstException) {
                    throw (FhException) ex.getTargetException();
                }
                throw new ActionInvocationException(method.getName(), ex.getTargetException());
            } catch (Exception ex){
                FhLogger.errorSuppressed("Method '{}.{}' throwed {}!", container.getClass().getName(), method.getName(), ex.getClass(), ex);
                throw new ActionInvocationException(method.getName(), ex);
            }
        }
        return null;
    }

    public static <A extends Annotation> A giveClassAnnotations(Class<?> clazz, Class<A> annotationClazz) {
        return AnnotationUtils.findAnnotation(clazz, annotationClazz);
    }

    public static <T> List<Class<? extends T>> getNotAnnotatedClasses(String rootPackage, Class<? extends Annotation>... classAnnotations) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent();
            }
        };

        scanner.addIncludeFilter(new AssignableTypeFilter(Object.class));
        for (Class<? extends Annotation> classAnnotation : classAnnotations) {
            scanner.addExcludeFilter(new AnnotationTypeFilter(classAnnotation));
        }

        List<Class<? extends T>> annotatedClasses = new ArrayList<>();
        for (BeanDefinition bd : scanner.findCandidateComponents(rootPackage)) {
            String controllerClazzName = bd.getBeanClassName();
            try {
                Class<?> controllerClazz = FhCL.classLoader.loadClass(controllerClazzName);
                annotatedClasses.add((Class<T>) controllerClazz);
            } catch (ClassNotFoundException e) {
                throw new FhFrameworkException("Invalid application build - no class '" + controllerClazzName + "'");
            }
        }
        return annotatedClasses;
    }

    public static <T> List<Class<? extends T>> getAnnotatedClasses(String packageName, Collection<Class<? extends Annotation>> annotationClazz, Class<T> requiredBaseClazz) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                return beanDefinition.getMetadata().isIndependent();
            }
        };
        for (Class<? extends Annotation> annotation : annotationClazz) {
            scanner.addIncludeFilter(new AnnotationTypeFilter(annotation));
        }

        List<Class<? extends T>> annotatedClasses = new ArrayList<>();
        for (BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
            String controllerClazzName = bd.getBeanClassName();
            try {
                Class<?> controllerClazz = FhCL.classLoader.loadClass(controllerClazzName);
                if (requiredBaseClazz != null && !requiredBaseClazz.isAssignableFrom(controllerClazz)) {
                    throw new FhFrameworkException("Annotated class'" + controllerClazz.getName() + "' does not inherit the required type '" + requiredBaseClazz.getSimpleName() + "'!!!");
                }
                annotatedClasses.add((Class<T>) controllerClazz);
            } catch (ClassNotFoundException e) {
                throw new FhFrameworkException("Invalid application build - no class '" + controllerClazzName + "'");
            }
        }
        return annotatedClasses;
    }


    public static <T> List<Class<? extends T>> getAnnotatedClasses(Class<? extends Annotation> annotationClazz, Class<T> filterBaseClazz) {
        return ModuleRegistry.getModulesBasePackages().stream().map(pcg -> {
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
                @Override
                protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                    return beanDefinition.getMetadata().isIndependent();
                }
            };
            scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClazz));

            List<Class<? extends T>> annotatedClasses = new ArrayList<>();
            for (BeanDefinition bd : scanner.findCandidateComponents(pcg)) {
                String controllerClazzName = bd.getBeanClassName();
                try {
                    Class<?> controllerClazz = FhCL.classLoader.loadClass(controllerClazzName);
                    if (filterBaseClazz != null && filterBaseClazz.isAssignableFrom(controllerClazz)) {
                        annotatedClasses.add((Class<T>) controllerClazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new FhFrameworkException("Invalid application build - no class '" + controllerClazzName + "'");
                }
            }
            return annotatedClasses;
        }).flatMap(List::stream).collect(Collectors.toList());
    }

    public static <T> List<Class<? extends T>> getAnnotatedClasses(String packageName, Class<? extends Annotation> annotationClazz, Class<T> requiredBaseClazz) {
        List<Class<? extends Annotation>> annotations = new ArrayList<>();
        annotations.add(annotationClazz);
        return getAnnotatedClasses(packageName, annotations, requiredBaseClazz);
    }

    public static List<Class<?>> getAnnotatedClasses(String packageName, Class<? extends Annotation> annotationClazz) {
        return getAnnotatedClasses(packageName, annotationClazz, null);
    }

    public static <T> List<Class<? extends T>> getAnnotatedClasses(Class packageIndicator, Class<? extends Annotation> annotationClazz, Class<T> requiredBaseClazz) {
        return getAnnotatedClasses(packageIndicator.getPackage().getName(), annotationClazz, requiredBaseClazz);
    }

    public static <T> List<Class<? extends T>> giveClassesTypeList(String packageName, Class<? extends T> requiredType) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(requiredType));
        List<Class<? extends T>> classesFound = new ArrayList<>();
        for (BeanDefinition bd : scanner.findCandidateComponents(packageName)) {
            String clazzName = bd.getBeanClassName();
            try {
                Class<?> foundClazz = FhCL.classLoader.loadClass(clazzName);
                classesFound.add((Class<T>) foundClazz);
            } catch (ClassNotFoundException e) {
                throw new FhFrameworkException("Invalid application build - no class '" + clazzName + "'");
            }
        }
        return classesFound;
    }

    public static <T> List<Class<? extends T>> giveClassesTypeList(Class<?> packageIndicator, Class<? extends T> requiredType) {
        return giveClassesTypeList(packageIndicator.getPackage().getName(), requiredType);
    }

    public static <T> List<Class<? extends T>> giveClassesTypeList(Class<? extends T> requiredType) {
        return ModuleRegistry.getModulesBasePackages().stream().map(modulePackage -> giveClassesTypeList(modulePackage, requiredType)).flatMap(List::stream).collect(Collectors.toList());
    }

    public static FhResource basePath(Class<?> classFromSubsystem) {
        FhResource baseUrl = baseClassPath(classFromSubsystem);
        /*Optional<Path> basePath = FileUtils.getFile(baseUrl);
        if (basePath.isPresent()) {
            return basePath.get();
        } else {
            throw new RuntimeException("Base url is not a plain path: " + baseUrl);
        }*/

        if (baseUrl != null) {
            return baseUrl;
        } else {
            throw new RuntimeException("Base url is not a plain path: " + baseUrl);
        }

    }

    public static FhResource baseClassPath(Class<?> classFromSubsystem) {
        String className = classFromSubsystem.getName();
        if (CLASS_TO_URL_CACHE.containsKey(className)) {
            return CLASS_TO_URL_CACHE.get(className);
        } else {
            String classpathRelativeLocation = "/" + className.replace('.', '/') + ".class";
            FhResource classpathURL = FhResource.get(classFromSubsystem.getResource(classpathRelativeLocation));
            if (classpathURL == null) {
                throw new FhException("Cannot find class resource " + classpathRelativeLocation);
            }
            String classUrl = classpathURL.getExternalPath().toString();
            if (classpathURL.getURL().toExternalForm().endsWith(classpathRelativeLocation)) {
                String baseUrlString = classUrl.substring(0, classUrl.length() - classpathRelativeLocation.length());
                if (classpathURL.getURL().getProtocol().equals("jar") && baseUrlString.indexOf('!') == baseUrlString.length() - 1) {
                    baseUrlString += '/';
                }
                FhResource baseUrl = FhResource.get(baseUrlString);
                CLASS_TO_URL_CACHE.put(className, baseUrl);
                return baseUrl;
            } else {
                throw new RuntimeException("Base URL " + classpathURL + " doesn't end with " + classpathRelativeLocation);
            }
        }
    }

    // TODO: remove?
    public static String packagePathForClass(Class<?> packageIndicator) {
        String packageIndicatorRelativePath = packageIndicator.getPackage().getName().replace('.', '/');
        URL url = packageIndicator.getProtectionDomain().getCodeSource().getLocation();
        Path path;
        try {
            path = FhResource.get(url).getExternalPath();
        } catch (Exception exc){
            throw new FhFormException("Unknown Exception");
        }
        if (url.getProtocol().equals("jar")) {
            path = path.getParent();
        } else {
            path = path.getParent();
        }

        //Checking source access
        Path developmentPath = path.getParent().resolve("src/main/java").resolve(packageIndicatorRelativePath);
        if (Files.exists(developmentPath)) {
            return developmentPath.toAbsolutePath().toString();
        } else {
            return path.resolve(packageIndicatorRelativePath).toAbsolutePath().toString();
        }
    }

    public static <T> T createClassObject(Class<T> clazz) {
        try {
            T instance = clazz.newInstance();
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            FhLogger.error("Failed to create class object '{}'!", clazz.getName(), e);
            throw new FhFrameworkException(e);
        }
    }

    public static <T> T createFormClassObject(Class<T> clazz, Form form) {
        try {
            T instance = clazz.getDeclaredConstructor(Form.class).newInstance(form);
            return instance;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            FhLogger.error("Failed to create class object '{}'!", clazz.getName(), e);
            throw new FhFrameworkException(e);
        }
    }

    public static Optional<Field> getPublicField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getFields()) {
            if (field.getName().equals(fieldName)) {
                return Optional.of(field);
            }
        }
        return Optional.empty();
    }

    public static Optional<Field> getPrivateField(Class<?> clazz, String fieldName) {
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    return Optional.of(field);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
        return Optional.empty();
    }

    public static List<Field> getFields(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                fields.add(field);
            }
        }
        return fields;
    }

    public static List<Field> getFieldsWithHierarchy(Class<?> clazz, Class<? extends Annotation> annotation) {
        List<Field> fields = new ArrayList<>();
        do {
            fields.addAll(getFields(clazz, annotation));
            clazz = clazz.getSuperclass();
        } while(clazz != null && clazz != Object.class);
        return fields;
    }

    public static List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            fields.add(field);
        }
        return fields;
    }

    public static List<Field> getFieldsWithHierarchy(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        do {
            fields.addAll(getFields(clazz));
            clazz = clazz.getSuperclass();
        } while(clazz != null && clazz != Object.class);
        return fields;
    }

    public static Class getFieldType(Class<?> clazz, String fieldName) {
        List<Field> fields = getFieldsWithHierarchy(clazz);
        for (Field field : fields) {
            if (fieldName.equals(field.getName())) {
                return field.getType();
            }
        }

        return null;
    }

    /**
     * Finds a public constructor.
     * @param clazz constructed class
     * @param argTypes types of arguments in constructor
     * @return optional constructor
     */
    public static <T> Optional<Constructor> findConstructor(Class<T> clazz, Class... argTypes) {
        for (Constructor constructor : clazz.getConstructors()) { // public only
            if (Arrays.equals(argTypes, constructor.getParameterTypes())) {
                return Optional.of(constructor);
            }
        }
        for (Constructor constructor : clazz.getConstructors()) {
            if (argTypes.length == constructor.getParameterTypes().length) {
                boolean match = true;
                for (int i = 0; i < argTypes.length; i++) {
                    if (!constructor.getParameterTypes()[i].isAssignableFrom(argTypes[i])){
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return Optional.of(constructor);
                }
            }
            if (Arrays.equals(argTypes, constructor.getParameterTypes())) {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a public setter of property.
     * @param clazz owner class
     * @param property property name
     * @param type desired property type (optional)
     * @return setter method (optional)
     */
    public static Optional<Method> findSetter(Class clazz, String property, Optional<Class<?>> type) {
        String methodName = getSetterName(property);
        for (Method method : clazz.getMethods()) {
            if (method.isBridge()) {
                continue;
            }
            if (method.getName().equals(methodName)
                    && method.getParameterTypes().length == 1
                    && (!type.isPresent() || method.getParameterTypes()[0] == type.get())) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a public setter of field.
     * @param clazz owner class
     * @param field field
     * @return setter method (optional)
     */
    public static Optional<Method> findSetter(Class clazz, Field field) {
        return findSetter(clazz, field.getName(), Optional.of(field.getType()));
    }

    /**
     * Finds a public getter of property.
     * @param clazz owner class
     * @param property property name
     * @param type desired property type (optional)
     * @return getter method (optional)
     */
    public static Optional<Method> findGetter(Class clazz, String property, Optional<Class<?>> type) {
        String getterGet = "get" + StringUtils.firstLetterToUpper(property);
        String getterIs = "is" + StringUtils.firstLetterToUpper(property);
        for (Method method : clazz.getMethods()) {
            if (method.isBridge()) {
                continue;
            }
            Class<?> returnType = method.getReturnType();
            boolean isBoolean = returnType == Boolean.class || returnType == boolean.class;
            if (method.getParameterTypes().length == 0
                    && (method.getName().equals(getterGet) || (isBoolean && method.getName().equals(getterIs)))
                    && (!type.isPresent() || type.get() == returnType)) {
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a public getter of field.
     * @param clazz owner class
     * @param field field
     * @return getter method (optional)
     */
    public static Optional<Method> findGetter(Class clazz, Field field) {
        return findGetter(clazz, field.getName(), Optional.of(field.getType()));
    }

    /**
     * Proposes a getter name
     * @param propertyName property name
     * @param returnedType type
     * @return proposed getter name
     */
    public static String getGetterName(String propertyName, Class<?> returnedType) {
        if (returnedType == boolean.class) {
            return "is" + StringUtils.firstLetterToUpper(propertyName);
        } else {
            return "get" + StringUtils.firstLetterToUpper(propertyName);
        }
    }

    /**
     * Proposes a getter name as if it was object.
     * @param propertyName property name
     * @return proposed getter name
     */
    public static String getObjectGetterName(String propertyName) {
        return "get" + StringUtils.firstLetterToUpper(propertyName);
    }

    /**
     * Proposes a setter name
     * @param propertyName property name
     * @return proposed setter name
     */
    public static String getSetterName(String propertyName) {
        return "set" + StringUtils.firstLetterToUpper(propertyName);
    }


    /**
     * Is type primitive type.
     * @param type type
     * @return true if type is primitive, otherwise false
     */
    public static boolean isPrimitive(Type type) {
        return type != null && PRIMITIVES_TO_WRAPPERS.containsKey(type);
    }

    /**
     * Maps a class to its non-primitive form. Primitive classes are mapped to wrapper classes, non-primitive classes are returned without mapping.
     * @param type class
     * @return non-primitive form of the class
     */
    public static Type mapPrimitiveToWrapper(Type type) {
        if (Class.class.isInstance(type) && PRIMITIVES_TO_WRAPPERS.containsKey(type)) {
            return PRIMITIVES_TO_WRAPPERS.get(type);
        } else {
            return type;
        }
    }

    /**
     * Maps a class to its non-primitive form. Primitive classes are mapped to wrapper classes, non-primitive classes are returned without mapping.
     * @param clazz class
     * @return non-primitive form of the class
     */
    public static Class mapPrimitiveToWrapper(Class clazz) {
        if (PRIMITIVES_TO_WRAPPERS.containsKey(clazz)) {
            return PRIMITIVES_TO_WRAPPERS.get(clazz);
        } else {
            return clazz;
        }
    }

    /**
     * Maps a wrapper class to its primitive form. Non-wrapper classes are returned without mapping.
     * @param clazz class
     * @return primitive form of the class
     */
    public static Class<?> mapWrapperToPrimitive(Class<?> clazz) {
        for (Map.Entry<Class, Class> mapping : PRIMITIVES_TO_WRAPPERS.entrySet()) {
            if (mapping.getValue() == clazz) {
                return mapping.getKey();
            }
        }
        return clazz;
    }

    public static Type[] getGenericArguments(Type type) {
        if (type == null || type instanceof Class) {
            return new Class<?>[0];
        } else if (type instanceof ParameterizedType) {
            return ParameterizedType.class.cast(type).getActualTypeArguments();
        } else if (type instanceof TypeVariable){
            return new Class<?> [] {AnyGenericType.class}; // TODO: FH_Generics
        } else {
            throw new RuntimeException("Not supported type: " + type.getClass().getName());
        }
    }

    public static Class<?>[] getGenericArgumentsRawClasses(Type type) {
        return getGenericArgumentsRawClasses(type, false);
    }

    public static Class<?>[] tryGetGenericArgumentsRawClasses(Type type) {
        return getGenericArgumentsRawClasses(type, true);
    }

    protected static Class<?>[] getGenericArgumentsRawClasses(Type type, boolean tryGet) {
        Type[] genericTypes = getGenericArguments(type);
        Class<?>[] genericClasses = new Class<?>[genericTypes.length];
        for (int i = 0; i < genericTypes.length; i++) {
            if (tryGet) {
                genericClasses[i] = tryGetRawClass(genericTypes[i]);
            }
            else {
                genericClasses[i] = getRawClass(genericTypes[i]);
            }
        }
        return genericClasses;
    }

    public static Class<?> getRawClass(Type type) {
        return getRawClass(type, true);
    }

    public static Class<?> tryGetRawClass(Type type) {
        return getRawClass(type, false);
    }

    protected static Class<?> getRawClass(Type type, boolean throwExp) {
        if (type == null) {
            return null;
        } else if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ParameterizedType.class.cast(type).getRawType();
        } else if (type instanceof GenericArrayType) {
            return Array.class;
        } else if (throwExp) {
            return AnyGenericType.class; // TODO: FH_Generics
        }
        return null;
    }

    public static boolean instanceOf(Type type, Class<?> superType) {
        if (type instanceof Class) {
            return superType.isAssignableFrom((Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return superType.isAssignableFrom((Class<?>) ParameterizedType.class.cast(type).getRawType());
        } else {
            return false;
        }
    }

    public static Class<?> tryGetClassForName(String fullClassName, ClassLoader classLoader) {
        if (PRIMITIVES_BY_NAME.containsKey(fullClassName)) {
            return PRIMITIVES_BY_NAME.get(fullClassName);
        }

        String typeFullNameTry = fullClassName;
        // try to deduce class name - if necessary replace last . with $ to match inner classes
        while (true) {
            try {
                return Class.forName(typeFullNameTry, true, classLoader);
            } catch (ClassNotFoundException e) {
                int lastDot = typeFullNameTry.lastIndexOf('.');
                if (lastDot > 0) { // -1 not found, 0 starts with a dot
                    typeFullNameTry = typeFullNameTry.substring(0, lastDot) + '$' + typeFullNameTry.substring(lastDot + 1);
                } else {
                    return null;
                }
            }
        }
    }

    public static Class<?> getClassForName(String fullClassName, ClassLoader classLoader) {
        Class<?> clazz = tryGetClassForName(fullClassName, classLoader);
        if (clazz != null) {
            return clazz;
        } else {
            throw new FhException("Cannot find type: " + fullClassName);
        }
    }

    public static Type tryGetTypeForName(String fullTypeName, ClassLoader classLoader) {
        if (!fullTypeName.contains("<") && !fullTypeName.contains(">")) {
            return tryGetClassForName(fullTypeName);
        }
        String genericArgs = fullTypeName.substring(fullTypeName.indexOf('<') + 1, fullTypeName.indexOf('>'));
        Class<?> mainClass = tryGetClassForName(fullTypeName.substring(0, fullTypeName.indexOf('<')));
        String[] genericsArgsArray = genericArgs.split(",");
        Type[] typesArray = new Type[genericsArgsArray.length];
        for (int i = 0; i < typesArray.length; i++) {
            typesArray[i] = tryGetTypeForName(genericsArgsArray[i].trim());
        }

        return TypeUtils.parameterize(mainClass, typesArray);
    }


    public static Class<?> tryGetClassForName(String fullClassName) {
        return tryGetClassForName(fullClassName, FhCL.classLoader);
    }

    public static Class<?> getClassForName(String fullClassName) {
        return getClassForName(fullClassName, FhCL.classLoader);
    }

    public static Type tryGetTypeForName(String fullTypeName) {
        return tryGetTypeForName(fullTypeName, FhCL.classLoader);
    }
    /**
     * Finds a matching public method.
     * @param clazz class
     * @param methodName method name
     * @param paramClasses classes of parameters
     * @return method (optional)
     */
    public static Optional<Method> findMatchingPublicMethod(Class<?> clazz, String methodName, Class<?>... paramClasses) {
        methodLoop:
        for (Method foundMethod : clazz.getMethods()) {
            if (foundMethod.isBridge()) {
                continue;
            }
            // name matches
            if (!foundMethod.getName().equals(methodName)) {
                continue;
            }
            // nof arguments matches
            Class<?>[] foundParamClasses = foundMethod.getParameterTypes();
            if (foundParamClasses.length != paramClasses.length) {
                continue;
            }
            // each argument's class matches
            for (int i = 0; i < paramClasses.length; i++) {
                if (!ReflectionUtils.isAssignablFrom(foundParamClasses[i], paramClasses[i])) {
                    continue methodLoop;
                }
            }
            return Optional.of(foundMethod);
        }
        if (clazz.isInterface()) {
            return findMatchingPublicMethod(Object.class, methodName, paramClasses);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Finds first matching public method.
     * @param clazz class
     * @param methodName method name
     * @return method (optional)
     */
    public static Optional<Method> findMatchingPublicMethod(Class<?> clazz, String methodName) {
        methodLoop:
        for (Method foundMethod : clazz.getMethods()) {
            if (foundMethod.isBridge()) {
                continue;
            }
            // name matches
            if (!foundMethod.getName().equals(methodName)) {
                continue;
            }

            return Optional.of(foundMethod);
        }
        if (clazz.isInterface()) {
            return findMatchingPublicMethod(Object.class, methodName);
        } else {
            return Optional.empty();
        }
    }

    public static Class<?> resolveToRealClass(Class<?> clazz) {
        if (clazz != null && clazz.getName().contains("_$$_")) {
            return clazz.getSuperclass();
        } else {
            return clazz;
        }
    }

    public static Class<?> getRealClass(Object object) {
        return AopUtils.getTargetClass(object);
    }

    public static <T> T getRealObject(T object) {
        if (Advised.class.isInstance(object)) {
            try {
                return (T) ((Advised)object).getTargetSource().getTarget();
            } catch (Exception e) {
                throw new FhUseCaseException(e);
            }
        }
        return object;
    }

    public static boolean objectsEqual(Object object1, Object object2) {
        Object realObject1 = getRealObject(object1);
        Object realObject2 = getRealObject(object2);

        return Objects.equals(realObject1, realObject2);
    }

    /**
     * Initializes and unproxies entity.
     * @param entity - object to initialize and unproxy
     * @param <T>
     * @return - unproxied object if aObiekt is instance of {@link HibernateProxy} otherwise returns aObject
     */
    public static <T> T initializeAndUnproxy(T entity) {
        if (entity == null) {
            throw new
                    NullPointerException("Entity passed for initialization is null");
        }
        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer()
                    .getImplementation();
        }
        return entity;
    }

    /**
     * Finds enum constant with given name ignoring case. First tries to find an exact match.
     * @param enumClass enum class
     * @param value value
     * @param <E> enum type
     * @return optional enum constant
     */
    public static <E extends Enum> Optional<E> findEnumConstantIgnoreCase(Class<E> enumClass, String value) {
        E caseIgnoreMatch = null;
        for (E singleEnumValue : enumClass.getEnumConstants()) {
            if (singleEnumValue.name().equals(value)) {
                return Optional.of(singleEnumValue);
            } else if (singleEnumValue.name().equalsIgnoreCase(value)) {
                caseIgnoreMatch = singleEnumValue;
            }
        }
        return Optional.ofNullable(caseIgnoreMatch);
    }

    public static boolean isAssignablFrom(Type type1, Type type2) {
        Class mainClass = getRawClass(type1);
        boolean mainMatch = isAssignablFrom(mainClass, getRawClass(type2));
        if (mainMatch && isAssignablFrom(Collection.class, mainClass)) {
            Type[] type1Args = getGenericArguments(type1);
            Type[] type2Args = getGenericArguments(type2);
            return (type1Args.length == 0 || type2Args.length == 0 || (type1Args.length == type2Args.length && isAssignablFrom(type1Args[0], type2Args[0])));
        }
        return mainMatch;
    }

    public static boolean isAssignablFrom(Class<?> class1, Class<?> class2) {
        if (class1 == AnyGenericType.class || class2 == AnyGenericType.class) {
            return true; // TODO: FH_Generics, for now delegate generics check till compile time
        }
        if (isGeneratedDynamicClass(class1) && isGeneratedDynamicClass(class2)) {
            return getClassName(class1).equals(getClassName(class2));
        }
        return mapPrimitiveToWrapper(class1).isAssignableFrom(mapPrimitiveToWrapper(class2));
    }

    /**
     * Create a new instance of a given class.
     * @param clazz class
     * @param args arguments
     * @param <T> type
     * @return new instance
     */
    public static <T> T newInstance(Class<T> clazz, Object... args) {
        try {
            List<Class<?>> classArgs = new ArrayList<>();
            if (args != null) {
                for (Object arg : args) {
                    classArgs.add(arg.getClass());
                }
            }
            Optional<Constructor> constructor = findConstructor(clazz, classArgs.toArray(new Class<?>[classArgs.size()]));
            if (constructor.isPresent()) {
                return (T) constructor.get().newInstance(args);
            }
            else {
                throw new NoSuchMethodException("No constructor for " + clazz + " : " + args);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T extends Annotation> Optional<T> getMethodParamAnnotation(Executable method, int paramIndex, Class<T> annotationClass) {
        return Arrays.asList(method.getParameterAnnotations()[paramIndex])
                .stream()
                .filter(a -> annotationClass.isAssignableFrom(a.getClass()))
                .map(annotationClass::cast)
                .findFirst();
    }

    public static String getClassName(Class clazz) {
        GeneratedDynamicClass dynamicClass = (GeneratedDynamicClass) clazz.getAnnotation(GeneratedDynamicClass.class);
        if (dynamicClass != null) {
            return dynamicClass.value();
        }
        return clazz.getName();
    }

    public static String getSimpleClassName(Class clazz) {
        GeneratedDynamicClass dynamicClass = (GeneratedDynamicClass) clazz.getAnnotation(GeneratedDynamicClass.class);
        if (dynamicClass != null) {
            return getSimpleClassName(dynamicClass.value());
        }
        return clazz.getSimpleName();
    }

    public static String getSimpleClassName(String clazz) {
        return DynamicClassName.forClassName(clazz).getBaseClassName();
    }

    public static boolean isGeneratedDynamicClass(Class clazz) {
        GeneratedDynamicClass dynamicClass = (GeneratedDynamicClass) clazz.getAnnotation(GeneratedDynamicClass.class);
        if (dynamicClass != null) {
            return true;
        }
        return false;
    }

    public static boolean isReadOnlyFieldProperty(Field field) {
        return field.isEnumConstant()
                || Modifier.isFinal(field.getModifiers());
    }
}

