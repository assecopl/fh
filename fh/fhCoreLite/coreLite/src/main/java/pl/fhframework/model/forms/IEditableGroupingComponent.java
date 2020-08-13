package pl.fhframework.model.forms;

import java.util.List;

/**
 * Created by Gabriel.Kurzac on 2016-11-02.
 */
public interface IEditableGroupingComponent<T extends Component> extends IGroupingComponent<T> {

    default void move(T formElement, int vector) {
        move(getSubcomponents(), formElement, vector);
    }

    public static <T> void move(List<T> subcomponents, T formElement, int vector) {
        int currentPos = subcomponents.indexOf(formElement);
        if (currentPos == -1) {
            return;
        }
        int targetPos = currentPos + vector;
        if (targetPos < 0) targetPos = 0;
        if (targetPos >= subcomponents.size()) targetPos = subcomponents.size() - 1;
        subcomponents.remove(currentPos);
        subcomponents.add(targetPos, (T) formElement);
    }
}
