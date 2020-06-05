package pl.fhframework.model.forms.docs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import pl.fhframework.core.designer.IDocumentationUseCase;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.*;
import pl.fhframework.annotations.Control;
import pl.fhframework.binding.ModelBinding;
import pl.fhframework.consts.DocumentationCategoryConsts;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.attributes.AttributeHolder;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.core.designer.DocumentedAttribute;
import pl.fhframework.model.forms.docs.model.FormComponentDocumentationHolder;
import pl.fhframework.model.forms.docs.model.FormElementCategory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

@Service
public class FormComponentsDocumentationService {

    private static final String FORM_CLASS_PATH = "pl.fhframework.docs.forms.component.%s%sForm";
    private static final String ELEMENT_CLASS_PATH = "pl.fhframework.docs.forms.component.%smodel.%sElement";
    private static final String UC_CLASS_PATH = "pl.fhframework.docs.uc.%s%sUC";
    private static final String DOCUMENTED_COMPONENTS_PACKAGE = "pl";
    private static final String GIS2D_PACKAGE = "pl.fhframework.gis2d";
    private static final String FH_BASIC_COMPONENTS_PACKAGE = "pl.fhframework.model";

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private MessageService messageService;

    public FormComponentDocumentationHolder findDocumentedBasicComponentsForPredicate(Boolean canBeDesigned) throws ClassNotFoundException {
        FormComponentDocumentationHolder form = new FormComponentDocumentationHolder();
        DocumentationPathParams pathParams = new DocumentationPathParams(FORM_CLASS_PATH, ELEMENT_CLASS_PATH, FH_BASIC_COMPONENTS_PACKAGE, UC_CLASS_PATH);
        Map<String, FormElementCategory> documentationElementsMap = collectDocumentedFormComponents(isInPackage(pathParams.getComponentsPackage()), canBeDesigned, pathParams);

        ArrayList<FormElementCategory> componentCategories = new ArrayList<>(documentationElementsMap.values());
        componentCategories.forEach(c -> c.setName(messageService.getBundle("fhCoreMessageSource").getEnumMessage(c.getCategory())));
        componentCategories.sort(Comparator.comparing(o -> o.getCategory().ordinal()));
        form.setFormElements(componentCategories);
        return form;
    }

    public FormComponentDocumentationHolder findDocumentedMap2dComponentsForPredicate(Predicate<? super Class<?>> predicate, DocumentationPathParams pathParams) throws ClassNotFoundException {
        return findDocumentedMap2dComponentsForPredicate(predicate, pathParams, DocumentationCategoryConsts.MAP_2D_CATEGORY);
    }

    public FormComponentDocumentationHolder findDocumentedMap2dComponentsForPredicate(Predicate<? super Class<?>> predicate, DocumentationPathParams pathParams, String category) throws ClassNotFoundException {
        FormComponentDocumentationHolder form = new FormComponentDocumentationHolder();
        Map<String, FormElementCategory> documentationElementsMap = collectDocumentedFormComponents(predicate, null, pathParams);
        form.setMap2dElements(documentationElementsMap.get("").getComponents());
        return form;
    }

