package pl.fhframework.app.menu;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.menu.MenuElement;
import pl.fhframework.model.forms.Form;

import java.util.LinkedList;
import java.util.List;

public class MenuForm extends Form<MenuForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private List<MenuElement> menuElements = new LinkedList<>();
    }
}
