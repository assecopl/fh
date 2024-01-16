import {Directive, Injector, Input, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngReactiveInputC} from './FhngReactiveInputC';
import {compile, eval as expEval, parse} from 'expression-eval';
import {FhngComponent} from "@fh-ng/forms-handler";

@Directive()
export class FhngInputWithListC extends FhngReactiveInputC {
  @Input()
  public values: Array<any> = [];

  @Input('values')
  public set _values(value: Array<any> | string | String | Map<any, any>) {
    if (value instanceof String || typeof value === 'string') {
      this.processStringToValues(value);
    } else if (value instanceof Map) {
      this.values = ['MultiValueMap not supported yet'];
    } else {
      this.values = value ? value : [];
    }
  }

  //Defines if value passed can be empty
  @Input()
  public emptyValue: boolean = false;

  //Determines if empty value should be displayed on list of options.
  @Input()
  public emptyLabel: boolean = false;


  //Defines an attribute of a single value object to be used for display. If not set, converter will be used.
  @Input()
  public displayExpression: string = 'name';
  private displayExpressionParsed: any = null;

  @Input()
  public displayFunction: any = null;

  @Input('displayExpressionFunction')
  public resultFormatter = (x: string | { name: string }) => {
    if (!x) {
      return '';
    }

    if (typeof x === 'string') {
      return x;
    } else {
      if (this.displayFunction) {
        return this.displayFunction(x);
      } else {
        return expEval(this.displayExpressionParsed, x);
      }
    }
  };

  processStringToValues(_values: string | String) {
    this.values = _values.split('|');
  }

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  public override ngOnInit() {
    super.ngOnInit();
    this.prepareDisplayExpresion();
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (changes['displayExpression']) {
      this.prepareDisplayExpresion();
    }
  }

  public prepareDisplayExpresion() {
    if (this.displayExpression) {
      const de = this.displayExpression
        .replace(/{/g, '')
        .replace(/}/g, '')
        .replace(new RegExp(/\\/g), '');
      this.displayExpressionParsed = parse(de);
      this.displayFunction = compile(de);
    }
  }

}
