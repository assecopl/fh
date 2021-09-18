package pl.fhframework.model.forms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import pl.fhframework.core.FhFormException;
import pl.fhframework.XmlAttributeReader;
import pl.fhframework.annotations.Control;
import pl.fhframework.tools.loading.XmlUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Created by Gabriel.Kurzac on 2016-07-01.
 */
@Control(parents = {Form.class})
public class AvailabilityConfiguration extends Component {

    @Getter
    private List<FormSetting> settings = new ArrayList<>();
    private Stack<FormSetting> stack = new Stack<>();

    public AvailabilityConfiguration(Form form) {
        super(form);
    }

    @Override
    public String getType() {
        return "AvailabilityConfiguration";
    }

    private void applySettings(Form form, FormSetting setting, Variant variant, Set<AccessibilityRule> allAvailabilityRules) {
        if (setting instanceof Variant) {
            Variant embeddedVariant = (Variant) setting;
            if (embeddedVariant.getDefaultAvailability() != null) {
                form.getVariantsDefaultAvailability().put(embeddedVariant.getId(), embeddedVariant.getDefaultAvailability());
            }
            for (FormSetting subordinateSetting : setting.subordinateSettings) {
                applySettings(form, subordinateSetting, embeddedVariant, allAvailabilityRules);
            }
        } else {
            String variantId = (variant != null) ? variant.id : "";
            for (String elementId : setting.formComponentsIds) {
                AccessibilityRule addedRule = null;
                if (setting instanceof SetByProgrammer) {
                    addedRule = AccessibilityRule.createRuleDevEstablishes(elementId, variantId, this.getForm(), setting);
                } else if (setting instanceof AvailabilityValue) {
                    addedRule = AccessibilityRule.createAvailabilityValueRule(elementId, variantId, this.getForm(), (AvailabilityValue) setting);
                } else if (setting instanceof Availability) {
                    addedRule = AccessibilityRule.createAvailabilityRule(elementId, variantId, this.getForm(), (Availability) setting);
                } else if (setting.getAvailability() != null) {
                    addedRule = AccessibilityRule.createStaticRule(elementId, variantId, setting.getAvailability(), this.getForm(), setting.when, setting);
                }
                if (addedRule != null) {
                    allAvailabilityRules.add(addedRule);
                    // does it make sens to create temporary structure and then create temporary structure to create final structure at the end of reading form?
                    // - proposal - let's leave AvailabilityConfiguration structure and apply this structure at the end of reading form
                }
            }
        }
    }

    @Override
    public void finalizeReading(String text) {
        Form form = getForm();
        form.getAllAccessibilityRules().clear();
        for (FormSetting setting : settings) {
            applySettings(form, setting, null, form.getAllAccessibilityRules());
        }
    }

    public void finishReadingOfRules(String text) {
        FormSetting setting = stack.pop();
        if (text != null && !text.trim().isEmpty()) {
            String[] elementsIds = text.trim().split("\\s*,\\s*");
            setting.formComponentsIds = Arrays.asList(elementsIds).stream().map(String::trim).collect(Collectors.toList());
        }

    }

    public void readRulesForInvisible(XmlAttributeReader xmlAttributeReader) {
        Invisible newRule = new Invisible();
        newRule.when = xmlAttributeReader.getAttributeValue("when");
        addRule(newRule, xmlAttributeReader);
    }

    public void readRulesForPreview(XmlAttributeReader xmlAttributeReader) {
        Preview newRule = new Preview();
        newRule.when = xmlAttributeReader.getAttributeValue("when");
        addRule(newRule, xmlAttributeReader);
    }

    public void readRulesForEdit(XmlAttributeReader xmlAttributeReader) {
        Edit newRule = new Edit();
        newRule.when = xmlAttributeReader.getAttributeValue("when");
        addRule(newRule, xmlAttributeReader);
    }

