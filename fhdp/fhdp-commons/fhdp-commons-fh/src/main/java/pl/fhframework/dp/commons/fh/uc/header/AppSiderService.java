package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.services.FhService;
import pl.fhframework.event.EventRegistry;

/**
 * Created by pawel.ruta on 2018-05-21.
 */
@FhService(groupName = "menu", categories = {"gui", "menu"})
public class AppSiderService {
    @Autowired
    private EventRegistry eventRegistry;

    @Getter
    private boolean hiddenHelp = true;

    @Getter
    private boolean hiddenDetails = true;

    public void toggleHelp() {
        if (hiddenHelp) {
            showHelp();
        } else {
            hideHelp();
        }
    }

    public void toggleDetails() {
        if (hiddenDetails) {
            showDetails();
        } else {
            hideDetails();
        }
    }

    public void hideHelp() {
        hiddenHelp = true;
        eventRegistry.fireCustomActionEvent("hideAppSiderHelp");
    }

    public void showHelp() {
        hiddenHelp = false;
        eventRegistry.fireCustomActionEvent("showAppSiderHelp");
    }

    public void hideDetails() {
        hiddenDetails = true;
        eventRegistry.fireCustomActionEvent("hideAppSiderDetails");
    }

    public void showDetails() {
        hiddenDetails = false;
        eventRegistry.fireCustomActionEvent("showAppSiderDetails");
    }
}
