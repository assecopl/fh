/***********************************************************
 * Opis: patrz niżej w komentarzach javadoc.
 ***********************************************************
 * Osoba odpowiedzialna: Kamil Pliszka
 * Data utworzenia: 2017-10-16 16:48
 ***********************************************************/

package pl.fhframework.compiler.core.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.fhframework.compiler.core.generator.ITypeProvider;
import pl.fhframework.compiler.core.generator.MethodDescriptor;
import pl.fhframework.core.i18n.MessageService;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessagesTypeProvider implements ITypeProvider {
    private static interface IMessageHintType {}

    public static final Class<?> MESSAGE_HINT_TYPE = IMessageHintType.class;
    public static final String MESSAGE_HINT_TYPE_NAME = "I18n...";
    public static final String MESSAGE_HINT_PREFIX = "$";

    @Autowired
    private MessageService messageService;


    @Override
    public Type getSupportedType() {
        return MESSAGE_HINT_TYPE;
    }

    @Override
    public List<MethodDescriptor> getMethods(Type ofType) {
        return new ArrayList<>();
    }

    @Override
    public String toTypeLiteral() {
        return null;
    }

}
