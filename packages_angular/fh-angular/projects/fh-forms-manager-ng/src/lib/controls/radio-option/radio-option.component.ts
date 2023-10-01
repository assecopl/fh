import {
    Component,
    EventEmitter,
    forwardRef, Host,
    Injector,
    Input,
    OnInit,
    Optional,
    Output,
    SimpleChanges, SkipSelf
} from '@angular/core';
import {DocumentedComponent, IForm} from "@ewop/ng-core";
import {EwopReactiveInputC} from '../../models/componentClasses/EwopReactiveInputC';
import {InputTypeEnum} from "../../models/enums/InputTypeEnum";
import {FormBuilder} from "@angular/forms";
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {FormComponent} from '../form/form.component';

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Radio Option represents a single radio component",
    icon: "fa fa-circle"
})
@Component({
    selector: 'ewop-radio-option',
    templateUrl: './radio-option.component.html',
    styleUrls: ['./radio-option.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => RadioOptionComponent)}
    ]
})
export class RadioOptionComponent extends EwopReactiveInputC implements OnInit {

    @Input()
    public checked: boolean = false;

    @Input()
    public name: string;

    @Input()
    public targetValue: any;

    @Output()
    public selectedRadio: EventEmitter<RadioOptionComponent> = new EventEmitter<RadioOptionComponent>();

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() iForm: FormComponent
    ) {
        super(injector, parentEwopComponent, iForm);
    }

    ngOnInit() {
        super.ngOnInit();

        this.inputType = InputTypeEnum.radio;

        this.processDisabledOptions();
    }

    public processDisabledOptions() {
        this.disabled = String(this.disabled) === 'true';
    }

    updateControlValue(event): void {
        this.control.setValue(this.targetValue, {onlySelf: true, emitEvent: false})
        this.selectedRadio.emit(this);
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}
