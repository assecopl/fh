package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.menu.MenuElement;
import pl.fhframework.model.forms.Form;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AppMenuForm extends Form<AppMenuForm.Model> {

    @Getter
    @Setter
    public static class Model {
        private List<MenuElementHolder> menuElements = new LinkedList<>();
        private Map<String, MenuElement> originalElements = new HashMap<>();
        private String activeTabId;
        private String searchText;
    }

    @Getter
    @Setter
    public static class MenuElementHolder {
        private List<MenuElement> elements = new LinkedList<>();
    }


    public AppMenuForm() {
        setId("menuInner");
//        setStyleClasses("col-md-12 border-0");
    }
}
