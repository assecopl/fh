import {
    Component,
    forwardRef, Host,
    Injector,
    OnInit, Optional,
    SkipSelf,
} from '@angular/core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {DropdownItemComponent} from "../dropdown-item/dropdown-item.component";

@Component({
    selector: 'ewop-three-dots-menu-item',
    templateUrl: '../dropdown-item/dropdown-item.component.html',
    styleUrls: ['../dropdown-item/dropdown-item.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => ThreeDotsMenuItemComponent)}
    ]
})
export class ThreeDotsMenuItemComponent extends DropdownItemComponent implements OnInit {
    constructor(public injector: Injector, @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);
    }
}
