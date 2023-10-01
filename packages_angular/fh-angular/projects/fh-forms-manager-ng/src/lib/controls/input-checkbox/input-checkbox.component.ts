import {
    Component,
    EventEmitter,
    forwardRef, Host, HostBinding,
    Injector,
    Input,
    OnInit,
    Optional,
    Output,
    SimpleChanges, SkipSelf
} from '@angular/core';
import {EwopReactiveInputC} from '../../models/componentClasses/EwopReactiveInputC';
import {DocumentedComponent, IForm} from "@ewop/ng-core";
import {InputTypeEnum} from "../../models/enums/InputTypeEnum";
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {BootstrapWidthEnum} from "./../../models/enums/BootstrapWidthEnum";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Checkbox allows users select many options from a predefined list of choices",
    icon: "fa fa-check-square"
})
@Component({
    selector: 'ewop-input-checkbox',
    templateUrl: './input-checkbox.component.html',
    styleUrls: ['./input-checkbox.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => InputCheckboxComponent)}
    ]
})
export class InputCheckboxComponent extends EwopReactiveInputC implements OnInit {

    // @HostBinding('class.form-check')
    // formCheck: boolean = true;

    @Input()
    public checked: boolean = false;

    @Output()
    public selectedCheckbox: EventEmitter<InputCheckboxComponent> = new EventEmitter<InputCheckboxComponent>();

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() iForm: IForm<any>
    ) {
        super(injector, parentEwopComponent, iForm);
        this.labelPosition = "RIGHT";
    }

    public width: string = BootstrapWidthEnum.MD12;


    //public class:string = "";

    ngOnInit() {
        super.ngOnInit();

        this.inputType = InputTypeEnum.checkbox;
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}
