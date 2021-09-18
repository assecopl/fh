package pl.fhframework.core.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.util.ClassUtils;
import pl.fhframework.format.AutoRegisteredConverter;
import pl.fhframework.format.FhConverter;

/**
 * I18n enum converter
 */
@FhConverter
public class EnumI18nConverter extends AutoRegisteredConverter<Enum<?>, String> implements ConditionalConverter {

    protected EnumI18nConverter() {
    }

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        for (Class<?> interfaceType : ClassUtils.getAllInterfacesForClass(sourceType.getType())) {
            if (this.conversionService.canConvert(TypeDescriptor.valueOf(interfaceType), targetType)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String convert(Enum<?> anEnum) {
        return messageService.getAllBundles().getEnumMessage(anEnum);
    }
}
