import {Component, forwardRef, Host, Injector, Input, OnInit, Optional, SimpleChanges, SkipSelf} from '@angular/core';
import {DocumentedComponent} from "@ewop/ng-core";
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";

@DocumentedComponent({
    category: DocumentedComponent.Category.ARRANGEMENT,
    value: "Component used to create spaces in application layout",
    icon: "fas fa-arrows-alt-h"
})
@Component({
    selector: 'ewop-spacer',
    templateUrl: './spacer.component.html',
    styleUrls: ['./spacer.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => SpacerComponent)}
    ]
})
export class SpacerComponent extends EwopHTMLElementC implements OnInit {

    public width: string = 'md-2';

    @Input() verticalSpace: string;

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);
    }

    ngOnInit() {
        super.ngOnInit();
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }


}
