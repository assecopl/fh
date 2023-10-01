import {
    Component, ContentChildren,
    forwardRef, Host,
    Injector, OnInit,
    Optional, QueryList, SkipSelf,
} from '@angular/core';
import {DocumentedComponent} from "@ewop/ng-core";
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {DropdownComponent} from "../dropdown/dropdown.component";
import {ThreeDotsMenuItemComponent} from "../three-dots-menu-item/three-dots-menu-item.component";
import {BootstrapStyleEnum} from "../../models/enums/BootstrapStyleEnum";

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Button component responsible for the grouping of ThreeDotsMenuItems in a table row.",
    icon: "fas fa-ellipsis-v",
})
@Component({
    selector: 'ewop-three-dots-menu',
    templateUrl: './three-dots-menu.component.html',
    styles: [`
    :host-context(.table-primary) .dropdown-toggle ,
    :host-context(.bg-primary) .dropdown-toggle {
      color: white !important;
    }
  `],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => ThreeDotsMenuComponent)}
    ]
})
export class ThreeDotsMenuComponent extends DropdownComponent implements OnInit {
    @ContentChildren(ThreeDotsMenuItemComponent)
    public subcomponents: QueryList<ThreeDotsMenuItemComponent> = new QueryList<ThreeDotsMenuItemComponent>();

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);
        this.bootstrapStyle = BootstrapStyleEnum.LINK;
        this.label = "[icon='fas fa-ellipsis-v']";
    }
}
