package pl.fhframework.fhdp.example.list.checkbox;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.PropertyUtils;
import pl.fhframework.model.forms.AccessibilityEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 09/08/2020
 */
@Getter @Setter
public class CheckBoxOnTheListModel {

    private List<Wrapper> elements = new ArrayList<>();
    private Wrapper selectedElement;

    @Getter @Setter
    public static class Element {
        private String text;
        private String importantText;
        private Boolean checked = false;
        private Boolean modified = false;
        private AccessibilityEnum availability = AccessibilityEnum.EDIT;

        public Element(String text, Boolean checked, String importantText) {
            this.text = text;
            this.checked = checked;
            this.importantText = importantText;
            if (modified) {
                this.availability = AccessibilityEnum.VIEW;
            } else {
                this.availability = AccessibilityEnum.EDIT;
            }
        }
    }

    @Getter @Setter
    public static class Wrapper {
        private Element element;
        private Element oldElement;
        private Boolean disabledRow;

        public Wrapper(Element element, Element oldElement) {
            this.element = element;
            this.oldElement = oldElement;
            this.disabledRow = false;
        }
        public Wrapper(Element element, Element oldElement, Boolean disabled) {
            this.element = element;
            this.oldElement = oldElement;
            this.disabledRow = disabled;
        }

        public String wrapDisabled(String prop) {
            return wrapDisabled(prop, false);
        }
        private String extractProperty(String prop) {
            return this.extractProperty(prop, this.element);
        }
        private String extractProperty(String prop, Element container) {
            String result = prop;
            try {
                if (PropertyUtils.isReadable(container, prop)) {
                    result = (String) PropertyUtils.getProperty(container, prop);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        public String wrapDisabled(String prop, boolean forceDisabled) {
            if (disabledRow || forceDisabled) {
                return "[s]" + this.extractProperty(prop) + "[/s]";
            }
            return this.extractProperty(prop);
        }

        public String formatOldNew(String prop) {
            return this.formatOldNew(prop, "");
        }

        public String formatOldNew(String prop, String additionalClasses) {
            String classes = "";
            if (additionalClasses.length() > 0) {
                classes += additionalClasses;
            }
            if (disabledRow) {
                if(classes.isEmpty()) classes += "fhml-tag-lt";
                else classes += ",fhml-tag-lt";
            }
            String oldProp = this.extractProperty(prop, oldElement);
            String newProp = this.extractProperty(prop, element);
            if (oldProp.equals(newProp)) {
                if (classes.length() > 0) {
                    return "[className='" + classes + "']" + newProp + "[/className]";
                } else {
                    return newProp;
                }
            }
            if (classes.length() > 0) {
                return "[className='" + classes + "']" + newProp + "[/className]" + "[br/][className='old-value," + classes + "']" + oldProp + "[/className]";
            } else {
                return newProp + "[br/][className='old-value']" + oldProp + "[/className]";
            }
        }
    }
}
