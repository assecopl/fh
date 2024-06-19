package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import pl.fhframework.core.designer.ComponentElement;
import pl.fhframework.docs.converter.model.User;
import pl.fhframework.docs.converter.service.UserService;

import java.util.List;
import java.util.function.BiPredicate;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SelectComboMenuElement extends ComponentElement {

    private MultiValueMap<String, User> selectedComboMenuData;
    private User simpleSelectedComboMenu;
    private String simpleString;
    private List<User> simpleSelectedComboMenuData;
    private List<User> simpleSelectedComboMenuWithEmptyValue;
    private BiPredicate<User, String> userSelectedComboMenuFilter = (k, v) -> k.getLastName().toLowerCase().contains(v.toLowerCase());
    private String selectComboMenuOnChangeValue = "Poland";

    public SelectComboMenuElement(UserService userService) {
        this.selectedComboMenuData = userService.getComboData();
        this.simpleSelectedComboMenuData = userService.findAll();
        this.simpleSelectedComboMenuWithEmptyValue = userService.findAll();
    }
}
