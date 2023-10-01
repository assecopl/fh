import {
    Component,
    forwardRef,
    Host,
    Injector,
    Input,
    OnChanges,
    OnInit,
    Optional,
    SimpleChanges,
    SkipSelf
} from '@angular/core';
import {EwopReactiveInputC} from '../../models/componentClasses/EwopReactiveInputC';
import {DocumentedComponent, EwopFormatter} from '@ewop/ng-core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {FormComponent} from "../form/form.component";

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "Component responsible for displaying field, where use can set only number.", icon: "fa fa-edit"
})
@Component({
    selector: 'ewop-input-number',
    templateUrl: './input-number.component.html',
    styleUrls: ['./input-number.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => InputNumberComponent)}
    ],
})
export class InputNumberComponent extends EwopReactiveInputC implements OnInit, OnChanges {

    public width = 'md-3';

    public pattern: string = "separator";

    @Input()
    public maxFractionDigits: any = null;
    @Input()
    public maxIntigerDigits: any = null;
    @Input()
    public allowNegativeNumbers: boolean = false;
    @Input()
    public textAlign: 'LEFT' | 'CENTER' | 'RIGHT';

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() iForm: FormComponent
    ) {
        super(injector, parentEwopComponent, iForm);
    }

    ngOnInit() {
        super.ngOnInit();
        this.processPattern();

    }

    public processPattern() {
        if (!this.formatterName) {
            // this.customPatterns = {'N': {pattern: new RegExp("^([-]?" + integerMark + "" + separatorMark + "" + fractionMark + ")$")}};
            let pattern = "separator"
            if (this.maxFractionDigits) {
                pattern = "separator." + this.maxFractionDigits
            }
            this.formatter = new EwopFormatter(pattern, null);
            this.formatter.thousandSeparator = "";
            if (this.maxIntigerDigits) {
                let ints = "1";
                this.maxIntigerDigits--;
                while (this.maxIntigerDigits) {
                    ints = ints + "0";
                    this.maxIntigerDigits--;
                }
                this.formatter.separatorLimit = ints;
            }

        }
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}
