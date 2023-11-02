import {Component, forwardRef, Injector, Input, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {NgSelectConfig} from '@ng-select/ng-select';
import {FhngInputWithListC} from '../../models/componentClasses/FhngInputWithListC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';

@Component({
  selector: 'fhng-combo',
  templateUrl: './combo.component.html',
  styleUrls: ['./combo.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => ComboComponent)},
  ],
})
export class ComboComponent extends FhngInputWithListC implements OnInit {
  constructor(
    public override injector: Injector,
    private config: NgSelectConfig,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD3;
  }

  @Input()
  public filteredValues: { [key: string]: [] } = null;


  public cursor: number = 0;
  //@Input()
  //public label:string;
  //
  //@Input('values')
  //public values: Array<any> = [];

  public model: any;

  @Input('multiselect')
  public multiple: boolean = false;

  @Input()
  public override displayFunction: any = null;

  @Input()
  public override displayExpression: any = 'lastName'; // TODO Convertery i formatter ??

  //@Input('formatter')
  //public resultFormatter = (x: string | {name: string}) => {
  //  if(typeof x === 'string'){
  //    return x
  //  } else {
  //    return x[this.displayExpresion? this.displayExpresion: "name"]
  //  }
  //};

  public getValuesForCursor(): [] {
    let values: [] = [];
    const keys = Object.keys(this.filteredValues);
    keys.forEach((value, index) => {
      if (index == this.cursor) {
        values = this.filteredValues[value];
      }
    })


    return values
  }

  @Input('filterFunction')
  public filterFunction: (term: string, value: any) => boolean = (
    term: string,
    value: any
  ) => this.inputFormatter(value)?.toLowerCase().includes(term?.toLowerCase());

  //@Input('formatter')
  public inputFormatter = (x: {
    displayAsTarget: boolean,
    targetValue: string,
    targetId: number,
    displayedValue: string
  }) => {

    return x.displayAsTarget ? x.targetValue : x.displayedValue;
    ;

  };

  override ngOnInit() {
    super.ngOnInit();
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
