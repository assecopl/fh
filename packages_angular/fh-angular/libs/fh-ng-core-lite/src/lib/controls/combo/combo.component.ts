import {Component, forwardRef, Host, Injector, Input, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {DocumentedComponent, IForm} from '@fhng/ng-core';
import {NgSelectConfig} from '@ng-select/ng-select';
import {FhngInputWithListC} from '../../models/componentClasses/FhngInputWithListC';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';

@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value:
    'Enables users to quickly find and select from a pre-populated list of values as they type, leveraging searching and filtering.',
  icon: 'fa fa-outdent',
})
@Component({
  selector: 'fhng-combo',
  templateUrl: './combo.component.html',
  styleUrls: ['./combo.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => ComboComponent)},
  ],
})
export class ComboComponent extends FhngInputWithListC implements OnInit {
  constructor(
    public injector: Injector,
    private config: NgSelectConfig,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @Host() @SkipSelf() iForm: IForm<any>
  ) {
    super(injector, parentFhngComponent, iForm);

    this.width = BootstrapWidthEnum.MD3;
  }

  //@Input()
  //public label:string;
  //
  //@Input('values')
  //public values: Array<any> = [];

  public model: any;

  @Input('multiselect')
  public multiple: boolean = false;

  @Input()
  public displayFunction: any = null;

  @Input()
  public displayExpression: any = 'lastName'; // TODO Convertery i formatter ??

  //@Input('formatter')
  //public resultFormatter = (x: string | {name: string}) => {
  //  if(typeof x === 'string'){
  //    return x
  //  } else {
  //    return x[this.displayExpresion? this.displayExpresion: "name"]
  //  }
  //};

  @Input('filterFunction')
  public filterFunction: (term: string, value: any) => boolean = (
    term: string,
    value: any
  ) => this.inputFormatter(value)?.toLowerCase().includes(term?.toLowerCase());

  //@Input('formatter')
  public inputFormatter = (x: string | { label: string }) => {
    if (typeof x === 'string') {
      return x;
    } else if (this.displayFunction) {
      return this.displayFunction(x);
    } else {
      return x[this.displayExpression ? this.displayExpression : 'label'];
    }
  };

  ngOnInit() {
    super.ngOnInit();
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
