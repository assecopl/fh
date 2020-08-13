package pl.fhframework.app.menu;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.services.FhService;
import pl.fhframework.event.EventRegistry;

/**
 * Created by pawel.ruta on 2018-05-21.
 */
@FhService(groupName = "menu", categories = {"gui", "menu"})
public class MenuService {
    @Autowired
    private EventRegistry eventRegistry;

    @Getter
    private boolean hidden;

    public void toggle() {
        if (hidden) {
            show();
        } else {
            hide();
        }
    }

    public void hide() {
        hidden = true;
        eventRegistry.fireCustomActionEvent("hideMenu");
    }

    public void show() {
        hidden = false;
        eventRegistry.fireCustomActionEvent("showMenu");
    }
}