    public Map<String, FormElementCategory> collectDocumentedFormComponents(Predicate<? super Class<?>> predicate, Boolean canBeDesigned, DocumentationPathParams pathParams) throws ClassNotFoundException {
        Map<String, FormElementCategory> componentDocumentation = new TreeMap<>();
        //TODO:think about get by superclass not only by annotation
        List<Class<?>> documentedClasses = findDocumentedClasses(predicate);

        for (Class<?> clazz : documentedClasses) {
            Control formElementAnnotation = clazz.getAnnotation(Control.class);

            if (canBeDesigned == null || (formElementAnnotation != null && canBeDesigned.equals(formElementAnnotation.canBeDesigned()))) {
                DocumentedComponent classAnnotation = clazz.getAnnotation(DocumentedComponent.class);
                String[] attributeExcludes = classAnnotation.ignoreFields();
                String formComponentSimpleName = clazz.getSimpleName();
                Class<? extends ComponentElement> formDocumentationComponentClassName;
                ComponentElement element;
                DocumentedComponent.Category category = classAnnotation.category();
                //if category attribute is set look for form and model inside *.<category>.* package i.e. for maps search in pl.fhframework.docs.forms.component.map3d package
                try {
                    formDocumentationComponentClassName = (Class<? extends ComponentElement>) Class.forName(String.format(pathParams.getModelClassPath(), "", formComponentSimpleName));
                    element = appContext.getBean(formDocumentationComponentClassName);
                    element.setForm((Class<? extends Form>) Class.forName(String.format(pathParams.getFormClassPath(), "", formComponentSimpleName)));
                    element.setUseCase((Class<IDocumentationUseCase>) ReflectionUtils.tryGetClassForName(String.format(pathParams.getUcClassPath(), "", formComponentSimpleName)));

                    element.setDescription(classAnnotation.value());
                    element.setComponentName(formComponentSimpleName);
                    element.setClazz(clazz);
                    element.setCategory(category);
                    element.setIcon(classAnnotation.icon());

                    collectDocumentedAttributes(clazz, element, Arrays.asList(attributeExcludes));

                    String categoryName = category.toString();
                    if (componentDocumentation.containsKey(categoryName)) {
                        componentDocumentation.get(categoryName).getComponents().add(element);
                    } else {
                        FormElementCategory formElementCategory = new FormElementCategory(categoryName, category, new ArrayList<>());
                        formElementCategory.getComponents().add(element);
                        componentDocumentation.put(categoryName, formElementCategory);
                    }
                } catch (ClassNotFoundException ex) {
                    FhLogger.warn("There is no documentation for component", ex);
                }
            }
        }

        return componentDocumentation;
    }

    private List<Class<?>> findDocumentedClasses(Predicate<? super Class<?>> predicate) {
        List<Class<?>> annotatedClasses = ReflectionUtils.getAnnotatedClasses(DOCUMENTED_COMPONENTS_PACKAGE, DocumentedComponent.class);
        if (predicate == null) {
            return annotatedClasses;
        }
        List<Class<?>> fhStandardControls = filterStandardControlsDocumentation(annotatedClasses);
        /*return annotatedClasses
                .stream().filter(predicate).collect(Collectors.toList());*/
        return new ArrayList<>(fhStandardControls);
    }

    private  List<Class<?>> filterStandardControlsDocumentation(List<Class<?>> annotatedClasses) {
        List<Class<?>> fhStandardControls = new ArrayList<>();

        for (Class<?> annotatedClass : annotatedClasses) {
            DocumentedComponent documentedComponentAnnotation = annotatedClass.getAnnotation(DocumentedComponent.class);

            if (documentedComponentAnnotation.documentationExample()) {
                fhStandardControls.add(annotatedClass);
            }
        }

        return fhStandardControls;
    }

    private void collectDocumentedAttributes(Class<?> clazz, ComponentElement element, List<String> attributeExcludes) {
        List<DocumentedAttribute> documentedAttributes = new ArrayList<>();
        while (clazz != null) {
            documentedAttributes.addAll(collectDocumentedAttributes(clazz, attributeExcludes));
            clazz = clazz.getSuperclass();
        }
        Collections.sort(documentedAttributes, (attributeDoc1, attributeDoc2) -> attributeDoc1.getName().compareTo(attributeDoc2.getName()));

        element.setAttributes(documentedAttributes);
    }

