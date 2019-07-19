package pl.fhframework.core.uc.instance;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import pl.fhframework.core.FhException;
import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.uc.*;
import pl.fhframework.core.uc.meta.UseCaseInfo;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by pawel.ruta on 2018-08-03.
 */
@Component
public class UseCaseInitializer {
    @Autowired
    private ApplicationContext context;

    @Autowired
    @Qualifier(NullUseCaseInputFactory.NAME)
    private NullUseCaseInputFactory nullFactory;

    @Autowired
    @Qualifier(ConstructorUseCaseInputFactory.NAME)
    private ConstructorUseCaseInputFactory constructorFactory;

    public Object[] createParameters(Executable executable, String inputFactory) {
        FactoryPair factoryPair = FactoryPair.of(inputFactory, context);

        return createParameters(executable, factoryPair.getInputFactoryObj(), factoryPair.getOutputFactoryObj());
    }

    public Object[] createParameters(Executable executable, IUseCaseInputFactory inputFactory, IUseCaseOutputFactory outputFactory) {
        IUseCaseInputFactory inputFactoryObj = nullFactory;
        IUseCaseOutputFactory outputFactoryObj = nullFactory;

        if (inputFactory != null) {
            inputFactoryObj = inputFactory;
        }
        if (outputFactory != null) {
            outputFactoryObj = outputFactory;
        }

        Class<?>[] parameters = executable.getParameterTypes();
        Object[] inputs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (IUseCaseOutputCallback.class.isAssignableFrom(parameters[i])) {
                inputs[i] = outputFactoryObj.createCallback((Class<? extends IUseCaseOutputCallback>) parameters[i]);
            }
            else {
                if (inputFactory == null && parameters[i].isAnnotationPresent(ParametersWrapper.class)) {
                    inputs[i] = constructorFactory.getInstance(parameters[i]);
                }
                else {
                    inputs[i] = inputFactoryObj.getInstance(parameters[i]);
                }
            }
        }
        return inputs;
    }

    public Object[] createParameters(Executable executable, Object[] inputParams) {
        Class<?>[] parameters = executable.getParameterTypes();
        Object[] args = new Object[parameters.length];
        for (int i = 0, inputIdx = 0; i < parameters.length; i++) {
            if (IUseCaseOutputCallback.class.isAssignableFrom(parameters[i])) {
                args[i] = createCallback((Class<? extends IUseCaseOutputCallback>) parameters[i]);
            }
            else {
                args[i] = inputParams[inputIdx];
                inputIdx++;
            }
        }
        return args;
    }

    public Object[] createInputParameters(UseCaseInfo useCaseInfo, IUseCaseInputFactory inputFactory, IUseCaseOutputFactory outputFactory) {
        Class<?> useCaseClass = getUseCaseClass(useCaseInfo);

        if (ICustomUseCase.class.isAssignableFrom(useCaseClass)) {
            throw new FhException("createInputParameters is not for ICustomUseCase, use createUseCase instead");
        }
        Optional<Method> start = Arrays.stream(useCaseClass.getMethods()).filter(method -> !method.isSynthetic()).filter(method -> Objects.equals(method.getName(), "start")).findFirst();
        if (!start.isPresent()) {
            throw new IllegalArgumentException("No start method in use case " + useCaseInfo.getId());
        }
        if (useCaseInfo.getStart() != null && useCaseInfo.getStart().getParametersClassWraper() != null && (inputFactory == null || inputFactory == nullFactory)) {
            inputFactory = constructorFactory;
        }
        return createParameters(start.get(), inputFactory, outputFactory);
    }

    public Object[] createInputParameters(UseCaseInfo useCaseInfo, String inputFactory) {
        FactoryPair factoryPair = FactoryPair.of(inputFactory, context);

        return createInputParameters(useCaseInfo, factoryPair.getInputFactoryObj(), factoryPair.getOutputFactoryObj());
    }

    public IUseCaseOutputCallback createCallback(UseCaseInfo useCaseInfo) {
        Class<?> useCaseClass = getUseCaseClass(useCaseInfo);

        Class<? extends IUseCaseOutputCallback> callbackClass = IUseCaseNoCallback.class;
        Type superType = useCaseClass.getGenericInterfaces()[0];
        if (superType instanceof ParameterizedType) {
            ParameterizedType superParameterizedType = (ParameterizedType) useCaseClass.getGenericInterfaces()[0];
            Class interfaceType = (Class) superParameterizedType.getRawType();

            if (IUseCaseNoInput.class.isAssignableFrom(interfaceType)) {
                callbackClass = (Class<? extends IUseCaseOutputCallback>) superParameterizedType.getActualTypeArguments()[0];
            } else {
                callbackClass = (Class<? extends IUseCaseOutputCallback>) superParameterizedType.getActualTypeArguments()[1];
            }
        }

        return createCallback(callbackClass);
    }

    public <T extends IUseCaseOutputCallback> T createCallback(Class<? extends T> clazz) {
        return (T) nullFactory.createCallback(clazz);
    }

    public ICustomUseCase createUseCase(String fullyQualifiedName, IUseCaseInputFactory inputFactory, IUseCaseOutputFactory outputFactory) {
        Optional<UseCaseInfo> useCaseInfo = UseCaseMetadataRegistry.INSTANCE.get(fullyQualifiedName);
        if (useCaseInfo.isPresent()) {
            return createUseCase(useCaseInfo.get(), inputFactory, outputFactory);
        }
        throw new IllegalArgumentException(String.format("%s is unkown UseCase type", fullyQualifiedName));
    }

    public ICustomUseCase createUseCase(String fullyQualifiedName, String inputFactory) {
        FactoryPair factoryPair = FactoryPair.of(inputFactory, context);

        return createUseCase(fullyQualifiedName, factoryPair.getInputFactoryObj(), factoryPair.getOutputFactoryObj());
    }

    public ICustomUseCase createUseCase(UseCaseInfo useCaseInfo, IUseCaseInputFactory inputFactory, IUseCaseOutputFactory outputFactory) {
        if (ICustomUseCase.class.isAssignableFrom(useCaseInfo.getClazz())) {
            Class<?> iUseCaseClass = useCaseInfo.getClazz();
            Constructor<?>[] constructors = iUseCaseClass.getConstructors();
            if (constructors.length > 0) {
                Constructor<?> constructor = constructors[0];
                try {
                    return (ICustomUseCase) constructor.newInstance(createParameters(constructor, inputFactory, outputFactory));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new FhUseCaseException(iUseCaseClass.getName() + " initialization error", e);
                }
            }
            else {
                throw new IllegalArgumentException(String.format("%s can't be created", iUseCaseClass.getName()));
            }
        }
        else {
            throw new IllegalArgumentException(String.format("%s is not ICustomUseCase type", useCaseInfo.getClazz().getName()));
        }
    }

    public ICustomUseCase createUseCase(UseCaseInfo useCaseInfo, String inputFactory) {
        FactoryPair factoryPair = FactoryPair.of(inputFactory, context);

        return createUseCase(useCaseInfo, factoryPair.getInputFactoryObj(), factoryPair.getOutputFactoryObj());
    }

    protected Class<?> getUseCaseClass(UseCaseInfo useCaseInfo) {
        Class<?> useCaseClass = useCaseInfo.getClazz();
        if (useCaseClass == null) {
            throw new IllegalArgumentException("No class for use case " + useCaseInfo.getId());
        }

        return useCaseClass;
    }

    private static class FactoryPair {
        @Getter
        private IUseCaseInputFactory inputFactoryObj = null;
        @Getter
        private IUseCaseOutputFactory outputFactoryObj = null;

        private FactoryPair(IUseCaseInputFactory inputFactoryObj, IUseCaseOutputFactory outputFactoryObj) {
            this.inputFactoryObj = inputFactoryObj;
            this.outputFactoryObj = outputFactoryObj;
        }

        public static FactoryPair of(String inputFactory, ApplicationContext context) {
            IUseCaseInputFactory inputFactoryObj = null;
            IUseCaseOutputFactory outputFactoryObj = null;

            if (!StringUtils.isNullOrEmpty(inputFactory)) {
                IUseCaseParametersFactory parametersFactory = (IUseCaseParametersFactory) context.getBean(inputFactory);
                inputFactoryObj = parametersFactory;
                outputFactoryObj = parametersFactory;
            }

            return new FactoryPair(inputFactoryObj, outputFactoryObj);
        }
    }
}
