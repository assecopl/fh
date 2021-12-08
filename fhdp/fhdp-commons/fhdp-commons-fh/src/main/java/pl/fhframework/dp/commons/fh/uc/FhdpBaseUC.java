package pl.fhframework.dp.commons.fh.uc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.uc.header.AppSiderService;
import pl.fhframework.dp.commons.fh.uc.header.ButtonsBarService;
import pl.fhframework.dp.commons.fh.uc.header.SideBarService;
import pl.fhframework.core.uc.IUseCaseRefreshListener;

@Getter
public class FhdpBaseUC implements IUseCaseRefreshListener {

    @Autowired
    protected ButtonsBarService buttonsBarService;
    @Autowired
    protected SideBarService sideBarService;
    @Autowired
    protected AppSiderService appSiderService;

    private boolean hiddenSideBar = true;
    private boolean hiddenAdvancedSideBar = true;
    private boolean hiddenAppSiderHelp = true;
    private boolean hiddenAppSiderDetails = true;
    private boolean hiddenButtonsForm = true;
    private boolean hiddenSearchButtons = true;
    private boolean hiddenHeaderSearchButton = true;

    public void hiddenBars(boolean sideBar, boolean advancedSidebar, boolean siderHelp, boolean siderDetails,
                           boolean buttonsForm, boolean searchButtons, boolean headerSearchButton) {
        sideBarManagement(sideBar);
        advancedSidebarManagement(advancedSidebar);
        appSiderHelpManagement(siderHelp);
        appSiderDetailsManagement(siderDetails);
        buttonsFormManagement(buttonsForm);
        searchButtonsManagement(searchButtons);
        headerSearchButtonManagement(headerSearchButton);

    }

    public void hiddenAllBars() {
        hiddenBars(true, true, true, true, true, true,
                true);
    }

    public void declarationBars(boolean headerSearchButton) {
        hiddenBars(false, true, true, true, true, false,
                headerSearchButton);
    }

    public void doAfterRefresh() {
        this.refreshPage();
    }

    public void refreshPage() {
        hiddenBars(hiddenSideBar, hiddenAdvancedSideBar, hiddenAppSiderHelp, hiddenAppSiderDetails, hiddenButtonsForm,
                hiddenSearchButtons, hiddenHeaderSearchButton);
    }

    public void toogleSideBar() {
        sideBarManagement(!hiddenSideBar);
    }

    public void sideBarManagement(boolean hidden) {
        if(hidden) {
            hiddenSideBar = true;
            sideBarService.hideSideBar();
        } else {
            hiddenSideBar = false;
            sideBarService.showSideBar();
        }
    }

    public void toogleSideAndAdvancedBar() {
        sideAdvancedBarManagement(!hiddenSideBar);
    }

    public void sideAdvancedBarManagement(boolean hidden) {
        if(hidden) {
            hiddenSideBar = true;
            sideBarService.hideSideBar();

            hiddenAdvancedSideBar = true;
            sideBarService.hideAdvancedSideBar();
        } else {
            hiddenSideBar = false;
            sideBarService.showSideBar();
        }
    }

    public void toogleAdvancedSideBar() {
        advancedSidebarManagement(!hiddenAdvancedSideBar);
    }

    public void advancedSidebarManagement(boolean hidden) {
        if(hidden) {
            hiddenAdvancedSideBar = true;
            sideBarService.hideAdvancedSideBar();
        }
        else {
            hiddenAdvancedSideBar = false;
            sideBarService.showAdvancedSideBar();
        }
    }

    public void toogleAppSiderHelp() {
        appSiderHelpManagement(!hiddenAppSiderHelp);
    }

    public void appSiderHelpManagement(boolean hidden) {
        if(hidden) {
            hiddenAppSiderHelp = true;
            appSiderService.hideHelp();
        } else {
            hiddenAppSiderHelp = false;
            appSiderService.showHelp();
        }
    }

    public void toogleAppSiderDetails() {
        appSiderDetailsManagement(!hiddenAppSiderDetails);
    }

    public void appSiderDetailsManagement(boolean hidden) {
        if(hidden) {
            hiddenAppSiderDetails = true;
            appSiderService.hideDetails();
        }
        else {
            hiddenAppSiderDetails = false;
            appSiderService.showDetails();
        }
    }

    public void toogleButtonsForm() {
        buttonsFormManagement(!hiddenButtonsForm);
    }

    public void buttonsFormManagement(boolean hidden) {
        if(hidden) {
            hiddenButtonsForm = true;
            buttonsBarService.hideButtons();
        }
        else {
            hiddenButtonsForm = false;
            buttonsBarService.showButtons();
        }
    }

    public void toogleSearchButtons() {
        searchButtonsManagement(!hiddenSearchButtons);
    }

    public void searchButtonsManagement(boolean hidden) {
        if(hidden) {
            hiddenSearchButtons = true;
            buttonsBarService.hideSearchButtons();
        }
        else {
            hiddenSearchButtons = false;
            buttonsBarService.showSearchButtons();
        }
    }

    public void toogleHeaderSearchButton() {
        headerSearchButtonManagement(!hiddenHeaderSearchButton);
    }

    public void headerSearchButtonManagement(boolean hidden) {
        if(hidden) {
            hiddenHeaderSearchButton = true;
            buttonsBarService.hideSearchButton();
        }
        else {
            hiddenHeaderSearchButton = false;
            buttonsBarService.showSearchButton();
        }
    }
}
