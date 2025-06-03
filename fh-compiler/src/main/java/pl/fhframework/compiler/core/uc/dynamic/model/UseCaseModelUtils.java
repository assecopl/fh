package pl.fhframework.compiler.core.uc.dynamic.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.dynamic.DynamicClassRepository;
import pl.fhframework.compiler.core.model.generator.DynamicModelClassJavaGenerator;
import pl.fhframework.compiler.core.uc.dynamic.model.element.attribute.ParameterDefinition;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.ActivityTypeEnum;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.CallFunction;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.rules.dynamic.model.dataaccess.From;
import pl.fhframework.core.rules.dynamic.model.dataaccess.Limit;
import pl.fhframework.core.rules.dynamic.model.dataaccess.Offset;
import pl.fhframework.core.uc.dynamic.model.element.attribute.TypeMultiplicityEnum;
import pl.fhframework.model.forms.PageModel;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Use case model utility methods.
 */
@Service
public class UseCaseModelUtils {
    @Autowired
    protected DynamicClassRepository dynamicClassRepository;

    public Type getType(String typeString, boolean isCollection, boolean isPageable) {
        if (isPageable) {
            return ReflectionUtils.createParametrizedType(PageModel.class, getType(typeString, null));
        } else {
            return getType(typeString, isCollection ? List.class : null);
        }
    }

    public Type getType(String typeString, Class<? extends Collection> collectionClass) {
        DynamicClassName typeClassName = DynamicClassName.forClassName(typeString);
        Type type;
        if (dynamicClassRepository.isRegisteredDynamicClass(typeClassName)) {
            try {
                type = dynamicClassRepository.getOrCompileDynamicClass(typeClassName);
            } catch (Throwable e) {
                FhLogger.errorSuppressed("Error while resolving dynamic class type " + typeString, e);
                throw new FhUseCaseException(typeClassName.getBaseClassName() + " has errors. Please repair it.");
            }
        } else {
            try {
                Type simpleType = DynamicModelClassJavaGenerator.TYPE_MAPPER.get(typeString);
                if (simpleType != null) {
                    typeString = simpleType.getTypeName();
                }
                type = FhCL.classLoader.loadClass(typeString);
            } catch(Exception e) {
                throw new FhUseCaseException("Invalid type: " + typeString, e);
            }
        }

        if (collectionClass != null) {
            type = ReflectionUtils.createCollectionType(collectionClass, type);
        }
        return type;
    }

    public Type getType(ParameterDefinition parameter) {
        return getType(parameter.getType(), parameter.isCollection(), parameter.isPageable());
    }

    public Type getType(VariableType parameter) {
        if (parameter == null) {
            return null;
        }

        if (parameter.getResolvedType() != null) {
            return parameter.getResolvedType();
        }

        return getType(parameter.getType(), parameter.isCollection(), parameter.isPageable());
    }

    public static String getDataReadType(CallFunction dataRead) {
        if (dataRead.getActivityType() == ActivityTypeEnum.DataRead) {
            if (dataRead.getRule() == null) { // old version
                return dataRead.getParameters().get(0).getValue();
            }
            Optional<From> from = dataRead.getRule().getRuleDefinition().getStatements().stream().filter(From.class::isInstance).map(From.class::cast).findAny();
            if (from.isPresent()) {
                return from.get().getType();
            }
        }

        return null;
    }

    public static TypeMultiplicityEnum getDataReadMultiplicity(CallFunction dataRead) {
        if (dataRead.getActivityType() == ActivityTypeEnum.DataRead) {
            if (dataRead.getRule() == null) { // old version
                return TypeMultiplicityEnum.Collection;
            }
            Optional<From> from = dataRead.getRule().getRuleDefinition().getStatements().stream().filter(From.class::isInstance).map(From.class::cast).findAny();
            if (from.isPresent()) {
                return Boolean.TRUE.equals(from.get().getPageable()) ? TypeMultiplicityEnum.MultiplePageable : TypeMultiplicityEnum.Collection;
            }
        }

        return null;
    }

    public static void setDataReadMultiplicity(CallFunction dataRead, TypeMultiplicityEnum multi) {
        if (dataRead.getActivityType() == ActivityTypeEnum.DataRead) {
            if (dataRead.getRule() != null) {
                Optional<From> from = dataRead.getRule().getRuleDefinition().getStatements().stream().filter(From.class::isInstance).map(From.class::cast).findAny();
                if (from.isPresent()) {
                    if (multi == TypeMultiplicityEnum.MultiplePageable) {
                        from.get().setPageable(true);
                        from.get().getStatements().removeIf(Offset.class::isInstance);
                        from.get().getStatements().removeIf(Limit.class::isInstance);
                    } else {
                        from.get().setPageable(false);
                    }
                }
            }
        }
    }

    public static boolean isDataReadPageable(CallFunction dataRead) {
        if (dataRead.getActivityType() == ActivityTypeEnum.DataRead && dataRead.getRule() != null) {
            From from = ((From) dataRead.getRule().getRuleDefinition().getStatements().get(0));
            return from.getPageable() != null && from.getPageable();
        }
        return false;
    }

    public static void setDataReadType(CallFunction dataRead, String type) {
        if (dataRead.getRule() != null) {
            ((From) dataRead.getRule().getRuleDefinition().getStatements().get(0)).setType(type);
        }
    }
}
