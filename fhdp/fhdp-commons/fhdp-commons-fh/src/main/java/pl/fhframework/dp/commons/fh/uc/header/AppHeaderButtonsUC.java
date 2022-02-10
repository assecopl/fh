package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.ISystemUseCase;
import pl.fhframework.annotations.Action;
import pl.fhframework.app.menu.MenuService;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.model.forms.AccessibilityEnum;

@UseCase
@Getter @Setter
public class AppHeaderButtonsUC extends FhdpBaseUC implements ISystemUseCase {
    private static final String CONTAINER_ID = "appHeaderButtons";

    private AppHeaderButtonsForm.Model model;

    @Autowired
    private MenuService menuService;

    @Override
    public void start() {
        model = new AppHeaderButtonsForm.Model();
        initModel();
        showForm(AppHeaderButtonsForm.class, model);
        menuService.hide();
        super.hiddenAllBars();
    }

    @Action
    public void toggleMenu() {
        if (this.menuService.isHidden()) {
            this.menuService.show();
        } else {
            this.menuService.hide();
        }
    }

    @Action
    public void toggleSidebar() {
        super.toogleSideAndAdvancedBar();
    }

    @Action
    public void toggleAdvancedSidebar() {
        super.toogleAdvancedSideBar();
    }

    @Action
    public void toggleButtons() {
        super.toogleButtonsForm();
    }

    @Action
    public void toggleSearchButtons() {
        super.toogleSearchButtons();
    }

    public void setSideBarButtonEnabled(boolean yes) {
        if(yes) {
            model.setSideBarButtonAvailability(AccessibilityEnum.EDIT);
        } else {
            model.setSideBarButtonAvailability(AccessibilityEnum.VIEW);
        }
    }

    private void initModel() {

    }

    @Override
    public void doAfterRefresh() {

    }


    @Override
    public String getContainerId() {
        return CONTAINER_ID;
    }

}
