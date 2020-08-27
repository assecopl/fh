package pl.fhframework.tools.loading;

import pl.fhframework.annotations.*;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.designer.IDesignerAttributeSupport;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.binding.ActionBinding;
import pl.fhframework.binding.IndexedModelBinding;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.attribute.IComponentAttributeTypeConverter;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static pl.fhframework.model.forms.Component.RESOLUTION_SIZE;

/**
 * Created by Gabriel on 31.10.2016. Form class implementing operations for moving, resizing and
 * adding form elements.
 */
// TODO: CORE_LITE_REMOVE
public class FormWriter {

    private static final Map<Class<? extends IDesignerAttributeSupport>, IDesignerAttributeSupport> SUPPORT_CACHE = new ConcurrentHashMap<>();

    private static class XmlEntry {
        private String tag;
        private Map<String, String> attributes = new LinkedHashMap<>();
        private String body;

        String toXML() {
            StringBuilder sb = new StringBuilder();
            sb.append("<").append(tag);
            attributes.forEach((k, v) -> sb.append(" ").append(k).append("=\"" + XmlUtils.encodeAttribute(v) + "\""));
            sb.append(">");
            if (body != null) {
                sb.append("\n<![CDATA[");
                sb.append(body);
                sb.append("]]>\n");
            }
            return sb.toString();
        }

        String closeTag() {
            return "</" + tag + ">";
        }
    }

    public static void saveXml(Form form, Path targetPath, boolean preserveIds) {
        saveXml(form, targetPath, preserveIds, false);
    }

    public static void saveXml(Form form, Path targetPath, boolean preserveIds, boolean splitAvailabilityConfiguration) {
        String xml = toXml(form, preserveIds, splitAvailabilityConfiguration);
        try {
            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath, xml.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new FhFormException("Error writing form: " + e.getMessage(), e);
        }
    }

    public static String toXml(Component form, boolean preserveIds, boolean splitAvailabilityConfiguration) {
        return prettyPrintXml(elementToXml(form, preserveIds, splitAvailabilityConfiguration));
    }

    public static String toXml(Component form, boolean preserveIds) {
        return prettyPrintXml(elementToXml(form, preserveIds, false));
    }

    public static String convertToXmlAttributeValue(Class<? extends Component> componentClass, Field componentField, Object componentFieldValue) {
        if (componentFieldValue == null) {
            return null;
        }
        Optional<IComponentAttributeTypeConverter> converter = (Optional) FormReader.getInstance().getAttributeConverter(componentClass, componentField);
        if (converter.isPresent()) {
            return converter.get().toXML(componentClass, componentFieldValue);
        }
        if (componentFieldValue instanceof String) {
            return (String) componentFieldValue;
        }
        if (componentFieldValue instanceof StaticBinding) {
            return convertToXmlAttributeValue(componentClass, componentField, StaticBinding.class.cast(componentFieldValue).getStaticValue());
        }
        if (componentFieldValue instanceof ModelBinding) {
            return convertToXmlAttributeValue(componentClass, componentField, ModelBinding.class.cast(componentFieldValue).getBindingExpression());
        }
        if (componentFieldValue instanceof IndexedModelBinding) {
            return convertToXmlAttributeValue(componentClass, componentField, IndexedModelBinding.class.cast(componentFieldValue).getBindingExpression());
        }
        if (componentFieldValue instanceof ActionBinding) {
            return convertToXmlAttributeValue(componentClass, componentField, ActionBinding.class.cast(componentFieldValue).getActionBindingExpression());
        }
        if (componentFieldValue.getClass().isEnum()) {
            return Enum.class.cast(componentFieldValue).name();
        }
        return componentFieldValue.toString();
    }

    private static String prettyPrintXml(String input) {
        Source xmlInput = new StreamSource(new StringReader(input));
        StringWriter stringWriter = new StringWriter();
        StreamResult xmlOutput = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no"); // possible bug in implementation. Without this declaration there is no new line after XML_DECLARATION
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
        } catch (TransformerException e) {
            throw new FhFormException("Error parsing saved form", e);
        }


