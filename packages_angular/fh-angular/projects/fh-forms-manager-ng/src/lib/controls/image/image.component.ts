import {
    Component,
    forwardRef,
    Host,
    Injector,
    Input,
    OnInit,
    Optional,
    SimpleChanges,
    SkipSelf
} from '@angular/core';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {DocumentedComponent} from '@ewop/ng-core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Image component is responsible for displaying images.",
    icon: "fa fa-font"
})
@Component({
    selector: 'ewop-image',
    templateUrl: './image.component.html',
    styleUrls: ['./image.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => ImageComponent)}
    ],
})
export class ImageComponent extends EwopHTMLElementC implements OnInit {

    public width: string = BootstrapWidthEnum.MD2;

    @Input()
    public src: any;
    @Input()
    public alt: any;

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent)
    }

    ngOnInit() {
        super.ngOnInit();
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }
}
