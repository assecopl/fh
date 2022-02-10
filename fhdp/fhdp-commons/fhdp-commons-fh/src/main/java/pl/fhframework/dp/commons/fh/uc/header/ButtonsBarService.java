package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.services.FhService;
import pl.fhframework.event.EventRegistry;

/**
 * Created by pawel.ruta on 2018-05-21.
 */
@FhService(groupName = "menu", categories = {"gui", "menu"})
public class ButtonsBarService {
    @Autowired
    private EventRegistry eventRegistry;

    @Getter
    private boolean buttonsHidden;
    @Getter
    private boolean searchButtonsHidden;
    @Getter
    private boolean searchButtonHidden;

    public void toggleSidebar() {
        if (buttonsHidden) {
            showButtons();
        } else {
            hideButtons();
        }
    }

    public void toggleAdvancedSidebar() {
        if (searchButtonsHidden) {
            showSearchButtons();
        } else {
            hideSearchButtons();
        }
    }

    public void toggleSearchButton() {
        if (searchButtonHidden) {
            showSearchButton();
        } else {
            hideSearchButton();
        }
    }

    public void hideSearchButton() {
        searchButtonHidden = true;
        eventRegistry.fireCustomActionEvent("hideHeaderSearchButton");
    }

    public void showSearchButton() {
        searchButtonHidden = false;
        eventRegistry.fireCustomActionEvent("showHeaderSearchButton");
    }

    public void hideButtons() {
        buttonsHidden = true;
        eventRegistry.fireCustomActionEvent("hideButtons");
    }

    public void showButtons() {
        buttonsHidden = false;
        eventRegistry.fireCustomActionEvent("showButtons");
    }

    public void hideSearchButtons() {
        searchButtonsHidden = true;
        eventRegistry.fireCustomActionEvent("hideSearchButtons");
    }

    public void showSearchButtons() {
        searchButtonsHidden = false;
        eventRegistry.fireCustomActionEvent("showSearchButtons");
    }
}
