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
  ViewChild,
} from '@angular/core';
import {InputTypeEnum} from '../../models/enums/InputTypeEnum';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@Component({
  selector: '[fhng-input-text]',
  templateUrl: './input-text.component.html',
  styleUrls: ['./input-text.component.css'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => InputTextComponent),
    },
  ],
})
export class InputTextComponent extends FhngReactiveInputC implements OnInit {
  public override width = BootstrapWidthEnum.MD3;

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

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  override ngOnInit() {
    super.ngOnInit();
    if (this.height || this.rowsCount > 1 || this.rowsCountAuto) {
      this.inputType = InputTypeEnum.textarea;
    }

  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
