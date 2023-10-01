import {
    Component, EventEmitter,
    forwardRef,
    Host,
    Injector,
    Input,
    OnInit,
    Optional,
    Output,
    SimpleChanges,
    SkipSelf
} from '@angular/core';
import {NgSelectConfig} from '@ng-select/ng-select';
import {EwopInputWithListC} from "../../models/componentClasses/EwopInputWithListC";
import {DocumentedComponent, IForm} from "@ewop/ng-core";
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {BootstrapWidthEnum} from "./../../models/enums/BootstrapWidthEnum";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.",
    icon: "fa fa-outdent"
})
@Component({
    selector: 'ewop-select-combo-menu',
    templateUrl: './select-combo-menu.component.html',
    styleUrls: ['./select-combo-menu.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => SelectComboMenuComponent)}
    ]
})
export class SelectComboMenuComponent extends EwopInputWithListC implements OnInit {

    @Input()
    public displayFunction: any;

    public model: any;

    public isOpen: boolean;

    @Input()
    public freeTyping: boolean = false;

    @Output()
    public change: EventEmitter<any> = new EventEmitter<any>();

    @Input()
    public displayExpression: string = "label";

    @Input()
    public filterFunction: (term: string, value: any) => boolean = (term: string, value: any) => this.inputFormatter(value).toLowerCase().includes(term.toLowerCase());

    public inputFormatter = (x: string) => {
        return this.resultFormatter(x);
    };

    constructor(public injector: Injector, private config: NgSelectConfig,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() iForm: IForm<any>
    ) {
        super(injector, parentEwopComponent, iForm);

        this.width = BootstrapWidthEnum.MD3;
        this.isOpen = false;
    }

    ngOnInit(): void {
        super.ngOnInit();
    }

    toggleOptionsViewOnClick(eventTarget): void {
        if (eventTarget.classList.contains('ng-arrow-wrapper') && !this.isOpen) {
            this.isOpen = true;
        } else if (eventTarget.classList.contains('ng-arrow-wrapper') && this.isOpen) {
            this.isOpen = false;
        }
    }

    toggleOptionsViewOnInput(event): void {
        if (event.term.length) {
            this.isOpen = true;
        } else {
            this.isOpen = false;
        }
    }

    closeOptionsView(): void {
        if (this.isOpen) {
            this.isOpen = false;
        }
        this.change.emit(this.value);
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}
