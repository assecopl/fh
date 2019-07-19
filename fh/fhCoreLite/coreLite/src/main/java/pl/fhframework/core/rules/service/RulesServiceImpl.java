package pl.fhframework.core.rules.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhAuthorizationException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.GeneratedDynamicClass;
import pl.fhframework.core.generator.GenericExpressionConverter;
import pl.fhframework.core.rules.meta.RuleMetadataRegistry;
import pl.fhframework.core.services.MethodPointer;
import pl.fhframework.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-06-02.
 */
@Service
public class RulesServiceImpl implements RulesService {
    @Autowired
    private ApplicationContext applicationContext;

    private static ThreadLocal<Map<String, MethodPointer>> ruleLookupCache = new ThreadLocal<>();

    @Override
    public String convertToFullNames(String expression) {
        return expression;
    }

    @Override
    public String convertToShortNames(String expression) {
        return expression;
    }

    @GeneratedDynamicClass("pl.fhframework.core.Context")
    public static abstract class Context {
    }

    @Override
    public <T> T runRule(String ruleName, Object... args) {
        MethodPointer ruleReference = getCachedRule(ruleName, args); // no overloading by type (arguments count must differ)

        Class<?>[] paramClasses = Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return null;
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
            String className = dynamicClassName.toFullClassName();
            Class<?> ruleClass = ReflectionUtils.tryGetClassForName(className);
            if (ruleClass == null) {
                ruleClass = ReflectionUtils.getClassForName(className);
            }

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
                            if (!ReflectionUtils.isAssignablFrom(foundParamClasses[i], paramClasses[i]) && paramClasses[i] != null) {
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
}
