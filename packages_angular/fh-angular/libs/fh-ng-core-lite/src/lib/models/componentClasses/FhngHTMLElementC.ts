import {
  AfterContentInit,
  AfterViewInit,
  Directive,
  ElementRef,
  EventEmitter,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
  ViewChild,
} from '@angular/core';
import {FhngComponent} from './FhngComponent';
import {SafeStyle} from '@angular/platform-browser';
import {FhMLService} from '../../service/fh-ml.service';
import {IDataAttributes} from "../interfaces/IDataAttributes";
import {IconAligmentType} from "../CommonTypes";
import {STYLE_UNIT} from "../enums/StyleUnitEnum";
import {AvailabilityEnum} from "../../availability/enums/AvailabilityEnum";
import {AvailabilityUtils} from "../../availability/AvailabilityUtils";
import {FhngFormatter} from "../../controls/input-number/input-number.component";

@Directive()
export class FhngHTMLElementC
  extends FhngComponent
  implements OnInit, AfterViewInit, AfterContentInit, OnChanges {

  public static STYLE_UNIT: STYLE_UNIT = STYLE_UNIT.PX;

  @HostBinding('attr.tabindex')
  public tabindex: number = null;


  public availability: AvailabilityEnum = AvailabilityEnum.EDIT;

  @Input('availability')
  public set accessibility(value: AvailabilityEnum | string) {
    this.availability = AvailabilityUtils.stringToEnum(value);
  }

  public get accessibility() {
    return this.availability;
  }

  /**
   * Parametr width jest domyśłnym parametrem komponentu angularowego, przychwytujemy go aby obsłużyć
   * logikę ustawiania klas typowych dla bootstrapa.
   */
  @Input()
  public width: string;

  @Input()
  public rawValue: any = null;

  public height: string = null;

  @HostBinding('class')
  @Input()
  public hostWidth: string = '';

  // @Input('width')
  // public set width(value: string) {
  //   this.processWidth(value, true);
  // }


  @Input()
  public bootstrapStyle: any = null;

  @Input()
  public hiddenElementsTakeUpSpace: boolean = false;

  @Input()
  public icon: string = null;

  @Input()
  public iconAlignment: IconAligmentType;

  public elementRef: ElementRef;

  @Input()
  public label: string = null;

  @Input()
  public labelPosition: 'UP' | 'DOWN' | 'LEFT' | 'RIGHT' = 'UP';

  @Input()
  public horizontalAlign: 'LEFT' | 'CENTER' | 'RIGHT';

  @HostBinding('class.align-self-left')
  public alignLeft: boolean;

  @HostBinding('class.text-center')
  @HostBinding('class.align-self-center')
  public alignCenter: boolean;

  @HostBinding('class.text-right')
  @HostBinding('class.align-self-right')
  public alignRight: boolean;

  @Input()
  public verticalAlign: 'TOP' | 'MIDDLE' | 'BOTTOM';

  @HostBinding('class.align-self-start')
  public valignTop: boolean;

  @HostBinding('class.align-self-middle')
  public valignMiddle: boolean;

  @HostBinding('class.align-self-end')
  public valignBottom: boolean;

  @Input()
  public hint: any = null;
  @Input()
  public hintTitle: any = null;

  @Input()
  public hintPlacement: 'LEFT' | 'RIGHT' | 'TOP' | 'BOTTOM' = 'TOP';

  @Input()
  public hintTrigger: 'HOVER' | 'FOCUS' | 'HOVER FOCUS' | string =
    'HOVER FOCUS';
  @Input()
  public hintType: 'STANDARD' | 'STANDARD_POPOVER' | 'STATIC' | 'STATIC_POPOVER' | 'STATIC_POPOVER_LEFT' | 'STATIC_LEFT' = 'STANDARD_POPOVER'

  @Input()
  public presentationStyle: 'BLOCKER' | "ERROR" | "INFO" | "WARNING" | "SUCCESS" | "OK" = null;

  @Input()
  public title: string = '';

  //Style object fo ngStyle.
  public styles: SafeStyle & any = {};

  @HostBinding('style')
  public hostStyle: SafeStyle & any = {};

  @HostBinding('class.mb-2')
  public mb2 = true;

  // @Input('formatter')
  public formatterName: string = null;
  // public formatter:FhngFormatter = null;
  public formatter: FhngFormatter = null;
  // public formatterService:FhngFormatterService = null;
  public formatterService: any = null;

  @Output()
  public click: EventEmitter<any> = new EventEmitter<any>();

  @HostBinding('class.pointer')
  public pointer: boolean = false;

  @HostBinding('class.fc')
  public fcClass: boolean = true;

  @HostBinding('class.wrapper')
  public wrapperClass: boolean = true;

  /* Setters and Getters*/

  @Input()
  public set marginTop(value: string) {
    this.processStyleWithUnit('marginTop', value);
  }

  @Input()
  public set marginBottom(value: string) {
    this.processStyleWithUnit('marginBottom', value);
    this.mb2 = false;
  }

  @Input()
  public set marginLeft(value: string) {
    this.processStyleWithUnit('marginLeft', value);
  }

  @Input()
  public set marginRight(value: string) {
    this.processStyleWithUnit('marginRight', value);
  }

  @Input()
  public set paddingTop(value: string) {
    this.processStyleWithUnit('paddingTop', value);
  }

  @Input()
  public set paddingBottom(value: string) {
    this.processStyleWithUnit('paddingBottom', value);
  }

  @Input()
  public set paddingLeft(value: string) {
    this.processStyleWithUnit('paddingLeft', value);
  }

  @Input()
  public set paddingRight(value: string) {
    this.processStyleWithUnit('paddingRight', value);
  }

  @Input('width')
  public set setWidth (value: string) {
    this.processWidth(value, true);
  }

  @Input('height')
  public set setHeight(value: string) {
    //TODO Mayby move this setter to decorator
    this.height = this.processStyleWithUnit('height', value);


  }

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.elementRef = this.injector.get(ElementRef, null);
  }

  public override ngAfterContentInit(): void {
    //After content init logic
  }

  public override ngAfterViewInit(): void {
    //After View init logic
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.processWidth(this.width);

  }

  public override ngOnInit(): void {
    super.ngOnInit();
    if (this.click.observers.length > 0) {
      this.pointer = true;
    } else {
      this.pointer = false;
    }

    this.processWidth(this.width);
    this.processHorizontalAlign();
    this.processVerticalAlign();

    let fhml = this.injector.get(FhMLService);
    this.label = fhml.transform(this.label);
  }

  /**
   * Obsługa szerokości bootstrapowych
   */


  processStyleWithUnit(name: string, val: string) {
    // let v = null;
    if (val) {
      // v = isNumber(val) ? val : val.replace(/px|%|em|rem|pt/gi, '');
      // const unit = val.replace(v, "");
      this.styles[name] = val;
    }
    return val;
  }

  public processHorizontalAlign() {
    switch (this.horizontalAlign) {
      case 'RIGHT':
        this.alignLeft = false;
        this.alignRight = true;
        this.alignCenter = false;
        break;
      case 'CENTER':
        this.alignLeft = false;
        this.alignRight = false;
        this.alignCenter = true;
        break;
      case 'LEFT':
        this.alignLeft = true;
        this.alignRight = false;
        this.alignCenter = false;
        break;
    }
  }

  public processVerticalAlign() {
    switch (this.verticalAlign) {
      case 'TOP':
        this.valignTop = true;
        this.valignBottom = false;
        this.valignMiddle = false;
        break;
      case 'MIDDLE':
        this.valignTop = false;
        this.valignBottom = false;
        this.valignMiddle = true;
        break;
      case 'BOTTOM':
        this.valignTop = false;
        this.valignBottom = true;
        this.valignMiddle = false;
        break;
    }
  }

  public override mapAttributes(data: IDataAttributes): void {
    super.mapAttributes(data);
    if (data.inlineStyle) this.styles = this._convertInlineStylesToSafeStyle(data.inlineStyle);

    if (data.accessibility) {
      this.accessibility = data.accessibility;
    }

    // if (data.value) this.label = data.value; //Label zostawiamy w spokoju, pozmieniamy w komponentach jak trzeba - z value na label
    if (data.style) this.bootstrapStyle = data.style
    this.setWidth = data.width;
    this.setHeight = data.height;
  }



  public processWidth(value: string, force: boolean = false): void {
    // if (this.hostWidth.length === 0 || force) {
    if (!value) {
      value = this.width;
    }

    if (value) {
      this.width = value;

      if (
        value.indexOf('px') >= 0 || //pixel width
        value.indexOf('%') >= 0 || //procent widths
        value.indexOf('vw') >= 0 || //width Relative width of the viewport
        value == 'fit' //width Relative width of the viewport
      ) {
        //Set host element width to auto to fit its content.
        this.hostWidth += 'col-auto exactWidth';
        //Set inner element styles to exact width;
        if (value != 'fit') {
          this.processStyleWithUnit('width', value);
        }
      } else if (value == 'auto') {
        this.hostWidth += 'col';
      } else {
        //Host works with bootstrap width classes.
        const widths = value.replace(/ /g, '').split(',');
        this.hostWidth += ' col-' + widths.join(' col-');
      }
    }
  }

  private _convertInlineStylesToSafeStyle (inlineStyle: string): SafeStyle {
    let _safeStyle: SafeStyle = {};

    for (let style of (inlineStyle || '').split(';').filter(item => !!(item))) {
      let _property = style.split(':');

      if (_property[0] && _property[1]) {
        _safeStyle[_property[0].replace(' ', '')] = _property[1];
      }
    }

    return _safeStyle;
  }

  public getHintTooltip(): string {
    if (this.hint && this.hintType == 'STANDARD') {
      return this.hint;
    }
    return null;
  }

  public getHintPopover(): string {
    if (this.hint && this.hintType == 'STANDARD_POPOVER') {
      return this.hint;
    }
    return null;
  }

  setPresentationStyle(): string {
    let styles = null;

    switch (this.presentationStyle) {
      case 'BLOCKER':
      case 'ERROR':
        // ['is-invalid', 'border', 'border-danger'].forEach(function (cssClass) {
        //   this.getMainComponent().classList.add(cssClass);
        // }.bind(this));
        styles = 'is-invalid border border-danger';
        break;
      case 'OK':
        // ['border', 'border-success'].forEach(function (cssClass) {
        //   this.getMainComponent().classList.add(cssClass);
        // }.bind(this));
        styles = 'border border-success';
        break;
      case 'INFO':
        // ['border', 'border-info'].forEach(function (cssClass) {
        //   this.getMainComponent().classList.add(cssClass);
        // }.bind(this));
        styles = 'border border-info';
        break;
      case 'WARNING':
        // ['border', 'border-warning'].forEach(function (cssClass) {
        //   this.getMainComponent().classList.add(cssClass);
        // }.bind(this));
        styles = 'border border-warning';
        break;
    }

    return styles;
  }

}