    public void readRulesForSetByProgrammer(XmlAttributeReader xmlAttributeReader) {
        SetByProgrammer newRule = new SetByProgrammer();
        newRule.when = xmlAttributeReader.getAttributeValue("when");
        addRule(newRule, xmlAttributeReader);
    }

    public void readRulesForAvailabilityValue(XmlAttributeReader xmlAttributeReader) {
        AvailabilityValue newRule = new AvailabilityValue();
        newRule.value = xmlAttributeReader.getAttributeValue("value");
        addRule(newRule, xmlAttributeReader);
    }

    public void readRulesForAvailability(XmlAttributeReader xmlAttributeReader) {
        Availability newRule = new Availability();
        newRule.editPermissions = xmlAttributeReader.getAttributeValue("editPermissions");
        newRule.previewPermissions = xmlAttributeReader.getAttributeValue("previewPermissions");
        newRule.invisiblePermissions = xmlAttributeReader.getAttributeValue("invisiblePermissions");
        newRule.editRoles = xmlAttributeReader.getAttributeValue("editRoles");
        newRule.previewRoles = xmlAttributeReader.getAttributeValue("previewRoles");
        newRule.invisibleRoles = xmlAttributeReader.getAttributeValue("invisibleRoles");
        newRule.defaultValue = xmlAttributeReader.getAttributeValue("default");
        addRule(newRule, xmlAttributeReader);
    }

    private void addRule(FormSetting newRule, XmlAttributeReader xmlAttributeReader) {
        newRule.id = xmlAttributeReader.getAttributeValue("id");
        if (stack.isEmpty()) {
            settings.add(newRule);
        } else {
            stack.peek().subordinateSettings.add(newRule);
        }
        stack.add(newRule);
    }

    public void addVariant(XmlAttributeReader xmlAttributeReader) {
        Variant newRule = new Variant();
        String defaultAvailabilityAttr = xmlAttributeReader.getAttributeValue("defaultAvailability");
        if (!StringUtils.isEmpty(defaultAvailabilityAttr)) {
            newRule.setDefaultAvailability(AccessibilityEnum.valueOf(defaultAvailabilityAttr));
        }
        addRule(newRule, xmlAttributeReader);
    }

    @Getter
    @Setter
    public static abstract class FormSetting {

        AvailabilityEnum tag;
        AccessibilityEnum availability;

        List<FormSetting> subordinateSettings = new ArrayList<>();
        String id;
        String when;
        List<String> formComponentsIds = new ArrayList<>();

        public FormSetting(AvailabilityEnum tag, AccessibilityEnum availability) {
            this.tag = tag;
            this.availability = availability;
        }

        public String toXml() {
            StringBuilder sb = new StringBuilder();
            sb.append("<").append(tag.getTagName());
            appendXmlAttributes(sb);
            if (!hasXmlBody()) {
                sb.append("/>");
            } else {
                sb.append(">");
                appendXmlBody(sb);
                sb.append("</").append(tag.getTagName()).append(">");
            }
            return sb.toString();
        }

        /**
         * Checks if this setting contains given component id
         * @param id component id
         * @return true if this setting contains given component id
         */
        public boolean containsComponentId(String id) {
            if (formComponentsIds != null && formComponentsIds.contains(id)) {
                return true;
            }
            for (FormSetting child : subordinateSettings) {
                if (child.containsComponentId(id)) {
                    return true;
                }
            }
            return false;
        }

        protected boolean hasXmlBody() {
            return formComponentsIds != null && !formComponentsIds.isEmpty();
        }

        protected void appendXmlBody(StringBuilder sb) {
            sb.append(StringUtils.collectionToCommaDelimitedString(formComponentsIds));
        }

        protected void appendXmlAttributes(StringBuilder sb) {
            if (id!=null && !id.isEmpty()) {
                sb.append(" id=\"").append(XmlUtils.encodeAttribute(id)).append("\"");
            }
            if (when!=null && !when.isEmpty()){
                sb.append(" when=\"").append(XmlUtils.encodeAttribute(when)).append("\"");
            }
        }

