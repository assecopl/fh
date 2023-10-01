import {
    AfterContentInit,
    Component, forwardRef, Host,
    HostBinding,
    Injector,
    Input, OnChanges,
    OnInit, Optional,
    SimpleChanges, SkipSelf
} from '@angular/core';
import {DocumentedComponent} from "@ewop/ng-core";
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {EwopHTMLElementC} from "../../models/componentClasses/EwopHTMLElementC";

@DocumentedComponent({
    category: DocumentedComponent.Category.OTHERS,
    value: "Tab represents a single tab component",
    icon: "fa-fw fa fa-window-maximize"
})
@Component({
    selector: 'ewop-tab',
    templateUrl: './tab.component.html',
    styleUrls: ['./tab.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => TabComponent)}
    ]
})
export class TabComponent extends EwopHTMLElementC implements OnInit, OnChanges, AfterContentInit {
    //FIXME Why tabComponent is GroupingComponentC - it is wrong


    @Input()
    active: boolean = false;

    @HostBinding('id')
    tabId: string;

    constructor(public injector: Injector, @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);

        this.width = '';
        this.active = false;
    }

    ngOnInit() {
        super.ngOnInit();
        if (this.id) {
            this.tabId = this.id;
        } else {
            this.tabId = this.constructor.name + '_' + ((Math.random() * 100000000)).toFixed();
        }
    }

    public ngAfterContentInit(): void {
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}
