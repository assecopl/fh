import {ComponentManager} from "./service/component-manager.service";
import {EventsManager} from "./service/events-manager.service";
import {CustomActionsManager} from "./service/custom-actions-manager.service";

abstract class FhNgModule {
    /**
     * Z uwagi na czyszczenie atrybutów modół musi przekazać odpowiednie Serwisy/Manager-y
     * aby można było zarejestrować elementy składowe aplikacji
     * @param componentManager
     * @param eventManager
     * @param cutomActionsManager
     */
    constructor(componentManager?: ComponentManager, eventManager?: EventsManager, customActionsManager?: CustomActionsManager) {
        this.init(componentManager, eventManager, customActionsManager);
    }

    public init(componentManager?: ComponentManager, eventManager?: EventsManager, customActionsManager?: CustomActionsManager) {
        if (componentManager) this.registerComponents(componentManager);
        if (eventManager) this.registerEvents(eventManager);
        if (customActionsManager) this.registerCustomActions(customActionsManager);
    }

    protected abstract registerComponents(componentManager?: ComponentManager);

    protected abstract registerEvents(eventManager?: EventsManager);

    protected abstract registerCustomActions(customActionsManager?: CustomActionsManager);
}

export {FhNgModule}
