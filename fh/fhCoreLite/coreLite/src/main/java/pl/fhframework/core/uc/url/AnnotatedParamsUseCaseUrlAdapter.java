package pl.fhframework.core.uc.url;

import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.uc.*;
import pl.fhframework.core.util.CollectionsUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.format.FhConversionService;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Default implementation of an use case URL adapter which is responsible for starting use case based on URL.
 */
public class AnnotatedParamsUseCaseUrlAdapter implements IUseCaseUrlAdapter<IUseCase<? extends IUseCaseOutputCallback>> {

    @ToString
    protected static class ParamAnnotationMetadata {

        /**
         * Index if to be used in path
         */
        private int positionalIndex = -1;

        /**
         * Name if to be used in query
         */
        private String nameInQuery;

        /**
         * Index of the outer, real input param of an use case
         */
        private int inputParamIndex;

        /**
         * Class of the outer, real input param of an use case
         */
        private Class<?> inputParamClass;

        /**
         * Class of the param class
         */
        private Class<?> thisParamClass;

        private boolean optional;

        /**
         * Field chain to access the real
         */
        private List<Field> nestedFieldChain = new ArrayList<>();
    }

    @ToString
    protected static class UseCaseAnnotationMetadata {

        private List<ParamAnnotationMetadata> params = new ArrayList<>();
    }

    private static Map<Class<?>, UseCaseAnnotationMetadata> METADATA_CACHE = new WeakHashMap<>();

    @Autowired
    protected FhConversionService conversionService;

    @Override
    public boolean startFromURL(IUseCase useCase, UseCaseUrl url) {
        if (useCase instanceof ICustomUseCase) {
            ((ICustomUseCase) useCase).start();
            return true;
        }

        Optional<Object[]> inputParams = extractParameters(useCase.getClass(), url);

        if (!inputParams.isPresent()) {
            return false;
        }

        if (useCase instanceof IUseCaseNoInput) {
            ((IUseCaseNoInput) useCase).start();
        } else if (useCase instanceof IUseCaseOneInput) {
            ((IUseCaseOneInput) useCase).start(inputParams.get()[0]);
        } else if (useCase instanceof IUseCaseTwoInput) {
            ((IUseCaseTwoInput) useCase).start(inputParams.get()[0], inputParams.get()[1]);
        }
        return true;
    }

    @Override
    public boolean exposeURL(IUseCase useCase, UseCaseUrl url, Object[] params) {
        try {
            UseCaseAnnotationMetadata metadata = getMetadata(useCase.getClass());
            // For ICustomeUseCase passed params values are unknown
            if (!ICustomUseCase.class.isInstance(useCase)) {
                for (ParamAnnotationMetadata paramMetadata : metadata.params) {
                    Object paramValue = params[paramMetadata.inputParamIndex];
                    paramValue = getNestedObject(paramValue, paramMetadata.nestedFieldChain);

                    String paramString = valueToString(paramValue);
                    if (!paramMetadata.optional && paramString == null) {
                        return false;
                    }
                    if (StringUtils.isNullOrEmpty(paramMetadata.nameInQuery)) {
                        url.putPositionalParameter(paramMetadata.positionalIndex, paramString);
                    } else {
                        url.putNamedParameter(paramMetadata.nameInQuery, paramString);
                    }
                }
            }
            return true;
        } catch (Throwable e) {
            FhLogger.errorSuppressed("Error exposing URL: {}", url, e);
            return false;
        }
    }

    @Override
    public Optional<Object[]> extractParameters(Class useCaseClass, UseCaseUrl url) {
        Object[] inputParams; // up to n parameters
        try {
            UseCaseAnnotationMetadata metadata = getMetadata(useCaseClass);
            inputParams = new Object[metadata.params.size()]; // up to n parameters
            for (ParamAnnotationMetadata paramMetadata : metadata.params) {
                String urlParamString;
                if (StringUtils.isNullOrEmpty(paramMetadata.nameInQuery)) {
                    urlParamString = url.getPositionalParameter(paramMetadata.positionalIndex);
                } else {
                    urlParamString = url.getNamedParameter(paramMetadata.nameInQuery);
                }
                if (!paramMetadata.optional && urlParamString == null) {
                    return Optional.empty();
                }

                Object urlParamValue = stringToValue(paramMetadata.thisParamClass, urlParamString);
                Object inputParamValue;
                if (paramMetadata.nestedFieldChain.isEmpty()) {
                    inputParamValue = urlParamValue;
                } else {
                    inputParamValue = inputParams[paramMetadata.inputParamIndex];
                    if (inputParamValue == null) {
                        inputParamValue = ReflectionUtils.newInstance(paramMetadata.inputParamClass);
                    }
                    setNestedObject(inputParamValue, urlParamValue, paramMetadata.nestedFieldChain);
                }

                inputParams[paramMetadata.inputParamIndex] = inputParamValue;
            }
        } catch (Throwable e) {
            FhLogger.errorSuppressed("Error exposing URL: {}", url, e);
            return Optional.empty();
        }

        return Optional.of(inputParams);
    }


