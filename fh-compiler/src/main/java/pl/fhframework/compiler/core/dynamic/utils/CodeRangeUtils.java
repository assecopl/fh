package pl.fhframework.compiler.core.dynamic.utils;

import pl.fhframework.ReflectionUtils;
import pl.fhframework.compiler.core.dynamic.CodeRangeLogger;
import pl.fhframework.compiler.core.dynamic.RuntimeErrorDescription;
import pl.fhframework.compiler.core.dynamic.dependency.CodeRangeJavaGenerator;
import pl.fhframework.core.generator.CodeRange;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.PresentationStyleEnum;
import pl.fhframework.validation.IValidationResults;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2018-02-27.
 */
public class CodeRangeUtils {

    public static String generateCodeDescription(Class<?> clazz, int lineNumber, String type, boolean inner) {
        List<CodeRange> codeRangeList = CodeRangeJavaGenerator.getCodeRanges(clazz);

        StringBuilder description = new StringBuilder();
        if (!inner) {
            description.append(String.format("%s '%s'", type, ReflectionUtils.getSimpleClassName(clazz)));
        }

        if (!codeRangeList.isEmpty()) {
            if (!inner) {
                description.append(CodeRange.DELIMITER);
            }

            codeRangeList = codeRangeList.stream().filter(codeRange -> codeRange.contains(lineNumber)).collect(Collectors.toList());

            codeRangeList.sort(Comparator.comparingInt(range -> (range.getStart() - lineNumber)));

            description.append(codeRangeList.stream().map(CodeRange::getName).collect(Collectors.joining(CodeRange.DELIMITER)));
        }

        return description.toString();
    }

    public static Optional<RuntimeErrorDescription> generateRuntimeErrorDescription(List<CodeRangeLogger.ElementDesc> identifiedElements, Throwable rootCause) {
        List<CodeRangeLogger.ElementDesc> rootClassElements = new ArrayList<>();

        Class<?> rootClass = identifiedElements.get(0).getClazz();

        for (CodeRangeLogger.ElementDesc element : identifiedElements) {
            if (element.getClazz() == rootClass) {
                rootClassElements.add(element);
            }
            else {
                break;
            }
        }

        Collections.reverse(rootClassElements);

        List<CodeRange> codeRangeList = new ArrayList<>();

        for (CodeRangeLogger.ElementDesc element : rootClassElements) {
            List<CodeRange> codeRangeElementList = CodeRangeJavaGenerator.getCodeRanges(element.getClazz());

            codeRangeElementList = codeRangeElementList.stream().filter(codeRange -> codeRange.contains(element.getElement().getLineNumber())).collect(Collectors.toList());

            codeRangeElementList.sort(Comparator.comparingInt(range -> (range.getStart() - element.getElement().getLineNumber())));

            codeRangeList.addAll(codeRangeElementList);
        }

        /*boolean additionalLevel = !"rule".equals(rootClassElements.get(0).getTypeStr());
        if (!additionalLevel && !codeRangeList.isEmpty() || codeRangeList.size() > 1) {
            if (additionalLevel) {
                codeRangeList = codeRangeList.subList(1, codeRangeList.size());
            }*/

        if (!codeRangeList.isEmpty()) {
            StringBuilder path = new StringBuilder();

            Iterator<CodeRange> codeRangeIterator = codeRangeList.iterator();
            CodeRange prevCodeRange = codeRangeIterator.next();
            while (codeRangeIterator.hasNext()) {
                CodeRange currCodeRange = codeRangeIterator.next();
                if (!currCodeRange.getPath().startsWith(prevCodeRange.getPath())) {
                    path.append(prevCodeRange.getPath()).append(CodeRange.DELIMITER);
                }
                prevCodeRange = currCodeRange;
            }
            path.append(prevCodeRange.getPath());

            String pathDescription = codeRangeList.stream().map(CodeRange::getName).collect(Collectors.joining(CodeRange.DELIMITER));

            return Optional.of(new RuntimeErrorDescription(path.toString(), pathDescription, exceptionDescription(rootCause)));
        }

        return Optional.empty();
    }

    public static String exceptionDescription(Throwable throwable) {
        if (throwable instanceof NullPointerException) {
            return "Attempt to access data of null object";
        }
        if (throwable instanceof IndexOutOfBoundsException) {
            return "Attempt to access an object (Collection, Array, String) with index out of range";
        }
        if (throwable instanceof ClassCastException) {
            return "Attempt to cast an object to incompatible type";
        }
        if (throwable instanceof ConcurrentModificationException) {
            return "Attempt to modify collection while interating over it";
        }
        return String.format("%s (%s)", throwable.getClass().getSimpleName(), throwable.getMessage());
    }

    public static void showRuntimeErrors(Object parent, List<RuntimeErrorDescription> errorDescriptions, IValidationResults validationResults) {
        showRuntimeErrors(parent, errorDescriptions, validationResults, null);
    }

    public static void showRuntimeErrors(Object parent, List<RuntimeErrorDescription> errorDescriptions, IValidationResults validationResults, String trimLevelPath) {
        if (errorDescriptions == null) {
            return;
        }
        for (RuntimeErrorDescription errorDescription : errorDescriptions) {
            if (StringUtils.isNullOrEmpty(trimLevelPath) || errorDescription.getPath().startsWith(trimLevelPath + CodeRange.DELIMITER)) {
                validationResults.addCustomMessage(parent, "Runtime error",
                        String.format("%s : %s", trimPathDesc(errorDescription.getPathDescription(), trimLevelPath), errorDescription.getMessage()), PresentationStyleEnum.WARNING);
            }
        }
    }

    private static String trimPathDesc(String pathDescription, String trimLevelPath) {
        if (StringUtils.isNullOrEmpty(trimLevelPath)) {
            return pathDescription;
        }

        int pathElementsCount = StringUtils.countSequence(trimLevelPath, CodeRange.DELIMITER);
        return Arrays.stream(pathDescription.split(CodeRange.DELIMITER)).skip(pathElementsCount + 1).collect(Collectors.joining(CodeRange.DELIMITER));
    }
}