        String prettyXML = xmlOutput.getWriter().toString();
        return prettyXML;
    }

    private static XmlEntry prepareElement(Component element, boolean preserveIds) {
        Class<? extends Component> elementClass = element.getClass();

        List<Field> fields = new LinkedList<>();

        String formTag = (element instanceof Form) ? Form.class.getSimpleName() : elementClass.getSimpleName();
        getAllDeclaredFields(elementClass, fields);
        boolean xmlPropertyClassLevel = elementClass.isAnnotationPresent(XMLProperty.class);
        XmlEntry xmlEntry = new XmlEntry();
        xmlEntry.tag = formTag;
        for (Field field : fields) {
            XMLProperty xmlProperty = XMLProperty.Defaults.getOrDefaults(elementClass, field);
            if (xmlProperty == null) {
                continue;
            }

            DesignerXMLProperty designerXMLProperty = DesignerXMLProperty.Defaults.getOrDefaults(elementClass, field);

            String attributeName = !StringUtils.isNullOrEmpty(xmlProperty.value())
                    ? xmlProperty.value()
                    : field.getName();
            Optional<Method> methodGet = ReflectionUtils.findGetter(elementClass, field);
            if (!methodGet.isPresent()) {
                throw new FhFormException(elementClass + "." + field.getName() + " has not getter");
            }

            Object attributeValue = null;
            if (methodGet != null) {
                attributeValue = org.springframework.util.ReflectionUtils.invokeMethod(methodGet.get(), element);
            }

            // skip null value in object field
            if (attributeValue == null) {
                continue;
            }

            if (attributeName.equals("width")) {
                DesignerControl designerControl = DesignerControl.Utils.getDesignerControlProperties(elementClass);
                String designerControlValue = RESOLUTION_SIZE + designerControl.defaultWidth();

                if (attributeValue.equals(designerControlValue)) {
                    continue;
                }
            }

            // maybe skip writing
            boolean neverSkip = preserveIds && "id".equals(attributeName);
            if (!neverSkip && getSupport(designerXMLProperty).skipOnWrite(element.getForm(), element, attributeValue)) {
                continue;
            }

            // decuce default value which should not be writen to XML
            String defaultStringValue = "";

            // deduce default values for primitives
            if ((attributeValue.getClass() == boolean.class || attributeValue.getClass() == Boolean.class)
                    && (boolean) attributeValue == false) {
                defaultStringValue = "false";
            } else if ((attributeValue == int.class || attributeValue.getClass() == Integer.class)
                    && (int) attributeValue == 0) {
                defaultStringValue = "0";
            } else if ((attributeValue == double.class || attributeValue.getClass() == Double.class)
                    && (double) attributeValue == 0d) {
                defaultStringValue = "0.0";
            }

            // override this with explicit default value declaration
            if (!"".equals(xmlProperty.defaultValue())) {
                defaultStringValue = xmlProperty.defaultValue();
            }

            // convert value to String
            String attributeStringValue = convertToXmlAttributeValue(elementClass, field, attributeValue);

            // skip empty
            if (StringUtils.isNullOrEmpty(attributeStringValue)) {
                continue;
            }
            // skip default value
            if (attributeStringValue.equals(defaultStringValue)) {
                continue;
            }

            // decide if value should be put to attribute or body
            boolean putToBody = false;
            if (element instanceof IBodyXml) {
                IBodyXml bodySupportedElement = (IBodyXml) element;
                putToBody = attributeName.equals(bodySupportedElement.getBodyAttributeName())
                        && bodySupportedElement.shouldWriteToBody(attributeStringValue);
            }
            if (putToBody) {
                if (attributeStringValue.contains("\\{") && attributeStringValue.contains("\\}")) {
                    attributeStringValue = FormReader.ESCAPE_BINDING_TAG_START
                            + attributeStringValue.replace("\\{", "{").replace("\\}", "}")
                            + FormReader.ESCAPE_BINDING_TAG_END;
                }
                xmlEntry.body = attributeStringValue;
            } else {
                xmlEntry.attributes.put(attributeName, attributeStringValue);
            }
        }
        return xmlEntry;
    }

    public static String elementToXml(Component element, boolean preserveIds) {
        return elementToXml(element, preserveIds, false);
    }

    public static String elementToXml(Component element, boolean preserveIds, boolean splitAvailabilityConfiguration) {
        // remember already added immediate children to avoid getting the same component from different source (subcomponent, annotations etc.)
        Set<Component> alreadyAddedChildren = new HashSet<>();

        StringBuilder xml = new StringBuilder();

        // prepare element with attributes
        XmlEntry xmlEntry = prepareElement(element, preserveIds);
        // add starting element with attributes
        xml.append(xmlEntry.toXML());

        // add all embedded components
        if (element instanceof Form) {
            xml.append(stringifyAvailabilityTag(Form.class.cast(element), splitAvailabilityConfiguration));
            // avoid adding again because of XMLMetadataSubelement annotation
            alreadyAddedChildren.add(Form.class.cast(element).getAvailabilityConfiguration());
        }
        if (element instanceof IRepeatableComponentsHolder) {
            elementsToXml(xml, IRepeatableComponentsHolder.class.cast(element).getRepeatedComponents(), alreadyAddedChildren, preserveIds);
        }
        if (element instanceof IGroupingComponent && !(element instanceof Includeable)) {
            elementsToXml(xml, IGroupingComponent.class.cast(element).getSubcomponents(), alreadyAddedChildren, preserveIds);
            elementsToXml(xml, IGroupingComponent.class.cast(element).getNonVisualSubcomponents(), alreadyAddedChildren, preserveIds);
        }
        elementsToXml(xml, getSubelementsByAnnotation(element, XMLMetadataSubelement.class), alreadyAddedChildren, preserveIds);
        elementsToXml(xml, getSubelementsByAnnotation(element, XMLMetadataSubelements.class), alreadyAddedChildren, preserveIds);

        // close element
        xml.append(xmlEntry.closeTag());
        return xml.toString();
    }

    public static void elementsToXml(StringBuilder xml,
                                      Collection<? extends Component> elements,
                                      Set<Component> alreadyAddedChildren,
                                      boolean preserveIds) {
        for (Component subElement : elements) {
            if (!subElement.isArtificial() && alreadyAddedChildren.add(subElement)) {
                xml.append(elementToXml(subElement, preserveIds));
            }
        }
    }

    private static Set<Component> getSubelementsByAnnotation(Component parent, Class<? extends Annotation> fieldAnnotationClass) {
        Set<Component> subelements = new LinkedHashSet<>();
        for (Field field : ReflectionUtils.getFieldsWithHierarchy(parent.getClass(), fieldAnnotationClass)) {
            Optional<Method> getter = ReflectionUtils.findGetter(parent.getClass(), field);
            if (!getter.isPresent()) {
                throw new FhFormException("No getter for " + field.toString());
            }
            Object result = org.springframework.util.ReflectionUtils.invokeMethod(getter.get(), parent);
            if (result instanceof Collection) {
                subelements.addAll((Collection) result);
            } else if (result != null) {
                subelements.add((Component) result);
            }
        }
        return subelements;
    }

    private static String stringifyAvailabilityTag(Form form, boolean splitAvailabilityConfiguration) {
        if (form.getAvailabilityConfiguration() == null
                || form.getAvailabilityConfiguration().getSettings().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<" + AvailabilityConfiguration.class.getSimpleName() + ">");
        for (AvailabilityConfiguration.FormSetting setting : form.getAvailabilityConfiguration().getSettings()) {
            sb.append(setting.toXml(splitAvailabilityConfiguration));
        }
        sb.append("</" + AvailabilityConfiguration.class.getSimpleName() + ">");
        return sb.toString();
    }

    private static void getAllDeclaredFields(Class<?> clazz, List<Field> listFields) {
        if (!Objects.equals(clazz.getName(), Component.class.getName()))
            getAllDeclaredFields(clazz.getSuperclass(), listFields);

        listFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    }

    private static IDesignerAttributeSupport getSupport(DesignerXMLProperty designerXMLProperty) {
        Class<? extends IDesignerAttributeSupport> supportClass = designerXMLProperty.support();
        if (!SUPPORT_CACHE.containsKey(supportClass)) {
            try {
                SUPPORT_CACHE.put(supportClass, AutowireHelper.createAndAutoWire(supportClass));
            } catch (Exception e) {
                throw new FhFormException("Cannot create fixed values provider: " + supportClass.getName(), e);
            }
        }
        return SUPPORT_CACHE.get(supportClass);
    }
}
