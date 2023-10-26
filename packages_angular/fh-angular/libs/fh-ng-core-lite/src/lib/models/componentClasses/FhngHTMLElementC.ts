import {
  AfterContentInit,
  AfterViewInit,
  Directive,
  ElementRef,
  EventEmitter,
  Host,
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
import {isNumber} from 'util';
import {FhngComponent} from './FhngComponent';
import {SafeStyle} from '@angular/platform-browser';
import {FhMLService} from '../../service/fh-ml.service';
import {IDataAttributes} from "../interfaces/IDataAttributes";
import {Accessibility, IconAligmentType, StyleUnit} from "../CommonTypes";
import {STYLE_UNIT} from "../enums/StyleUnitEnum";

@Directive()
export class FhngHTMLElementC
  extends FhngComponent
  implements OnInit, AfterViewInit, AfterContentInit, OnChanges {

  public static STYLE_UNIT: StyleUnit = STYLE_UNIT.PX;

  @HostBinding('attr.tabindex')
  public tabindex: number = null;

  @Input()
  public accessibility: Accessibility;

  // public availabilityDirective: FhngAvailabilityDirective;
  // public availabilityEnum:any = AvailabilityEnum;

  public availability: string;
  // @Input()
  // public set availability(value: AvailabilityEnum | string) {
  //   // if ( typeof value === 'string') {
  //   //   const a: any = AvailabilityEnum[value];
  //   //   this.availabilityDirective._myAvailability = a;
  //   // } else {
  //   //   this.availabilityDirective._myAvailability = value;
  //   // }
  // }

  /**
   * Parametr width jest domyśłnym parametrem komponentu angularowego, przychwytujemy go aby obsłużyć
   * logikę ustawiania klas typowych dla bootstrapa.
   */
  @Input()
  public width: string;

  public height: string;

  @HostBinding('class')
  @Input()
  public hostWidth: string = '';

  // @Input('width')
  // public set width(value: string) {
  //   this.processWidth(value, true);
  // }


  @Input()
  public bootstrapStyle: any;

  @Input()
  public hiddenElementsTakeUpSpace: boolean = false;

  @Input()
  public icon: string = null;

  @Input()
  public iconAlignment: IconAligmentType;

  public elementRef: ElementRef;

  @Input()
  public label: string = '';

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
  public hint: any;

  @Input()
  public hintPlacement: 'LEFT' | 'RIGHT' | 'TOP' | 'BOTTOM' = 'TOP';

  @Input()
  public hintTrigger: 'HOVER' | 'FOCUS' | 'HOVER FOCUS' | string =
    'HOVER FOCUS';

  @Input()
  public title: string = '';

  //Style object fo ngStyle.
  public styles: SafeStyle & any = {};

  @HostBinding('style')
  public hostStyle: SafeStyle & any = {};

  @HostBinding('class.mb-2')
  public mb3 = true;

  @Input('formatter')
  public formatterName: string = null;
  // public formatter:FhngFormatter = null;
  public formatter: any = null;
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
    this.mb3 = false;
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
    this.processStyleWithUnit('height', value);
  }

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.elementRef = this.injector.get(ElementRef, null);
    // this.availabilityDirective = this.injector.get(FhngAvailabilityDirective, null);
  }

  public override ngAfterContentInit(): void {
    //After content init logic
  }

  public override ngAfterViewInit(): void {
    //After View init logic
  }

  ngOnChanges(changes: SimpleChanges): void {
    // if (this.availabilityDirective && changes["availability"] && changes["availability"].firstChange == false) {
    //   //Inside component we set availability so it will be processed by directive as its own availability.(_myAvailability)
    //   if(this.hiddenElementsTakeUpSpace) {
    //     this.availabilityDirective.hiddenElementsTakeUpSpace = this.hiddenElementsTakeUpSpace;
    //   }
    //   this.availabilityDirective.setAvailability(this.availabilityDirective._myAvailability, true);
    // }
    this.processWidth(this.width);

  }

  public override ngOnInit(): void {
    super.ngOnInit();
    if (this.click.observers.length > 0) {
      this.pointer = true;
    } else {
      this.pointer = false;
    }

    // if(this.formatterName){
    //   this.formatterService = this.injector.get(FhngFormatterService, null);
    //   if(this.formatterService) {
    //     this.formatter = this.formatterService.getFormatter(this.formatterName);
    //   }
    // }

    this.processWidth(this.width);
    this.processHorizontalAlign();
    this.processVerticalAlign();

    let fhngml = this.injector.get(FhMLService);
    this.label = fhngml.transform(this.label);

    /**
     * Initilize local directive initilize it and set id
     */
    // if (this.availabilityDirective && !this.availabilityDirective.initialize) {
    //   //Pass only external ID to use it with AvailabilityCOnfiguration.
    //   this.availabilityDirective.id = this.id;
    //   if(this.hiddenElementsTakeUpSpace) {
    //     this.availabilityDirective.hiddenElementsTakeUpSpace = this.hiddenElementsTakeUpSpace;
    //   }
    //   this.availabilityDirective.ngOnInit();
    // }
  }

  /**
   * Obsługa szerokości bootstrapowych
   */


  processStyleWithUnit(name: string, val: string) {
    let v = null;
    if (val) {
      v = isNumber(val) ? val : val.replace(/px|%|em|rem|pt/gi, '');
      this.styles[name + '.' + FhngHTMLElementC.STYLE_UNIT] = v;
    }
    return v;
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

  /**
   * Component Focus logic
   */
  @ViewChild('focusElement', {static: false}) focusElement: ElementRef;

  public focus() {
    if (this.focusElement) {
      try {
        this.focusElement.nativeElement.focus();
      } catch (e) {
        console.warn('Element ' + this.id + ' is not focusable');
      }
    }
  }

  public override mapAttributes(data: IDataAttributes): void {
    super.mapAttributes(data);
    if (data.inlineStyle) this.styles = this._convertInlineStylesToSafeStyle(data.inlineStyle);

    if (data.value) this.label = data.value;
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

}
