import {
    Component,
    EventEmitter,
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
import {EwopReactiveInputC} from '../../models/componentClasses/EwopReactiveInputC';
import {DocumentedComponent, EwopValidators} from '@ewop/ng-core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {BootstrapWidthEnum} from "./../../models/enums/BootstrapWidthEnum";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {FormComponent} from "../form/form.component";
import {ValidatorFn} from "@angular/forms";

@DocumentedComponent({
    category: DocumentedComponent.Category.INPUTS_AND_VALIDATION, value: "FileUpload component is responsible for displaying simple field, where user can write some data" +
        " plus label representing this field.", icon: "fa fa-edit"
})
@Component({
    selector: 'ewop-file-upload',
    templateUrl: './file-upload.component.html',
    styleUrls: ['./file-upload.component.css'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => FileUploadComponent)}
    ]
})
export class FileUploadComponent extends EwopReactiveInputC implements OnInit {

    public width = BootstrapWidthEnum.MD3;

    @Input()
    public updateOn: 'change' | 'blur' | 'submit' = "change"; //Domyślnie upload plików musi aktualizować/odpisywać się od razu.


    //Used for static values mappings
    //@Override
    @Input('file')
    public value: any = null;

    @Output('fileChange')
    public valueChange: EventEmitter<any> = new EventEmitter<any>();

    @Input()
    public maxSize: number = 1000000; //KB

    @Input()
    public extensions: string = null;

    @Input()
    public multiple: boolean = false;

    @Output()
    public upload: EventEmitter<File> = new EventEmitter<File>();

    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                @Optional() @Host() @SkipSelf() iForm: FormComponent) {
        super(injector, parentEwopComponent, iForm);
    }

    ngOnInit() {
        super.ngOnInit();
    }

    change(e: any) {
        if (!this.control.errors && this.control.value) {
            this.upload.emit(this.control.value);
        }
    }

    ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }


    getValidationLabel(): string {
        return super.getValidationLabel();
    }

    addCustomValidators(): ValidatorFn[] {
        return [EwopValidators.fileSizeValidator(this.maxSize), EwopValidators.requiredFileType(this.extensions ? this.extensions.split(',') : null)]
    }


}

