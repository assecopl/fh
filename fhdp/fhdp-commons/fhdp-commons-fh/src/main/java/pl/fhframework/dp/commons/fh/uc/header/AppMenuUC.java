package pl.fhframework.dp.commons.fh.uc.header;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.ISystemUseCase;
import pl.fhframework.SessionManager;
import pl.fhframework.annotations.Action;
import pl.fhframework.app.menu.MenuService;
import pl.fhframework.core.i18n.IUseCase18nListener;
import pl.fhframework.core.uc.IUseCaseCancelEvent;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.UseCaseContainer;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.event.dto.NotificationEvent;
import pl.fhframework.menu.MenuElement;
import pl.fhframework.menu.MenuManager;
import pl.fhframework.model.forms.Form;

import java.util.List;
import java.util.stream.Collectors;

@UseCase
@Getter @Setter
public class AppMenuUC extends FhdpBaseUC implements ISystemUseCase, IUseCase18nListener {
    public static final String MENU_CONTAINER_ID = "menuForm";

    @Autowired
    protected MenuManager menuManager;
    @Autowired
    protected MenuService menuService;
    @Autowired
    protected EventRegistry eventRegistry;

    @Autowired
    protected ApplicationContext context;


    protected AppMenuForm.Model model;
    protected Form form;

    @Override
    public void start() {
        model = new AppMenuForm.Model();
        List<MenuElement> menuElements = menuManager.getMenuModel(SessionManager.getUserSession().getSystemUser());
        for (MenuElement el : menuElements) {
            AppMenuForm.MenuElementHolder holder = new AppMenuForm.MenuElementHolder();
//            el.setChildren(filterMenuElements(model.getSearchText(), el.getChildren()));
            holder.getElements().add(el);
            model.getMenuElements().add(holder);
            MenuElement elCopy = new MenuElement(el);
            copyChildrenRecursive(el, elCopy);
            model.getOriginalElements().put(el.getName(), elCopy);
        }
        form = showForm(getFormClass(), model);
    }

    protected String getFormClass(){
        return AppMenuForm.class.getName();
    }

    protected void copyChildrenRecursive(MenuElement el, MenuElement elCopy) {
        for(MenuElement e: el.getChildren()) {
            MenuElement eCopy = new MenuElement(e);
            if(e.getChildren() != null && e.getChildren().size() > 0) {
                copyChildrenRecursive(e, eCopy);
            }
            elCopy.getChildren().add(eCopy);
        }
    }

//    private void setMenuElements(List<MenuElement> menuElements) {
//        model.setMenuElements(new ArrayList<>());
//        for (MenuElement el : menuElements) {
//            AppMenuForm.MenuElementHolder holder = new AppMenuForm.MenuElementHolder();
//            el.setChildren(filterMenuElements(model.getSearchText(), el.getChildren()));
//            holder.getElements().add(el);
//            model.getMenuElements().add(holder);
//        }
//    }

    @Action(validate = false)
    public void filter() {
        filterMenu(model.getSearchText());
    }

    public void filterMenu(String filteredName) {
        for(AppMenuForm.MenuElementHolder el: model.getMenuElements()) {
            MenuElement element =  model.getOriginalElements().get(el.getElements().get(0).getName());
            el.getElements().get(0).setChildren(filterMenuElements(filteredName,element.getChildren()));
        }
        getActiveForm().refreshView();
    }

    protected List<MenuElement> filterMenuElements(String filteredName, List<MenuElement> children) {
        List<MenuElement> menuElements = children;
        if (filteredName != null) {
            menuElements = menuElements.stream()
                    .filter(me -> me.getName() != null && me.getName().toLowerCase().contains(filteredName.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return menuElements;
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
            menuService.hide();
        }
    }

    @Override
    public String getContainerId() {
        return MENU_CONTAINER_ID;
    }

    @Override
    public void doAfterRefresh() {

    }

    @Override
    public void onSessionLanguageChange() {
//        List<MenuElement> menuElements = menuManager.getMenuModel(SessionManager.getUserSession().getSystemUser());
//        model.getMenuElements().clear();
//        model.getOriginalElements().clear();
//        for (MenuElement el : menuElements) {
//            AppMenuForm.MenuElementHolder holder = new AppMenuForm.MenuElementHolder();
//            holder.getElements().add(el);
//            model.getMenuElements().add(holder);
//            MenuElement elCopy = new MenuElement(el);
//            copyChildrenRecursive(el, elCopy);
//            model.getOriginalElements().put(el.getName(), elCopy);
//        }

        for(AppMenuForm.MenuElementHolder holder: model.getMenuElements()) {
            translateElements(holder.getElements());
        }

        form.onSessionLanguageChange(SessionManager.getUserSession().getLanguage().toLanguageTag());

//        // refresh menu structure as it cloud change
//        // hide and show form as tree is marked as static - no changes are detected
        hideForm(form);
        showForm(form);
    }

    protected void translateElements(List<MenuElement> elements) {
        if(elements == null) return;
        for(MenuElement element: elements) {
            element.setName(menuManager.translateLabel(element.getKey()));
            translateElements(element.getChildren());
        }
    }
}
