package pl.fhframework.docs.forms.component.model;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import pl.fhframework.docs.converter.model.User;
import pl.fhframework.docs.converter.service.UserService;
import pl.fhframework.core.designer.ComponentElement;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComboElement extends ComponentElement {
    private MultiValueMap<String, User> comboData;
    private User selectedCombo;
    private List<User> simpleComboData;
    private List<User> comboDataWithEmptyValue;
    private User simpleSelectedCombo;
    private User simpleSelectedComboWithDisplayFunction;
    private User comboUserWithEmptyValue;
    private BiPredicate<User, String> userComboFilter = (k, v) -> k.getLastName().toLowerCase().contains(v.toLowerCase());
    private Function<User, String> userAsFirstLastNameFunction = (u) -> "" + u.getFirstName() + " " + u.getLastName();
    private List<User> selectedUsers;
    private LinkedList<String> selectedColors;

    private LinkedList<String> enumColors = new LinkedList<>();

    enum SimpleEnumColor {
        RED, GREEN, BLUE, YELLOW, BLACK,
    }

    public LinkedList<String> setEnumValues() {
        SimpleEnumColor[] colors = SimpleEnumColor.values();
        for (SimpleEnumColor color : colors) {
            this.enumColors.add(color.toString());
        }
        return this.enumColors;
    }

    public ComboElement(UserService userService) {
        this.comboData = userService.getComboData();
        this.simpleComboData = userService.findAll();
        this.comboDataWithEmptyValue = userService.findAll();
        setEnumValues();
    }
}
