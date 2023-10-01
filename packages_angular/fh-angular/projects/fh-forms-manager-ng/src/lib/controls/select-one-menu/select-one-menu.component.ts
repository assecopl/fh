import {Component, forwardRef, Host, Injector, OnInit, Optional, SimpleChanges, SkipSelf} from '@angular/core';
import {DocumentedComponent, IForm} from '@ewop/ng-core';
import {EwopInputWithListC} from '../../models/componentClasses/EwopInputWithListC';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';

@Component({
    selector: 'ewop-select-one-menu',
    templateUrl: './select-one-menu.component.html',
    styleUrls: ['./select-one-menu.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => SelectOneMenuComponent)}
    ]
})
@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Component responsible for displaying list of values with possibility of selecting only one value.",
    icon: "fa fa-caret-down"
})
export class SelectOneMenuComponent extends EwopInputWithListC implements OnInit {

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() iForm: IForm<any>
    ) {
        super(injector, parentEwopComponent, iForm);
        this.width = BootstrapWidthEnum.MD3;
    }

    ngOnInit() {
        super.ngOnInit();


    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}