    private List<DocumentedAttribute> collectDocumentedAttributes(Class<?> clazz, List<String> attributeExcludes) {
        List<DocumentedAttribute> documentedAttributes = new ArrayList<>();
        List<Field> holderFields = ReflectionUtils.getFields(clazz, DocumentedAttributesHolder.class);
        for (Field field : holderFields) {
            if (ClassUtils.isAssignable(AttributeHolder.class, field.getType())) {
                final DocumentedAttributesHolder documentationAttributesHolder = field.getAnnotation(DocumentedAttributesHolder.class);
                final Class<?>[] attributeClasses = documentationAttributesHolder.attributeClasses();
                for (Class<?> attributeClass : attributeClasses) {
                    DocumentedAttribute documentedAttribute = new DocumentedAttribute();
                    XMLProperty xmlPropertyAnnotation = attributeClass.getAnnotation(XMLProperty.class);
                    String xmlAttributeValue = xmlPropertyAnnotation.value();
                    if (attributeExcludes.contains(xmlAttributeValue)) {
                        continue;
                    }

                    final DocumentedComponentAttribute documentedComponentAttributeAnnotation = attributeClass.getAnnotation(DocumentedComponentAttribute.class);

                    documentedAttribute.setType(documentedComponentAttributeAnnotation.type().getSimpleName());
                    documentedAttribute.setName(xmlAttributeValue);
                    documentedAttribute.setDescription(documentedComponentAttributeAnnotation.value());
                    documentedAttribute.setDefaultValue(documentedComponentAttributeAnnotation.defaultValue());
                    documentedAttribute.setBoundable(documentedComponentAttributeAnnotation.boundable());
                    documentedAttributes.add(documentedAttribute);
                }
            }
        }

        List<Field> fields = ReflectionUtils.getFields(clazz, DocumentedComponentAttribute.class);
        for (Field f : fields) {
            DocumentedAttribute documentedAttribute = createDocumentedAttribute(clazz, attributeExcludes, f);
            if (documentedAttribute == null) continue;

            documentedAttributes.add(documentedAttribute);
        }
        return documentedAttributes;
    }

    private static Predicate<? super Class<?>> isInPackage(final String packageName) {
        return clazz -> clazz.getName().contains(packageName);
    }


    private static Predicate<? super Class<?>> isNotGis2d() {
        return clazz -> !clazz.getName().contains(GIS2D_PACKAGE);
    }

    private DocumentedAttribute createDocumentedAttribute(Class<?> clazz, List<String> attributeExcludes, Field f) {
        DocumentedAttribute documentedAttribute = new DocumentedAttribute();
        if (documentedAttribute.getType() == null) {
            documentedAttribute.setType(f.getType().getSimpleName());
        }

        String fieldName = f.getName();
        if (attributeExcludes.contains(fieldName)) {
            return null;
        }

        XMLProperty xmlPropertyAnnotation = f.getAnnotation(XMLProperty.class);
        String xmlAttributeValue = xmlPropertyAnnotation.value();
        if (xmlPropertyAnnotation != null && !StringUtils.isEmpty(xmlPropertyAnnotation.value())) {
            documentedAttribute.setName(xmlAttributeValue);
        } else {
            documentedAttribute.setName(fieldName);
        }

        DocumentedComponentAttribute attributeAnnotation = f.getAnnotation(DocumentedComponentAttribute.class);
        documentedAttribute.setDescription(attributeAnnotation.value());

        documentedAttribute.setDefaultValue(attributeAnnotation.defaultValue());
        documentedAttribute.setBoundable(attributeAnnotation.boundable());
        if (attributeAnnotation.type() != DocumentedComponentAttribute.EMPTY.class) {
            documentedAttribute.setType(attributeAnnotation.type().getSimpleName());
        }

        if (attributeAnnotation.canReadNested()) {
            Class<?> nestedClass = f.getType();
            if (ModelBinding.class.isAssignableFrom(nestedClass) && ReflectionUtils.getGenericTypeInFieldType(f, 0) != null) {
                Class<?> modelBindingClass = ReflectionUtils.getGenericTypeInFieldType(f, 0);
                org.springframework.util.ReflectionUtils.doWithFields(modelBindingClass, nestedField ->
                                documentedAttribute.getNestedAttributes().add(createDocumentedAttribute(modelBindingClass, attributeExcludes, nestedField))
                        , field -> field.isAnnotationPresent(DocumentedComponentAttribute.class));
            } else {
                org.springframework.util.ReflectionUtils.doWithFields(nestedClass, nestedField ->
                                documentedAttribute.getNestedAttributes().add(createDocumentedAttribute(nestedClass, attributeExcludes, nestedField))
                        , field -> field.isAnnotationPresent(DocumentedComponentAttribute.class));
            }
        }
        return documentedAttribute;
    }
}