    protected Object getNestedObject(Object value, List<Field> fieldChain) {
        for (Field field : fieldChain) {
            if (value == null) {
                return null;
            }
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            value = org.springframework.util.ReflectionUtils.getField(field, value);
        }
        return value;
    }

    protected void setNestedObject(Object target, Object value, List<Field> fieldChain) {
        // ensure intermediate objects and get final target object
        for (Field field : CollectionsUtils.getWithoutLast(fieldChain)) {
            // get nested (next target) object
            field.setAccessible(true);
            Object nestedTarget = org.springframework.util.ReflectionUtils.getField(field, target);
            if (nestedTarget == null) {
                // create missing object
                nestedTarget = ReflectionUtils.newInstance(field.getType());
                // set it in parent's field
                org.springframework.util.ReflectionUtils.setField(field, target, nestedTarget);
            }
            target = nestedTarget;
        }

        // now set the last field
        Field lastField = CollectionsUtils.getLast(fieldChain);
        lastField.setAccessible(true);
        org.springframework.util.ReflectionUtils.setField(lastField, target, value);
    }

    protected String valueToString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Enum) {
            return ((Enum) value).name();
        }
        if (value instanceof BaseEntity) {
            return valueToString(((BaseEntity) value).getEntityId());
        }
        Class<?> valueClass = value.getClass();
        if (conversionService.canConvert(valueClass, String.class)) {
            return (String) conversionService.convert(value, TypeDescriptor.valueOf(valueClass), TypeDescriptor.valueOf(String.class));
        } else {
            throw new FhUseCaseException("Cannot convert URL param type " + valueClass.getName());
        }
    }

    protected Object stringToValue(Class<?> clazz, String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        if (String.class.isAssignableFrom(clazz)) {
            return text;
        }
        if (clazz.isEnum()) {
            return Enum.valueOf((Class<Enum>) clazz, text);
        }
        if (BaseEntity.class.isAssignableFrom(clazz)) {
            BaseEntity baseEntity = (BaseEntity) ReflectionUtils.newInstance(clazz);
            Class<?> idClass = ReflectionUtils.getGenericTypeClassInImplementedInterface(clazz, BaseEntity.class, 0);
            baseEntity.setEntityId(stringToValue(idClass, text));
            return baseEntity;
        }

        if (conversionService.canConvert(String.class, clazz)) {
            return conversionService.convert(text, TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(clazz));
        } else {
            throw new FhUseCaseException("Cannot convert URL param type " + clazz.getName());
        }
    }

    protected UseCaseAnnotationMetadata getMetadata(Class<?> useCaseClazz) {
        synchronized (METADATA_CACHE) {
            if (!METADATA_CACHE.containsKey(useCaseClazz)) {
                METADATA_CACHE.put(useCaseClazz, readMetadata(useCaseClazz));
            }
            return METADATA_CACHE.get(useCaseClazz);
        }
    }

    private UseCaseAnnotationMetadata readMetadata(Class<?> useCaseClazz) {
        Set<Integer> takenPositionals = new HashSet<>();

        UseCaseAnnotationMetadata metadata = new UseCaseAnnotationMetadata();

        Executable startMethod = findStartMethod(useCaseClazz);

        for (int paramIndex = 0, inputParamIndex = 0; paramIndex < startMethod.getParameterCount(); paramIndex++) {
            Class<?> inputParamClass = ReflectionUtils.getRawClass(ReflectionUtils.extractTypeVariable(
                    startMethod.getGenericParameterTypes()[paramIndex], useCaseClazz));
            if (!IUseCaseOutputCallback.class.isAssignableFrom(inputParamClass)) {
                // get annotation
                Optional<UrlParam> paramAnnotation = ReflectionUtils.getMethodParamAnnotation(startMethod, paramIndex, UrlParam.class);
                Optional<UrlParamWrapper> wrapperAnnotation = ReflectionUtils.getMethodParamAnnotation(startMethod, paramIndex, UrlParamWrapper.class);
                Optional<UrlParamIgnored> ignoredAnnotation = ReflectionUtils.getMethodParamAnnotation(startMethod, paramIndex, UrlParamIgnored.class);

                if (ignoredAnnotation.isPresent()) {
                    continue;
                }

                if (paramAnnotation.isPresent() && wrapperAnnotation.isPresent()) {
                    throw new FhUseCaseException(String.format("%s.start() method parameter can't have both %s and %s annotations.",
                            useCaseClazz.getSimpleName(), UrlParam.class.getSimpleName(), UrlParamWrapper.class.getSimpleName()));
                }

                readParamMetadata(metadata, inputParamIndex, inputParamClass, inputParamClass, paramAnnotation, wrapperAnnotation, takenPositionals, Collections.emptyList(), "", null);

                inputParamIndex++;
            }
        }
        return metadata;
    }

    protected void readParamMetadata(UseCaseAnnotationMetadata metadata,
                                     int inputParamIndex, Class<?> inputParamClass,
                                     Class<?> thisParamClass,
                                     Optional<UrlParam> paramAnnotation, Optional<UrlParamWrapper> wrapperAnnotation,
                                     Set<Integer> takenPositionals,
                                     List<Field> fieldChain,
                                     String namePrefix,
                                     String defaultName) {
        if (wrapperAnnotation.isPresent()) {
            org.springframework.util.ReflectionUtils.FieldCallback processor = field -> {
                if (paramAnnotation.isPresent() && wrapperAnnotation.isPresent()) {
                    String chainString = fieldChain.stream().map(Field::getName).collect(Collectors.joining("."));
                    throw new FhUseCaseException(String.format("%s.%s field can't have both %s and %s annotations.",
                            inputParamClass.getSimpleName(), UrlParam.class.getSimpleName(), UrlParamWrapper.class.getSimpleName()));
                }

                // make a copy
                List<Field> newFieldChain = new ArrayList<>(fieldChain);
                newFieldChain.add(field);

                String newNamePrefix = namePrefix + wrapperAnnotation.get().namePrefix();

                // add wrapper's field based URL parameter
                readParamMetadata(metadata, inputParamIndex, inputParamClass, field.getType(),
                        Optional.ofNullable(field.getAnnotation(UrlParam.class)), Optional.ofNullable(field.getAnnotation(UrlParamWrapper.class)),
                                takenPositionals, newFieldChain, newNamePrefix,
                                wrapperAnnotation.get().useNames() ? field.getName() : null);
            };
            org.springframework.util.ReflectionUtils.doWithFields(thisParamClass, processor, field ->
                            field.getDeclaringClass() != Object.class /* not declared by Object */
                                    && !field.isAnnotationPresent(UrlParamIgnored.class) /* no @UrlParamIgnored */
                                    && !Modifier.isFinal(field.getModifiers())
                                    && !Modifier.isStatic(field.getModifiers())
                                    && !Modifier.isTransient(field.getModifiers())
            );
        } else {
            ParamAnnotationMetadata paramMetadata = new ParamAnnotationMetadata();
            paramMetadata.inputParamIndex = inputParamIndex;
            paramMetadata.inputParamClass = inputParamClass;
            paramMetadata.thisParamClass = thisParamClass;
            paramMetadata.nestedFieldChain = fieldChain;

            if (paramAnnotation.isPresent()) {
                paramMetadata.positionalIndex = paramAnnotation.get().position();
                paramMetadata.nameInQuery = paramAnnotation.get().name();
            }

            if (paramMetadata.positionalIndex == -1 && StringUtils.isNullOrEmpty(paramMetadata.nameInQuery)) {
                if (defaultName != null) {
                    paramMetadata.nameInQuery = namePrefix + defaultName;
                } else {
                    paramMetadata.positionalIndex = reserveFirstNotTakenIndex(takenPositionals);
                }
            }

            paramMetadata.optional = paramAnnotation.isPresent() ? paramAnnotation.get().optional() : false;
            metadata.params.add(paramMetadata);
        }
    }

    protected Executable findStartMethod(Class<?> useCaseClazz) {
        if (ICustomUseCase.class.isAssignableFrom(useCaseClazz)){
            Optional<Constructor<?>> constructor = Arrays.stream(useCaseClazz.getConstructors()).filter(method -> !method.isSynthetic()).findFirst();
            if (constructor.isPresent())
            return constructor.get();
        }

        int nofParams;
        if (IUseCaseNoInput.class.isAssignableFrom(useCaseClazz)) {
            nofParams = 0;
        } else if (IUseCaseOneInput.class.isAssignableFrom(useCaseClazz)) {
            nofParams = 1;
        } else if (IUseCaseTwoInput.class.isAssignableFrom(useCaseClazz)) {
            nofParams = 2;
        } else {
            throw new FhUseCaseException("Unsupported use case input param count in class " + useCaseClazz.getName());
        }
        List<Method> startMethods = Arrays.stream(useCaseClazz.getMethods()).filter(method -> method.getName().equals("start") && method.getParameterTypes().length == nofParams).collect(Collectors.toList());

        if (!startMethods.isEmpty()) {
            Method firstMethod = startMethods.get(0);
            if (nofParams == 0 || startMethods.size() == 1) {
                return firstMethod;
            }
            startMethods = startMethods.stream().filter(method -> !method.getParameterTypes()[0].isAssignableFrom(Object.class)).collect(Collectors.toList());
            if (!startMethods.isEmpty()) {
                return startMethods.get(0);
            }

            return firstMethod;
        }

        throw new FhUseCaseException("Start method not found in class " + useCaseClazz.getName());
    }

    private int reserveFirstNotTakenIndex(Set<Integer> taken) {
        for (int index = 0; ; index++) {
            if (!taken.contains(index)) {
                taken.add(index);
                return index;
            }
        }
    }
}
