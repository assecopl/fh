import {
  AfterViewInit,
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
import {FhngComponent, FhngFormatter, FORM_VALUE_ATTRIBUTE_NAME, IDataAttributes} from "@fh-ng/forms-handler";

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
export class InputTextComponent extends FhngReactiveInputC implements OnInit, AfterViewInit {
  public override width = BootstrapWidthEnum.MD3;

  // @Input('mask')
  public pattern: string = null;

  @Input()
  public set mask(value: string) {
    if (value) {
      this.formatter = new FhngFormatter(value, null);
    }
  }

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


  // protected keySupport: FormComponentKeySupport;
  // private readonly isTextarea: boolean;
  public textareaAutosize: boolean;
  protected keySupportCallback: any;
  private readonly maskDefinition: string;
  private lastValidMaskedValue: any;
  protected readonly textAlign: string;
  protected format: string;
  protected emptyValue: any;
  protected onEmptyValue: any;
  private timeoutFunction: any;
  private readonly inputTimeout: number;
  protected inputmaskEnabled: boolean;
  protected maskPlugin: any;
  protected maskInsertMode: boolean;
  public input: any;



  @ViewChild('inputRef', {static: false}) inputRef: ElementRef = null;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  override ngOnInit() {
    super.ngOnInit();
    this.inputType = InputTypeEnum.text;
    if (this.height || this.rowsCount > 1 || this.rowsCountAuto) {
      this.inputType = InputTypeEnum.textarea;
    }

  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    console.log("-------changes----------", this.id, changes);
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

  override mapAttributes(data: IDataAttributes | any) {
    super.mapAttributes(data);
    this.mask = data.mask
    this.rawValue = data.rawValue || null;
  }

  override ngAfterViewInit(): void {
    super.ngAfterViewInit();

  }

}
