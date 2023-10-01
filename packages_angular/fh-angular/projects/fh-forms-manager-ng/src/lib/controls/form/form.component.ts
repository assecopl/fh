import {Component, forwardRef, Input} from '@angular/core';
import {EwopComponent} from '../../models/componentClasses/EwopComponent';

@Component({
    selector: 'ewop-form',
    templateUrl: './form.component.html',
    styleUrls: ['./form.component.scss'],
    providers: [
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => FormComponent)}
    ]
})
export class FormComponent extends EwopComponent {


    // @Input() public model: any = null;
    @Input() public label: string = null;
    @Input() public hideHeader: boolean = false;

    constructor() {
        super(null)
    }

}
