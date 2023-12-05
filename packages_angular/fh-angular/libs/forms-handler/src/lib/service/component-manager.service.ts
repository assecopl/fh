import {inject, Injectable, Type} from '@angular/core';
import {NotificationService} from "./Notification";
import {FhngComponent} from "../models/componentClasses/FhngComponent";
import {FHNG_CORE_CONFIG, FhNgCoreConfig} from "../fh-ng-core.config";

declare var contextRoot: string;
declare var fhBaseUrl: string;
declare const $: any;

@Injectable({
    providedIn: 'root',
})
class ComponentManager {
    protected configuration: FhNgCoreConfig = inject(FHNG_CORE_CONFIG);
    private notificationService: NotificationService = inject(NotificationService);


    protected components: {
        [index: string]: Type<FhngComponent | any>;
    } = {}

    constructor() {

    }

    public registerComponent(component: Type<FhngComponent> | any): void {
        this.components[component.name.replace("Component", "")] = component;
    }

    public registerComponentWithName(name: string, component: Type<FhngComponent> | any): void {
        this.components[name] = component;
    }

    public getComponentFactory(type: string): Type<FhngComponent> {

        if (this.components[type]) {
            return this.components[type];
        } else {
            if (this.configuration.development && !this.configuration.debug) {
                this.notificationService.showWarning("Component " + type + " is not registered.");
            }
            return null;
        }
    }


}

export {ComponentManager};


