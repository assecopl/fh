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

@Directive()
export class FhngHTMLElementC
  extends FhngComponent
  implements OnInit, AfterViewInit, AfterContentInit, OnChanges {
  @HostBinding('attr.tabindex')
  public tabindex: number = null;

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

  @Input()
  public hiddenElementsTakeUpSpace: boolean = false;

  public elementRef: ElementRef;

  private styleUnit: '%' | 'px' | 'em' | 'rem' = 'px';

  /**
   * Standard inputs for HTMLElement
   */

  @Input()
  public label: string = '';

  @Input()
  public labelPosition: 'UP' | 'DOWN' | 'LEFT' | 'RIGHT' = 'UP';

  @Input()
  public set height(value: string) {
    //TODO Mayby move this setter to decorator
    this.processStyleWithUnit('height', value);
  }

  @Input()
  public horizontalAlign: 'LEFT' | 'CENTER' | 'RIGHT';

  @HostBinding('class.align-self-left')
  alignLeft: boolean;

  @HostBinding('class.text-center')
  @HostBinding('class.align-self-center')
  alignCenter: boolean;

  @HostBinding('class.text-right')
  @HostBinding('class.align-self-right')
  alignRight: boolean;

  @Input()
  public verticalAlign: 'TOP' | 'MIDDLE' | 'BOTTOM';

  @HostBinding('class.align-self-start')
  valignTop: boolean;

  @HostBinding('class.align-self-middle')
  valignMiddle: boolean;

  @HostBinding('class.align-self-end')
  valignBottom: boolean;

  @Input()
  public hint: any;

  @Input()
  public hintPlacement: 'LEFT' | 'RIGHT' | 'TOP' | 'BOTTOM' = 'TOP';

  @Input()
  public hintTrigger: 'HOVER' | 'FOCUS' | 'HOVER FOCUS' | string =
    'HOVER FOCUS';

  @Input()
  public title: string = '';

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

  //Style object fo ngStyle.
  public styles: SafeStyle & any = {};

  /**
   * Parametr width jest domyśłnym parametrem komponentu angularowego, przychwytujemy go aby obsłużyć
   * logikę ustawiania klas typowych dla bootstrapa.
   */
  public width: string = 'md-12';

  @Input('width')
  public set _width(value: string) {
    this.processWidth(value, true);
  }

  @HostBinding('class')
  @Input()
  hostWidth: string = '';

  @HostBinding('style')
  hostStyle: SafeStyle & any = {};

  @HostBinding('class.mb-2')
  mb3 = true;

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

  constructor(
    public override injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
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

    this.processWidth(this._width);
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
  public processWidth(value: string, force: boolean = false) {
    if (this.hostWidth.length === 0 || force) {
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
          this.hostWidth += 'col-auto fc wrapper exactWidth';
          //Set inner element styles to exact width;
          if (value != 'fit') {
            this.processStyleWithUnit('width', value);
          }
        } else if (value == 'auto') {
          this.hostWidth += 'col fc wrapper';
        } else {
          //Host works with bootstrap width classes.
          const widths = value.replace(/ /g, '').split(',');
          this.hostWidth += ' col-' + widths.join(' col-');
        }
      }
    }
  }

  processStyleWithUnit(name: string, val: string) {
    let v = null;
    if (val) {
      v = isNumber(val) ? val : val.replace(/px|%|em|rem|pt/gi, '');
      this.styles[name + '.' + this.styleUnit] = v;
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
}
