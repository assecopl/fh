import {Component, forwardRef, HostBinding, Injector, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {EwopComponent} from '../../models/componentClasses/EwopComponent';

@Component({
  selector: 'fh-form',
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
export class FormComponent extends EwopComponent implements OnInit, OnChanges {


    // @Input() public model: any = null;
    @Input() public label: string = null;
    @Input() public hideHeader: boolean = false;
  @Input() public formType: string = "STANDARD";

  @HostBinding('class.form-header')
  header: boolean = false;

  constructor(public override injector: Injector) {
    super(injector, null)
  }

  override ngOnInit() {
    super.ngOnInit();
    if (this.formType == "HEADER") {
      this.header = true;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.formType == "HEADER") {
      this.header = true;
    }
    }


}
