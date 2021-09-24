package pl.fhframework.binding;

import pl.fhframework.core.FhFormException;
import pl.fhframework.BindingResult;
import pl.fhframework.annotations.DesignerXMLProperty;
import pl.fhframework.annotations.RepeaterTraversable;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.model.forms.Component;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.designer.IDesignerPreviewProvider;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Adam Zareba on 16.03.2017. Binding class simulating fake binding for designer
 * purpose.
 */
@RepeaterTraversable
public class DesignerModelBinding<V> extends AdHocModelBinding<V> {

    private static Map<Class<? extends IDesignerPreviewProvider>, IDesignerPreviewProvider> PROVIDERS_CACHE = new ConcurrentHashMap<>();

    /**
     * Attribute xmlProperty.
     */
    private Field field;
    private XMLProperty xmlProperty;
    private DesignerXMLProperty designerXmlProperty;
    private FhConversionService conversionService;

    public DesignerModelBinding(Form form, Component owner, String bindingExpression, Field field, FhConversionService conversionService) {
        super(form, owner, bindingExpression);
        this.xmlProperty = field.getAnnotation(XMLProperty.class);
        this.designerXmlProperty = field.getAnnotation(DesignerXMLProperty.class);
        if (this.designerXmlProperty == null) {
            this.designerXmlProperty = DesignerXMLProperty.Defaults.getDefaults();
        }
        this.field = field;
        this.conversionService = conversionService;
    }

    public BindingResult<V> getBindingResult() {
        //FIXME Unused classes , shuldn't it be checked??
        List<Class> classes = Arrays.asList(designerXmlProperty.allowedTypes());

        String defaultValue = xmlProperty.defaultValue();
        if (defaultValue.isEmpty()) {
            defaultValue = null;
        }

        IDesignerPreviewProvider provider = getPreviewValueProvider(designerXmlProperty);
        Object previewValue = provider.getPreviewValue(owner, field, this, bindingExpression, getStaticValueText(), defaultValue);
        return new BindingResult(null, null, previewValue);
    }

    private IDesignerPreviewProvider getPreviewValueProvider(DesignerXMLProperty designerXmlProperty) {
        Class<? extends IDesignerPreviewProvider> providerClass = designerXmlProperty.previewValueProvider();
        if (!PROVIDERS_CACHE.containsKey(providerClass)) {
            try {
                PROVIDERS_CACHE.put(providerClass, providerClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new FhFormException("Cannot create preview value provider: " + providerClass.getName(), e);
            }
        }
        return PROVIDERS_CACHE.get(providerClass);
    }

    @Override
    public DesignerModelBinding<V> clone(Component newOwner) {
        return new DesignerModelBinding<>(getForm(), newOwner, bindingExpression, field, conversionService);
    }
}

