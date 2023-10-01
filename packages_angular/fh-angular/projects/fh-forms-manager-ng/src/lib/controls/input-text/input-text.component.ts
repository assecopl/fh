import {
    Component,
    ElementRef,
    forwardRef,
    Host,
    Injector,
    Input,
    OnInit,
    Optional,
    SimpleChanges,
    SkipSelf,
    ViewChild
} from '@angular/core';
import {InputTypeEnum} from '../../models/enums/InputTypeEnum';
import {EwopReactiveInputC} from '../../models/componentClasses/EwopReactiveInputC';
import {DocumentedComponent, EwopFormatter} from '@ewop/ng-core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {BootstrapWidthEnum} from "./../../models/enums/BootstrapWidthEnum";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {FormComponent} from '../form/form.component';

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
    value: "InputText component is responsible for displaying simple field, where user can write some data" +
        " plus label representing this field.",
    icon: "fa fa-edit"
})
@Component({
    selector: 'ewop-input-text',
    templateUrl: './input-text.component.html',
    styleUrls: ['./input-text.component.css'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => InputTextComponent)}
    ]
})
export class InputTextComponent extends EwopReactiveInputC implements OnInit {

    public width = BootstrapWidthEnum.MD3;

    @Input('mask')
    public pattern: string = null;

    @Input()
    public rowsCount: number = 1;

    @Input()
    public maskDynamic: boolean = false;

    @Input()
    public requiredRegexMessage: string;

    @Input()
    public rowsCountAuto: boolean = false;

    @Input()
    public resize: string = 'none';

    @ViewChild('inputRef', {static: false}) inputRef: ElementRef = null;

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() iForm: FormComponent
    ) {
        super(injector, parentEwopComponent, iForm);
    }

    ngOnInit() {
        super.ngOnInit();
        if (this.height || this.rowsCount > 1 || this.rowsCountAuto) {
            this.inputType = InputTypeEnum.textarea;
        }

        if (this.pattern) {
            // this.pattern = this.pattern.replace("M", "G");
            this.formatter = new EwopFormatter(this.pattern, null)
        }

    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }

}

