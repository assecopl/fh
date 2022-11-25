package pl.fhframework.menu;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.core.util.FhmlUtils;
import pl.fhframework.model.forms.AvailabilityEnum;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by mateusz.zaremba
 */
@Getter
@Setter
public class MenuElement {

    private String name;
    private String key;
    private String ref;
    private boolean groupingElement;
    private String icon;
    private List<MenuElement> children;
    private AtomicBoolean activityToken;
    private String inputFactory;
    private AvailabilityEnum availability = AvailabilityEnum.EDIT;

    public MenuElement(String name, String key, String ref, String icon, boolean isGroupingElement, AtomicBoolean activityToken, String inputFactory) {
        this.name = name;
        this.key = key;
        this.ref = ref;
        this.icon = icon;
        this.groupingElement = isGroupingElement;
        this.activityToken = activityToken;
        this.inputFactory = inputFactory;
        children = new LinkedList<>();
    }

    public MenuElement(MenuElement me) {
        this.name = me.name;
        this.ref = me.ref;
        this.icon = me.icon;
        this.groupingElement = me.groupingElement;
        this.activityToken = me.activityToken;
        this.inputFactory = me.inputFactory;
        children = new LinkedList<>();
    }

    public boolean isActive() {
        return activityToken == null || activityToken.get();
    }

    public String getDecoratedName() {
        String decoratedName = name;
        if (!isActive()) {
            decoratedName = FhmlUtils.color(decoratedName, "silver");
        }
        UserSession session = SessionManager.getUserSession();
        if (ref != null && session != null && session.getUseCaseContainer().isUseCaseRunning(ref, false)) {
            decoratedName = FhmlUtils.bold(decoratedName);
        }
        return decoratedName;
    }
}