        public static FormSetting createNew(AvailabilityEnum tag) {
            switch (tag) {
                case EDIT:
                    return new Edit();
                case INVISIBLE:
                    return new Invisible();
                case PREVIEW:
                case READONLY:
                    return new Preview();
                case SET_BY_PROGRAMMER:
                    return new SetByProgrammer();
                case VALUE:
                    return new AvailabilityValue();
                case AVAILABILITY:
                    return new Availability();
                default:
                    throw new FhFormException("No appropriate setting for: " + tag);
            }
        }
    }

    public static class Edit extends FormSetting {
        public Edit() {
            super(AvailabilityEnum.EDIT, AccessibilityEnum.EDIT);
        }
    }

    public static class Preview extends FormSetting {
        public Preview() {
            super(AvailabilityEnum.PREVIEW, AccessibilityEnum.VIEW);
        }
    }

    public static class Invisible extends FormSetting {
        public Invisible() {
            super(AvailabilityEnum.INVISIBLE, AccessibilityEnum.HIDDEN);
        }
    }

    public static class SetByProgrammer extends FormSetting {
        public SetByProgrammer() {
            super(AvailabilityEnum.SET_BY_PROGRAMMER, null);
        }
    }

    public static class AvailabilityValue extends FormSetting {

        @Getter
        @Setter
        private String value;

        public AvailabilityValue() {
            super(AvailabilityEnum.VALUE, null);
        }

        @Override
        protected void appendXmlAttributes(StringBuilder sb) {
            super.appendXmlAttributes(sb);
            if (value != null) {
                sb.append(" value=\"").append(value).append("\"");
            }
        }
    }

    public static class Availability extends FormSetting {

        @Getter
        @Setter
        private String editPermissions;

        @Getter
        @Setter
        private String previewPermissions;

        @Getter
        @Setter
        private String invisiblePermissions;

        @Getter
        @Setter
        private String editRoles;

        @Getter
        @Setter
        private String previewRoles;

        @Getter
        @Setter
        private String invisibleRoles;

        @Getter
        @Setter
        private String defaultValue;

        public Availability() {
            super(AvailabilityEnum.AVAILABILITY, null);
        }

        @Override
        protected void appendXmlAttributes(StringBuilder sb) {
            super.appendXmlAttributes(sb);
            if (editPermissions != null) {
                sb.append(" editPermissions=\"").append(editPermissions).append("\"");
            }
            if (previewPermissions != null) {
                sb.append(" previewPermissions=\"").append(previewPermissions).append("\"");
            }
            if (invisiblePermissions != null) {
                sb.append(" invisiblePermissions=\"").append(invisiblePermissions).append("\"");
            }
            if (editRoles != null) {
                sb.append(" editRoles=\"").append(editRoles).append("\"");
            }
            if (previewRoles != null) {
                sb.append(" previewRoles=\"").append(previewRoles).append("\"");
            }
            if (invisibleRoles != null) {
                sb.append(" invisibleRoles=\"").append(invisibleRoles).append("\"");
            }
            if (defaultValue != null) {
                sb.append(" default=\"").append(defaultValue).append("\"");
            }
        }
    }

    public static class Variant extends FormSetting {

        @Getter
        @Setter
        private AccessibilityEnum defaultAvailability;

        public Variant() {
            super(AvailabilityEnum.VARIANT, null);
        }

        @Override
        protected boolean hasXmlBody() {
            return subordinateSettings != null && !subordinateSettings.isEmpty();
        }

        @Override
        protected void appendXmlBody(StringBuilder sb) {
            for (FormSetting setting : subordinateSettings) {
                sb.append(setting.toXml());
            }
        }

        @Override
        protected void appendXmlAttributes(StringBuilder sb) {
            super.appendXmlAttributes(sb);
            if (defaultAvailability != null) {
                sb.append(" defaultAvailability=\"").append(defaultAvailability.name()).append("\"");
            }
        }

        @Override
        public AccessibilityEnum getAvailability() {
            return getDefaultAvailability();
        }
    }
}
