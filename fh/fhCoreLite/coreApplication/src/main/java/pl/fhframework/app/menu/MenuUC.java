package pl.fhframework.app.menu;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.i18n.IUseCase18nListener;
import pl.fhframework.core.uc.IUseCaseCancelEvent;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.ISystemUseCase;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.menu.MenuElement;
import pl.fhframework.menu.MenuManager;

import java.util.List;

@UseCase
//@SystemFunction(DemoSystemFunction.DEMO_PANEL_SIDE)
public class MenuUC implements ISystemUseCase, IUseCase18nListener {
    public static final String  MENU_CONTAINER_ID = "menuForm";

    @Autowired
    private MenuManager menuManager;

    @Autowired
    private EventRegistry eventRegistry;

    private MenuForm.Model model;
    private MenuForm form;

    @Override
    public void start() {
        List<MenuElement> menuElements = menuManager.getMenuModel(SessionManager.getUserSession().getSystemUser());

        model = new MenuForm.Model();
        model.setMenuElements(menuElements);
        form = showForm(MenuForm.class, model);
    }

    @Action
    public void menuElementClicked(MenuElement element) {
        if (!element.isGroupingElement()) {
            if (element.isActive()) {
                UseCaseContainer.UseCaseContext currentUseCaseContext = getUserSession().getUseCaseContainer().getCurrentUseCaseContext();
                if (currentUseCaseContext != null && currentUseCaseContext.getUseCase() instanceof IUseCaseCancelEvent) {
                    ((IUseCaseCancelEvent) currentUseCaseContext.getUseCase()).onCancel(one -> getUserSession().runUseCase(element.getRef(), element.getInputFactory()));
                } else {
                    getUserSession().runUseCase(element.getRef(), element.getInputFactory());
                }
            } else {
                eventRegistry.fireNotificationEvent(NotificationEvent.Level.WARNING, "Cloud server is inactive");
            }
        }
    }

    @Override
    public String getContainerId() {
        return MENU_CONTAINER_ID;
    }

    @Override
    public void onSessionLanguageChange() {
        List<MenuElement> menuElements = menuManager.getMenuModel(SessionManager.getUserSession().getSystemUser());

        model.setMenuElements(menuElements);

        // refresh menu structure as it cloud change
        // hide and show form as tree is marked as static - no changes are detected
        hideForm(form);
        showForm(form);
    }
}