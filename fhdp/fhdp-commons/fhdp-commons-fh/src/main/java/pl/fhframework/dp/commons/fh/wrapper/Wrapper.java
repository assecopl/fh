package pl.fhframework.dp.commons.fh.wrapper;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Slf4j
public class Wrapper<T> implements Serializable {

    private T element;
    private T oldElement;
    private Boolean disabledRow;
    private Boolean isLastValueEnabled;

    public Wrapper() {}

    public Wrapper(T element, T oldElement) {
        this.element = element;
        this.oldElement = oldElement;
        this.disabledRow = false;
        this.isLastValueEnabled = false;
    }

    public Wrapper(T element, T oldElement, Boolean disabled, Boolean isLastValueEnabled) {
        this.element = element;
        this.oldElement = oldElement;
        this.disabledRow = disabled;
        this.isLastValueEnabled = isLastValueEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wrapper<?> wrapper = (Wrapper<?>) o;
        return Objects.equals(element, wrapper.element) &&
                Objects.equals(oldElement, wrapper.oldElement) &&
                Objects.equals(disabledRow, wrapper.disabledRow) &&
                Objects.equals(isLastValueEnabled, wrapper.isLastValueEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, oldElement, disabledRow, isLastValueEnabled);
    }

    public String wrapDisabled(String prop) {
        return wrapDisabled(prop, false);
    }

    private String extractProperty(String prop) {
        return this.extractProperty(prop, this.element);
    }

    private String extractProperty(String prop, T container) {
        String result = "";
        try {
            if (PropertyUtils.isReadable(container, prop)) {
                Object propertyObject = PropertyUtils.getNestedProperty(container, prop);
                if(propertyObject != null) {
                    result = propertyObject.toString();
                }
            }
        } catch (Exception e) {
            log.error("extractProperty: Problem with extracting prop: {} from class: {}", prop, container.getClass());
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

        // dodanie za każdym razem niezbędnej klasy zapewniającej łamanie wiersza
        if(classes.isEmpty()) classes += "flex-basis-100";
        else classes += ",flex-basis-100";

        String newProp = this.extractProperty(prop, element);

        if (oldElement != null) {
            String oldProp = this.extractProperty(prop, oldElement);

            if (oldProp.equals(newProp)) {
                if (classes.length() > 0) {
                    return "[className='" + classes + "']" + newProp + "[/className]";
                }
            } else {
                // obsługa dla "[Dodano]
                if (!oldProp.equals(newProp) && oldProp.equals("")) {
                    // jeżeli poprzednia wartość isnieje, ale jest pusta to
                    // oznacza to, że mamy do czynienia z sytuacją dodania nowej wartości
                    return createAddMessage(newProp);
                } else {
                    return "[className='" + classes + "']" + newProp + "[/className]" + "[br/][className='old-value," + classes + "']" + oldProp + "[/className]";
                }
            }

            if (oldProp.equals(newProp)) {
                return newProp;
            } else {
                return "[className='" + classes + "']" + newProp + "[/className]" + "[br/][className='old-value," + classes + "']" + oldProp + "[/className]";
            }

        } else {
            if (isLastValueEnabled) {
                // jeżeli jesteśmy podczas korekty oraz oldElement = null, oznacza to że jest to nowy wiersz
                // należy dodać [Dodano] do każdego z pól
                return createAddMessage(newProp);
            } else {
                return newProp;
            }
        }
    }

    private String createAddMessage(String newProp) {
        if(newProp.equals("")) {
            return newProp;
        } else {
            return newProp + " [Dodano]";
        }
    }
}
