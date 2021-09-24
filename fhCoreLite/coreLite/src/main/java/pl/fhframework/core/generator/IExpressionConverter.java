package pl.fhframework.core.generator;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Interface of addtional expression converter for BindingParser
 */
public interface IExpressionConverter {
    String convertToFullNames(String expression);

    String convertToShortNames(String expression);
}
