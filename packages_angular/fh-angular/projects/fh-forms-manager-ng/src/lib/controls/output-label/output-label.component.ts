import {
    Component, EventEmitter,
    forwardRef,
    Host,
    HostBinding, HostListener,
    Injector,
    Input,
    OnInit,
    Optional, Output,
    SimpleChanges,
    SkipSelf
} from '@angular/core';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {DocumentedComponent, EwopML} from '@ewop/ng-core';
import {IconAligmentType} from '../../models/CommonTypes';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Label component is responsible for displaying value.",
    icon: "fa fa-font"
})
@Component({
    selector: 'ewop-output-label',
    templateUrl: './output-label.component.html',
    styleUrls: ['./output-label.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => OutputLabelComponent)}
    ],
})
export class OutputLabelComponent extends EwopHTMLElementC implements OnInit {

    public width: string = BootstrapWidthEnum.MD2;

    @Input()
    public value: any;

    //TODO Move Icon to mixin class ??
    @Input()
    public icon: string;

    @Input()
    public iconAlignment: IconAligmentType = "BEFORE";

    @HostBinding('class.justify-content-end')
    justifyContentRight: boolean = false;

    @HostBinding('class.justify-content-center')
    justifyContentCenter: boolean = false;

    @Input()
    public headingType: "H1" | "H2" | "H3" | "H4" | "H5" | "H6" | "Default" | "Auto" = "Default";

    @Output()
    public click: EventEmitter<any> = new EventEmitter<any>();

    @HostBinding('attr.tabindex')
    public tabindex: number = null;

    @HostListener('keydown.enter', ['$event']) onEnterHandler(event: KeyboardEvent) {
        if (this.click?.observers.length > 0) {
            event.stopPropagation();
            event.preventDefault();
            this.click.emit(event);

        }
    }


    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent)
    }

    ngOnInit() {
        super.ngOnInit();

        let ewopml = this.injector.get(EwopML);

        this.processTextAlign();
        if (this.click?.observers.length > 0) {
            this.tabindex = 0;
        }
    }

    processTextAlign() {
        switch (this.horizontalAlign) {
            case 'RIGHT':
                this.justifyContentRight = true;
                this.justifyContentCenter = false;
                break;
            case 'CENTER':
                this.justifyContentCenter = true;
                this.justifyContentRight = false;
                break;
            case "LEFT":
            default:
                this.justifyContentRight = false;
                this.justifyContentCenter = false;
                break;
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}
