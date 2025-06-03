package pl.fhframework.compiler.core.dynamic;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import pl.fhframework.FormsHandler;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.dynamic.utils.CodeRangeUtils;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.CodeRange;
import pl.fhframework.core.logging.ICodeRangeLogger;
import pl.fhframework.core.rules.meta.RuleMetadataRegistry;
import pl.fhframework.core.services.meta.ServiceMetadataRegistry;
import pl.fhframework.core.uc.meta.UseCaseMetadataRegistry;
import pl.fhframework.core.util.DebugUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2018-03-01.
 */
@Service
@Primary
public class CodeRangeLogger implements ICodeRangeLogger {
    @Autowired
    DynamicClassRepository repository;

    public Optional<String> resolveCodeRangeMessage(Throwable exception) {
        Throwable rootCause = DebugUtils.getRootCause(exception);

        List<ElementDesc> identifiedElements = new ArrayList<>();
        Set<String> uniqueElements = new HashSet<>();
        for (StackTraceElement element : rootCause.getStackTrace()) {
            if (element.getClassName().equals(FormsHandler.class.getName())) {
                break;
            }
            // Proxy
            if (element.getClassName().contains("$$")) {
                continue;
            }
            String outerClassName = DynamicClassName.getOuterClassName(element.getClassName());
            if (UseCaseMetadataRegistry.INSTANCE.get(outerClassName).isPresent()){
                if (!uniqueElements.contains(element.getClassName())) {
                    identifiedElements.add(new ElementDesc(element, "usecase"));
                    if (outerClassName.equals(element.getClassName())) {
                        break;
                    }
                    else {
                        uniqueElements.add(element.getClassName());
                    }
                }
            }
            else if (RuleMetadataRegistry.INSTANCE.getSubsystem(outerClassName) != null) {
                if (!uniqueElements.contains(element.getClassName())) {
                    identifiedElements.add(new ElementDesc(element, "rule"));
                    uniqueElements.add(element.getClassName());
                }
            }
            else if (ServiceMetadataRegistry.INSTANCE.getSubsystem(outerClassName) != null) {
                if (!uniqueElements.contains(element.getClassName())) {
                    identifiedElements.add(new ElementDesc(element, "service"));
                    uniqueElements.add(element.getClassName());
                }
            }
        }

        Set<DynamicClassName> dcnSet = new HashSet<>();
        if (!identifiedElements.isEmpty()) {
            for (int i = 0; i < identifiedElements.size(); i++) {
                ElementDesc dynamicElement = identifiedElements.get(i);
                DynamicClassName className = DynamicClassName.forClassName(ReflectionUtils.getClassName(dynamicElement.getClazz()));
                if (dcnSet.add(className)) {
                    if (repository.isRegisteredDynamicClass(className)) {
                        Optional<RuntimeErrorDescription> errorDescription = CodeRangeUtils.generateRuntimeErrorDescription(identifiedElements.subList(i, identifiedElements.size()), rootCause);

                        if (errorDescription.isPresent()) {
                            DynamicClassMetadata metadata = repository.getMetadata(className);
                            if (!metadata.getRuntimeErrors().contains(errorDescription.get())) {
                                metadata.getRuntimeErrors().add(errorDescription.get());
                            }
                        }
                    }
                }
            }

            StringBuilder message = new StringBuilder(CodeRangeUtils.exceptionDescription(rootCause));
            ElementDesc mainElement = identifiedElements.get(0);
            message.append(String.format(" in %s '%s', path to error: ", mainElement.getType(), ReflectionUtils.getSimpleClassName(mainElement.getClazz())));

            Collections.reverse(identifiedElements);
            message.append(identifiedElements.stream().map(desc -> CodeRangeUtils.generateCodeDescription(desc.getClazz(), desc.getElement().getLineNumber(), desc.getType(), desc.inner)).collect(Collectors.joining(CodeRange.DELIMITER)));

            return Optional.of(message.toString());
        }

        return Optional.empty();
    }

    @Data
    public static class ElementDesc {
        private StackTraceElement element;
        private Class<?> clazz;
        private String type;
        private boolean inner;

        public ElementDesc(StackTraceElement element, String type) {
            this.element = element;
            this.type = type;
            String outerClassName = DynamicClassName.getOuterClassName(element.getClassName());
            clazz = ReflectionUtils.tryGetClassForName(outerClassName);
            inner = !outerClassName.equals(element.getClassName());
        }
    }
}
