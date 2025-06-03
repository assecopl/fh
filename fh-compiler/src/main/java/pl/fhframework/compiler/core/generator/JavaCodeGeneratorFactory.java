package pl.fhframework.compiler.core.generator;

import pl.fhframework.ReflectionUtils;
import pl.fhframework.modules.services.ServiceTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pawel.ruta on 2018-04-09.
 */
public class JavaCodeGeneratorFactory {
    private static Map<ServiceTypeEnum, Class<? extends AbstractJavaCodeGenerator>> generators = new HashMap<>();

    public static void registerCodeGenerator(ServiceTypeEnum typeId, Class<? extends AbstractJavaCodeGenerator> codeGeneratorClass) {
        generators.put(typeId, codeGeneratorClass);
    }

    public static Class<? extends AbstractJavaCodeGenerator> getCodeGeneratorClass(ServiceTypeEnum typeId) {
        return generators.get(typeId);
    }

    public static AbstractJavaCodeGenerator getCodeGenerator(ServiceTypeEnum typeId, Object... args) {
        return ReflectionUtils.newInstance(generators.get(typeId), args);
    }
}
