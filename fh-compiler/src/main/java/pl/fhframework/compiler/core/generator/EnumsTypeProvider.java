package pl.fhframework.compiler.core.generator;

import org.springframework.stereotype.Component;
import pl.fhframework.core.generator.IExpressionConverter;

/**
 * Created by pawel.ruta on 2017-06-23.
 */
@Component
public class EnumsTypeProvider extends EnumsTypeProviderBase implements IExpressionConverter {
    @Override
    public String convertToShortNames(String expression) {
        return genericExpressionConverter.convertSymbolNames(expression, ENUM_PREFIX, this::getShortEnumName, true);
    }

    @Override
    public String convertToFullNames(String expression) {
        return genericExpressionConverter.convertSymbolNames(expression, ENUM_PREFIX, this::getFullEnumName, true);
    }
}
