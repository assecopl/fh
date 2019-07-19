package pl.fhframework.core.uc.meta;

import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.security.annotations.SystemFunction;
import pl.fhframework.core.uc.*;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.Action;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UseCaseMetadataReader implements IUseCaseMetadataReader {

    @Override
    public Optional<UseCaseInfo> buildUseCaseMetadata(String useCaseClassName) {
        Class<? extends IUseCase> clazz = (Class<? extends IUseCase>) ReflectionUtils.tryGetClassForName(useCaseClassName);
        if (clazz == null) {
            return Optional.empty();
        }
        return Optional.of(readMetadata(clazz, ModuleRegistry.getUseCaseSubsystem(useCaseClassName)));
    }

    @Override
    public UseCaseInfo buildUseCaseMetadata(Class<? extends IUseCase> useCaseClazz, Subsystem subsystem) {
        return readMetadata(useCaseClazz, subsystem);
    }

    private boolean isAvailableInMenu(Class<? extends IUseCase> clazz) {
        return true; //IUseCaseNoInput.class.isAssignableFrom(clazz);
    }

    //TODO:SSO Separate method/class for annotation loading?
    private UseCaseInfo readMetadata(Class<? extends IUseCase> clazz, Subsystem subsystem) {
        UseCaseInfo useCaseInfo = new UseCaseInfo();
        useCaseInfo.setSubsystem(subsystem);
        useCaseInfo.setClazz(clazz);
        useCaseInfo.setDynamic(clazz.getAnnotation(UseCase.class).modifiable());
        useCaseInfo.setId(ReflectionUtils.getClassName(clazz));
        useCaseInfo.setAvailableInMenu(isAvailableInMenu(clazz));

        UseCaseWithUrl urlInfo = clazz.getAnnotation(UseCaseWithUrl.class);
        if (urlInfo != null) {
            if (UseCaseWithUrl.DEFAULT_ALIAS.equals(urlInfo.alias())) {
                useCaseInfo.setUrlAlias(StringUtils.firstLetterToLower(clazz.getSimpleName()));
            } else {
                useCaseInfo.setUrlAlias(urlInfo.alias());
            }
        }

        Annotation[] useCaseClassAnnotations = clazz.getAnnotations();
        SystemFunction[] useCaseRequiredRoles = clazz.getAnnotationsByType(SystemFunction.class);

        //Method useCaseStartMethod = getUseCaseMethod(clazz, "start");

        UseCaseActionInfo useCaseStartActionInfo = new UseCaseActionInfo();
        useCaseInfo.setStart(useCaseStartActionInfo);
        useCaseStartActionInfo.setName("start");
        List<Executable> startMethods;
        if (ICustomUseCase.class.isAssignableFrom(clazz)) {
            startMethods = Arrays.stream(clazz.getConstructors()).filter(method -> !method.isSynthetic()).collect(Collectors.toList());
        }
        else {
            startMethods = Arrays.stream(clazz.getMethods()).filter(method -> !method.isSynthetic() && method.getName().equals("start")).collect(Collectors.toList());
        }
        if (startMethods.size() == 1) {
            useCaseStartActionInfo.setActionMethodHandler(startMethods.get(0));
        }
        else {
            throw new FhUseCaseException("Unknown start method for ".concat(clazz.getName()));
        }

        List<Type> typesList = new LinkedList<>();
        Class callback = getCallback(clazz, typesList);
        useCaseInfo.setCallbackClazz(callback.getCanonicalName());
        fillImplementedInterfacesInfo(useCaseInfo);

        Map<String, Type> resolvedTypes = resolveTypes(typesList);

        fillGenericParamForCallback(useCaseInfo, callback, resolvedTypes);

        fillParametersInfo(useCaseStartActionInfo, clazz, resolvedTypes);

        ReflectionUtils.forEachAnnotatedMethod(clazz, Action.class, (Method metoda, Action action) -> {
            String nazwaAkcji = action.value();
            if (Action.DEFAULT_VALUE.equals(nazwaAkcji)) {
                nazwaAkcji = metoda.getName();
            }
            UseCaseActionInfo eventCallbackInfo = new UseCaseActionInfo();//todo builder pattern
            eventCallbackInfo.setName(nazwaAkcji);
            eventCallbackInfo.setId(nazwaAkcji);
            eventCallbackInfo.setFormTypeId(action.form());
            eventCallbackInfo.setRemoteEnabled(action.remote());
            SystemFunction methodSystemFunction = metoda.getAnnotation(SystemFunction.class);
            if (methodSystemFunction != null) {
                //todo: add it to info
            }
            eventCallbackInfo.setActionMethodHandler(metoda);
            fillParametersInfo(eventCallbackInfo, clazz, resolvedTypes);
            useCaseInfo.addEventCallback(eventCallbackInfo);

            if (action.defaultRemote()) {
                if (!StringUtils.isNullOrEmpty(useCaseInfo.getDefaultRemoteEvent())) {
                    throw new FhUseCaseException("More than one default remote action defined for '" + clazz.getName() + "'");
                }
                useCaseInfo.setDefaultRemoteEvent(nazwaAkcji);
            }
        });

        //exits
        fillExitsInfo(useCaseInfo, clazz, callback, resolvedTypes);

        return useCaseInfo;
    }

    private void fillGenericParamForCallback(UseCaseInfo useCaseInfo, Class callback, Map<String, Type> resolvedTypes) {
        useCaseInfo.getCallbackGenericParam().clear();

        if (callback.getTypeParameters().length > 0) {
            for (TypeVariable typeVariable : callback.getTypeParameters()) {
                useCaseInfo.getCallbackGenericParam().add(ReflectionUtils.getRawClass(resolveType(typeVariable, resolvedTypes)));
            }
        }
    }

    private Map<String, Type> resolveTypes(List<Type> typesList) {
        Map<String, Type> resolved = new HashMap<>();

        typesList.forEach(type -> {
            Map<String, Type> resolvedCurrent = new HashMap<>();
            TypeVariable typeVariables[] = ((Class) ((ParameterizedType) type).getRawType()).getTypeParameters();
            Type types[] = ((ParameterizedType) type).getActualTypeArguments();
            for (int i = 0; i < typeVariables.length; i++) {
                TypeVariable typeVariable = typeVariables[i];
                Type typeRes = types[i];
                if (typeRes instanceof TypeVariable) {
                    typeRes = resolved.get(typeRes.getTypeName());
                }
                resolvedCurrent.put(typeVariable.getTypeName(), typeRes);
            }
            resolved.putAll(resolvedCurrent);
        });

        return resolved;
    }

    private void fillParametersInfo(UseCaseActionInfo actionInfo, Class<? extends IUseCase> clazz, Map<String, Type> resolvedTypes) {
        Executable actionMethod = actionInfo.getActionMethodHandler();
        Annotation[][] annotations = actionMethod.getParameterAnnotations();
        Type[] types = actionMethod.getGenericParameterTypes();
        if (types.length == 1) {
            Class rawClass = ReflectionUtils.getRawClass(resolveType(types[0], resolvedTypes));
            if (rawClass.getAnnotation(ParametersWrapper.class) != null) {
                actionInfo.setParametersClassWraper(rawClass.getCanonicalName());
                fillParametersInfoFromWrapper(actionInfo, rawClass, resolvedTypes);
                return;
            }
        }
        for (int i = 0; i < types.length; i++) {
            if (!IUseCaseOutputCallback.class.isAssignableFrom(ReflectionUtils.getRawClass(resolveType(types[i], resolvedTypes)))) {
                ParameterInfo parameterInfo = getParameterInfo(types[i], annotations[i], actionMethod, resolvedTypes, i + 1);
                actionInfo.getParameters().add(parameterInfo);
            }
        }
    }

    private ParameterInfo getParameterInfo(Type type, Annotation[] annotations, Executable actionMethod, Map<String, Type> resolvedTypes, int index) {
        ParameterInfo parameterInfo = new ParameterInfo();
        if (ParameterizedType.class.isInstance(type)) {
            Type rawType = ((ParameterizedType)type).getRawType();
            if (Class.class.isInstance(rawType) && Collection.class.isAssignableFrom((Class) rawType)) {
                parameterInfo.setMultiplicity(TypeMultiplicityEnum.Collection);
                parameterInfo.setType(resolveType(((ParameterizedType)type).getActualTypeArguments()[0], resolvedTypes).getTypeName());
            }
            else {
                parameterInfo.setType(resolveType(type, resolvedTypes).getTypeName());
            }
        }
        else {
            Type resolvedType = resolveType(type, resolvedTypes);
            parameterInfo.setType(ReflectionUtils.mapPrimitiveToWrapper(resolvedType).getTypeName());
            parameterInfo.setPrimitive(ReflectionUtils.isPrimitive(resolvedType));
        }
        pl.fhframework.core.uc.Parameter parameterAnnotation = getParameterAnnotation(annotations);
        if (parameterAnnotation != null) {
            parameterInfo.setName(parameterAnnotation.name());
        }
        else {
            parameterInfo.setName("param_".concat(Integer.toString(index)));
        }

        return parameterInfo;
    }

    private Type resolveType(Type type, Map<String, Type> resolvedTypes) {
        if (type instanceof TypeVariable) {
            return  resolvedTypes.get(((TypeVariable) type).getName());
        }
        return type;
    }

    private Parameter getParameterAnnotation(Annotation[] annotation) {
        return Arrays.stream(annotation).filter(Parameter.class::isInstance).map(Parameter.class::cast).findFirst().orElse(null);
    }

    private void fillParametersInfoFromWrapper(UseCaseActionInfo actionInfo, Class aWrapperClass, Map<String, Type> resolvedTypes) {
        Field[] fields = aWrapperClass.getDeclaredFields();
        for (int index = 0; index < fields.length; index++) {
            Field field = fields[index];
            ParameterInfo parameterInfo = getParameterInfo(field.getGenericType(), field.getAnnotations(), actionInfo.getActionMethodHandler(), resolvedTypes, index + 1);
            actionInfo.getParameters().add(parameterInfo);
        }
    }

    private void fillExitsInfo(UseCaseInfo useCaseInfo, Class<? extends IUseCase> clazz, Class<? extends IUseCaseOutputCallback> callback, Map<String, Type> resolvedTypes) {
        if (callback == null) {
            throw new FhUseCaseException("Unknown exit callback for ".concat(clazz.getName()));
        }

        Map<String, Method> exits;
        if (ICustomUseCase.class.isAssignableFrom(clazz)) {
            exits = Arrays.stream(useCaseInfo.getStart().getActionMethodHandler().getParameterTypes()).
                    filter(IUseCaseOutputCallback.class::isAssignableFrom).map(aClass -> Arrays.asList(aClass.getMethods())).
                    flatMap(List::stream).filter(method -> !Modifier.isStatic(method.getModifiers()) && !method.isBridge()).
                    collect(Collectors.toMap(Method::getName, Function.identity(),
                            (value1, value2) -> {
                                if (value1.equals(value2))
                                    return value1;
                                else
                                    throw new FhUseCaseException(String.format("Overloading callback method is not supported. Method '%s'  is oveloaded ", value1.getName()));
                            }));
        }
        else {
            exits = Arrays.stream(callback.getMethods()).
                    filter(method -> Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) && !method.isBridge()).
                    collect(Collectors.toMap(Method::getName, Function.identity(),
                    (value1, value2) -> {throw new FhUseCaseException(String.format("Overloading callback method is not supported. Method '%s'  is oveloaded ", value1.getName()));}));
        }
        exits.forEach((name, method) -> {
            UseCaseActionInfo useCaseExitInfo = new UseCaseActionInfo();
            useCaseExitInfo.setName(name);
            useCaseExitInfo.setId(name);
            useCaseExitInfo.setActionMethodHandler(method);
            fillParametersInfo(useCaseExitInfo, clazz, resolvedTypes);
            useCaseInfo.addExitInfo(useCaseExitInfo);
        });
    }

    private Class getCallback(Class<? extends IUseCase> clazz, List<Type> typesList) {
        if (!IUseCase.class.isAssignableFrom(clazz)) {
            return null;
        }

        Class currentClass = clazz;
        do {
            if (currentClass.getGenericSuperclass() != null) {
                if (currentClass.getGenericSuperclass() instanceof ParameterizedType) {
                    typesList.add(currentClass.getGenericSuperclass());
                }
            }
            Type[] types = currentClass.getGenericInterfaces();
            if (types.length > 0) {
                for (Type type : types) {
                    Class rawClassMain;
                    if (type instanceof ParameterizedType) {
                        typesList.add(type);
                        rawClassMain = (Class) ((ParameterizedType) type).getRawType();
                    }
                    else {
                        rawClassMain = (Class) type;
                    }

                    Class callback = getCallback(rawClassMain, typesList);
                    if (callback != null) {
                        return callback;
                    }
                    if (type instanceof ParameterizedType) {
                        typesList.remove(typesList.size() - 1);
                    }

                    if (type instanceof ParameterizedType) {
                        Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();

                        for (Type genericType : genericTypes) {
                            Class rawClass = null;
                            boolean withGenerics = genericType instanceof ParameterizedType;
                            if (withGenerics) {
                                if (((ParameterizedType) genericType).getRawType() instanceof Class) {
                                    rawClass = (Class) ((ParameterizedType) genericType).getRawType();
                                }
                            }
                            else if (genericType instanceof Class){
                                rawClass = (Class) genericType;
                            }
                            if (rawClass != null) {
                                if (withGenerics) {
                                    typesList.add(genericType);
                                }
                                if (IUseCaseOutputCallback.class.isAssignableFrom(rawClass)) {
                                    return rawClass;
                                }
                                else {
                                    callback = getCallback(rawClass, typesList);
                                    if (callback != null) {
                                        return callback;
                                    }
                                    if (withGenerics) {
                                        typesList.remove(typesList.size() - 1);
                                    }
                                }
                            }
                        }
                    }
                    else if (type instanceof Class) {
                        callback = getCallback((Class<? extends IUseCase>) type, typesList);
                        if (callback != null) {
                            return callback;
                        }
                    }
                }
            }
            currentClass = currentClass.getSuperclass();
        } while (currentClass != null && !currentClass.isAssignableFrom(Object.class));

        return null;
    }

    private static Method getUseCaseMethod(Class<?> useCaseClazz, String methodName, Class<?>... methodParamTypes) throws NoSuchMethodException {
        if (useCaseClazz == null) {
            throw new NoSuchMethodException();
        }
        try {
            return useCaseClazz.getDeclaredMethod(methodName, methodParamTypes);
        } catch (NoSuchMethodException exc) {
            return getUseCaseMethod(useCaseClazz.getSuperclass(), methodName, methodParamTypes);
        }
    }

    private void fillImplementedInterfacesInfo(UseCaseInfo useCaseInfo) {
        for (Class interf : useCaseInfo.getClazz().getInterfaces()) {
            // interface must extend IUseCase and one of IUseCaseXxxInput
            if ((IUseCaseNoInput.class.isAssignableFrom(interf) && interf != IUseCaseNoInput.class)
                    || (IUseCaseOneInput.class.isAssignableFrom(interf) && interf != IUseCaseOneInput.class)
                    || (IUseCaseTwoInput.class.isAssignableFrom(interf) && interf != IUseCaseTwoInput.class)) {
                useCaseInfo.getImplementedInterfaces().add(interf);
            }
        }
    }
}