package pl.fhframework.compiler.core.generator;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.TemplateControl;
import pl.fhframework.annotations.XMLProperty;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.core.FhException;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.helper.AutowireHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GenerationUtils {

    public static class Form extends Component implements pl.fhframework.model.forms.Form.IFormGenerationUtils {
        private pl.fhframework.model.forms.Form<?> form;

        private String fullName;

        private ModuleMetaModel metadata;

        @Autowired
        private FormsManager formsManager;

        public Form(pl.fhframework.model.forms.Form<?> form, String fullName, ModuleMetaModel metadata) {
            super(form);
            this.form = form;
            this.fullName = fullName;
            this.metadata = metadata;
            AutowireHelper.autowire(this, formsManager);
            fillGenerationUtils();
        }

        private void fillGenerationUtils() {
            this.form.doActionForEverySubcomponent(component -> {
                component.setGenerationUtils(new Component(component));
            });
        }

        @Override
        public Set<ActionSignature> getEvents() {
            return formsManager.getFormActions(DynamicClassName.forClassName(fullName));
        }
    }

    public static class Component implements pl.fhframework.model.forms.Component.IGenerationUtils {
        pl.fhframework.model.forms.Component component;

        public Component(pl.fhframework.model.forms.Component component) {
            this.component = component;
        }

        @Override
        public List<pl.fhframework.model.forms.Component.ComponentAttr> getAttributes() {
            List<pl.fhframework.model.forms.Component.ComponentAttr> attrs = new ArrayList<>();

            Class targetClass = component.getClass();
            // @XMLProperty
            for (Field field : ReflectionUtils.getFieldsWithHierarchy(targetClass, XMLProperty.class)) {
                XMLProperty attrAnnotation = field.getAnnotation(XMLProperty.class);
                if (attrAnnotation.skipCompiler()) {
                    continue;
                }

                String attrName = attrAnnotation.value();
                if ("".equals(attrName)) {
                    attrName = field.getName();
                }

                Optional<Method> getter = ReflectionUtils.findGetter(targetClass, field);
                Object fieldValue = null;
                try {
                    fieldValue = getter.get().invoke(component);
                } catch (IllegalAccessException | InvocationTargetException exception) {
                    throw new FhException(exception);
                }
                if (fieldValue == null) {
                    continue;
                }

                Class fieldType = field.getType();
                fieldType = ReflectionUtils.mapPrimitiveToWrapper(fieldType);

                attrs.add(new pl.fhframework.model.forms.Component.ComponentAttr(attrName, fieldValue, fieldType));
            }


            return attrs;
        }

        @Override
        public Object getFieldValue(Field field) {
            Method getter = ReflectionUtils.findGetter(component.getClass(), field).orElseThrow(() -> new RuntimeException(String.format("Cannot find getter for field '%s'.", field.getName())));

            try {
                return getter.invoke(component);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Exception while getting value from field %s.%s",
                        field.getDeclaringClass().getName(), field.getName()), e);
            }
        }

        @Override
        public String getTagName() {
            TemplateControl templateControl = ReflectionUtils.giveClassAnnotations(component.getClass(), TemplateControl.class);
            if (templateControl == null) {
                return "fh-" + component.getClass().getSimpleName().toLowerCase();
            } else {
                return templateControl.tagName();
            }
        }
    }
}

