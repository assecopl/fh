import {
    Component,
    EventEmitter, forwardRef, Host,
    Injector,
    Input,
    OnInit, Optional,
    Output, SimpleChanges, SkipSelf,
} from '@angular/core';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {IconAligmentType} from "../../models/CommonTypes";
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";

@Component({
    selector: 'ewop-dropdown-item',
    templateUrl: './dropdown-item.component.html',
    styleUrls: ['./dropdown-item.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => DropdownItemComponent)}
    ]
})
export class DropdownItemComponent extends EwopHTMLElementC implements OnInit {

    @Input()
    public icon: string;

    @Input()
    public iconAlignment: IconAligmentType;

    @Output()
    public selectedButton: EventEmitter<DropdownItemComponent> = new EventEmitter<DropdownItemComponent>();

    @Input()
    public url: string;

    mb3 = false;

    constructor(public injector: Injector, @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);
    }

    ngOnInit() {
        super.ngOnInit();
    }

    onClick() {
        if (this.url) {
            window.location.href = this.url;
        }
        this.selectedButton.emit(this);
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }
}
