package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.services.FhService;
import pl.fhframework.event.EventRegistry;

/**
 * Created by pawel.ruta on 2018-05-21.
 */
@FhService(groupName = "menu", categories = {"gui", "menu"})
public class SideBarService {
    @Autowired
    private EventRegistry eventRegistry;

    @Getter
    private boolean sidebarHidden;
    @Getter
    private boolean advancedSidebarHidden;

    public void toggleSidebar() {
        if (sidebarHidden) {
            showSideBar();
        } else {
            hideSideBar();
        }
    }

    public void toggleAdvancedSidebar() {
        if (advancedSidebarHidden) {
            showAdvancedSideBar();
        } else {
            hideAdvancedSideBar();
        }
    }

    public void hideSideBar() {
        sidebarHidden = true;
        eventRegistry.fireCustomActionEvent("hideSideBar");
    }

    public void showSideBar() {
        sidebarHidden = false;
        eventRegistry.fireCustomActionEvent("showSideBar");
    }

    public void hideAdvancedSideBar() {
        advancedSidebarHidden = true;
        eventRegistry.fireCustomActionEvent("hideAdvancedSideBar");
    }

    public void showAdvancedSideBar() {
        advancedSidebarHidden = false;
        eventRegistry.fireCustomActionEvent("showAdvancedSideBar");
    }
}
