import {Component, forwardRef, HostBinding, Injector, Input, OnChanges, OnInit, SimpleChanges, ElementRef} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@Component({
  selector: 'fh-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => FormComponent)},
  ],
})
export class FormComponent extends FhngComponent implements OnInit, OnChanges {
  // @Input() public model: any = null;
  @Input() public label: string = null;
  @Input() public hideHeader: boolean = false;
  @Input() public formType: string = 'STANDARD';

  @HostBinding('class.form-header')
  header: boolean = false;

  constructor(
    public override injector: Injector,
    private _elementRef: ElementRef<HTMLElement>
  ) {
    super(injector, null);
  }

  override ngOnInit() {
    super.ngOnInit();
    if (this.formType == 'HEADER') {
      this.header = true;
    }

    this._elementRef.nativeElement.classList.add('fc');
    this._elementRef.nativeElement.classList.add('tree');
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.formType == 'HEADER') {
      this.header = true;
    }
  }
}
