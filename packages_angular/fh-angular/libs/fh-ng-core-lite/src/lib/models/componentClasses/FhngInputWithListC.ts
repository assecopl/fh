import {Directive, Host, Injector, Input, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngReactiveInputC} from './FhngReactiveInputC';
import {FhngComponent} from './FhngComponent';
import {IForm} from '@fhng/ng-core';
import {compile, eval as expEval, parse} from 'expression-eval';

type Constructor<T> = new (...args: any[]) => T;

/**
 * TS Representation of BaseInputListField.java
 * @param Base
 * @constructor
 */
// export function InputListFieldM<T extends Constructor<{}>>(Base: T) {

// @Directive()
// class Temporary extends Base {
//     public values: Array<any> = [];
//
//     //FIXME There is problem with AOT compiltion with decorators on mixins. Template type check log erros as they cant see this attributes
//     @Input('values')
//     public set _values(value:Array<any> | string | String | Map<any,Array<any>>){
//       if(value instanceof String || typeof value === 'string'){
//         this.processStringToValues(value);
//       } else if(value instanceof Map) {
//         this.values = ["MultiValueMap not supported yet"]
//       } else {
//         this.values = value? value : []
//       }
//     }
//
//     //Defines if value passed can be empty
//     @Input()
//     public emptyValue:boolean = false;
//
//     //Determines if empty value should be displayed on list of options.
//     public emptyLabel:boolean = false;
//
//
//     //Defines an attribute of a single value object to be used for display. If not set, converter will be used.
//     @Input('displayAttribute')
//     public displayExpression:string = null;
//
//
//     @Input('displayExpressionFunction')
//     public resultFormatter = (x: string | {name: string}) => {
//       if(!x){
//         return "";
//       }
//
//       if(typeof x === 'string'){
//         return x
//       } else {
//         return x[this.displayExpression? this.displayExpression: "name"]
//       }
//     };
//
//      processStringToValues(_values:string | String){
//       this.values = _values.split("|");
//
//     }
//
//     constructor(...args: any[]) {
//       super(...args);
//     }
//   }
//
//   return Temporary
// }

// export namespace InputListFieldN
// {
//   export const InputListFieldMInputs = [ 'values'];
//
// }

@Directive()
export class FhngInputWithListC extends FhngReactiveInputC {
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
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @Host() @SkipSelf() iForm: IForm<any>
  ) {
    super(injector, parentFhngComponent, iForm);
  }

  ngOnInit() {
    super.ngOnInit();
    this.prepareDisplayExpresion();
  }

  ngOnChanges(changes: SimpleChanges) {
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
