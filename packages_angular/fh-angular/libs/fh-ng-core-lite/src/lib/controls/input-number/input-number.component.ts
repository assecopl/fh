import {
  Component,
  forwardRef,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FORM_VALUE_ATTRIBUTE_NAME} from "../../models/CommonTypes";

@Component({
  selector: '[fhng-input-number]',
  templateUrl: './input-number.component.html',
  styleUrls: ['./input-number.component.scss'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => InputNumberComponent),
    },
  ],
})
export class InputNumberComponent
  extends FhngReactiveInputC
  implements OnInit, OnChanges {
  public override width = 'md-3';

  public pattern: string = 'separator';

  @Input()
  public maxFractionDigits: any = null;
  @Input()
  public maxIntigerDigits: any = null;
  @Input()
  public allowNegativeNumbers: boolean = false;
  @Input()
  public textAlign: 'LEFT' | 'CENTER' | 'RIGHT';

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.processPattern();
  }

  public processPattern() {
    if (!this.formatterName) {
      // this.customPatterns = {'N': {pattern: new RegExp("^([-]?" + integerMark + "" + separatorMark + "" + fractionMark + ")$")}};
      let pattern = 'separator';
      if (this.maxFractionDigits || this.maxFractionDigits == 0) {
        pattern = 'separator.' + this.maxFractionDigits;
      }
      this.formatter = new FhngFormatter(pattern, null);
      this.formatter.thousandSeparator = '';
      if (this.maxIntigerDigits || this.maxIntigerDigits == 0) {
        let ints = '1';
        this.maxIntigerDigits--;
        while (this.maxIntigerDigits) {
          ints = ints + '0';
          this.maxIntigerDigits--;
        }
        this.formatter.separatorLimit = ints;
      }
    }
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    this.processPattern()
  }

  public override updateModel(event) {
    this.valueChanged = true;
    this.rawValue = event.target.value;
  };


  override extractChangedAttributes() {
    let attrs = {};
    if (this.valueChanged) {
      attrs[FORM_VALUE_ATTRIBUTE_NAME] = this.rawValue;
      this.valueChanged = false;
    }

    return attrs;
  };
}

export class FhngFormatter {
  pattern: string = null;
  customPatterns = null;
  separatorLimit; //maxIntegerDIgits
  maxFractionDigits;
  thousandSeparator;

  constructor(patern = null, customPatterns = null) {
    if (patern) this.pattern = patern;
    if (customPatterns) this.customPatterns = customPatterns;
  }

  fromModel(v: any): string {
    return v;
  }

  toModel(v: any): any {
    return v;
  }

}
