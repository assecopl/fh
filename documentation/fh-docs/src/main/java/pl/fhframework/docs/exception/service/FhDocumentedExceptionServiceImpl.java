package pl.fhframework.docs.exception.service;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.documented.DocumentedClass;
import pl.fhframework.core.documented.DocumentedConstructor;
import pl.fhframework.core.documented.DocumentedParameter;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.docs.exception.model.DescribedClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by k.czajkowski on 28.02.2017.
 */
@Service
public class FhDocumentedExceptionServiceImpl implements FhDocumentedExceptionService {

    @Override
    public Set<Class> findSubclasses(Class clazz, String... packageName) {
        Set<Class> subclasses = new LinkedHashSet<>();
        subclasses.add(clazz);
        for (String singlePackageName : packageName) {
            Set<BeanDefinition> beanDefinitions = getBeanDefinitions(singlePackageName);
            for (BeanDefinition beanDefinition : beanDefinitions) {
                try {
                    subclasses.add(getClass(beanDefinition));
                } catch (ClassNotFoundException e) {
                    FhLogger.error("Could not load class {} ", beanDefinition.getBeanClassName());
                }
            }
        }
        return subclasses;
    }

    private Class<?> getClass(BeanDefinition def) throws ClassNotFoundException {
        return FhCL.classLoader.loadClass(def.getBeanClassName());
    }

    private Set<BeanDefinition> getBeanDefinitions(String packageName) {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(FhException.class));
        return provider.findCandidateComponents(packageName);
    }

    @Override
    public Map<Class, String> getDescription(Collection<?> fhExceptions) {
        Map<Class, String> descriptionMap = new LinkedHashMap<>(fhExceptions.size());
        for (Object obj : fhExceptions) {
            Class clazz = (Class) obj;
            String description = getDescription(clazz);
            descriptionMap.put(clazz, description);
        }
        return descriptionMap;
    }

    @Override
    public String getDescription(Class clazz) {
        DocumentedClass[] declaredAnnotations = (DocumentedClass[]) clazz.getDeclaredAnnotationsByType(DocumentedClass.class);
        for (DocumentedClass annotation : declaredAnnotations) {
            return annotation.description();
        }
        FhLogger.debug(this.getClass(), logger -> logger.log("Did not find '{}' annotation in class '{}'", DocumentedClass.class.getSimpleName(), clazz.getName()));
        return "";
    }

    @Override
    public Set<DescribedClass> createDescribedExceptions(Set<Class> exceptions) {
        Map<Class, String> descriptions = getDescription(exceptions);
        Set<DescribedClass> describedExceptions = new LinkedHashSet<>();
        for (Map.Entry<Class, String> entry : descriptions.entrySet()) {
            Class documentedExceptionClass = entry.getKey();
            String clazzSimpleName = documentedExceptionClass.getSimpleName();
            String packageName = documentedExceptionClass.getPackage().toString();
            String exceptionDescription = entry.getValue();
            DescribedClass describedException = new DescribedClass(clazzSimpleName, packageName, exceptionDescription);
            describedException.setConstructors(findDescribedConstructors(documentedExceptionClass));
            describedExceptions.add(describedException);
        }
        return describedExceptions;
    }

    private List<DescribedClass.DescribedConstructor> findDescribedConstructors(Class clazz) {
        Constructor[] declaredConstructors = clazz.getDeclaredConstructors();
        List<DescribedClass.DescribedConstructor> describedConstructors = new LinkedList<>();
        for (Constructor constructor : declaredConstructors) {
            int modifiers = constructor.getModifiers();
            if (modifiers == 1) { //we want to document only public constructors
                DocumentedConstructor documentedConstructor = constructor.getDeclaredAnnotation(DocumentedConstructor.class);
                try {
                    DescribedClass.DescribedConstructor describedConstructor = createDescribedConstructor(clazz, modifiers, documentedConstructor);
                    describedConstructors.add(describedConstructor);
                } catch (NullPointerException e) {
                    //only log missing annotation,
                    FhLogger.debug(this.getClass(), logger -> logger.log("Did not find documentation for constructor {}", constructor));
                }
            }
        }
        return describedConstructors;
    }

    private DescribedClass.DescribedConstructor createDescribedConstructor(Class clazz, int modifiers, DocumentedConstructor documentedConstructor) {
        String description = documentedConstructor.description();
        DocumentedParameter[] parameters = documentedConstructor.parameters();
        List<DescribedClass.DescribedParameter> describedParameters = createDescribedParameters(parameters);
        String declaration = createConstructorDeclaration(clazz, modifiers, describedParameters);
        return createDescribedConstructor(description, describedParameters, declaration);
    }

    private DescribedClass.DescribedConstructor createDescribedConstructor(String description, List<DescribedClass.DescribedParameter> describedParameters, String declaration) {
        DescribedClass.DescribedConstructor describedConstructor = new DescribedClass.DescribedConstructor(declaration, description);
        describedConstructor.setParameters(describedParameters);
        return describedConstructor;
    }

    private List<DescribedClass.DescribedParameter> createDescribedParameters(DocumentedParameter[] params) {
        List<DescribedClass.DescribedParameter> describedParameters = new LinkedList<>();
        for (DocumentedParameter parameter : params) {
            describedParameters.add(new DescribedClass.DescribedParameter(parameter.className(), parameter.parameterName(), parameter.description()));
        }
        return describedParameters;
    }

    private String createConstructorDeclaration(Class clazz, int modifiers, List<DescribedClass.DescribedParameter> parameters) {
        StringBuilder sb = new StringBuilder(Modifier.toString(modifiers));
        sb.append(" ");
        sb.append(clazz.getSimpleName());
        sb.append("(");

        for(Iterator<DescribedClass.DescribedParameter> iterator = parameters.iterator(); iterator.hasNext();) {
            DescribedClass.DescribedParameter parameter = iterator.next();
            sb.append(parameter.getClassName());
            sb.append(" ");
            sb.append(parameter.getParameterName());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
