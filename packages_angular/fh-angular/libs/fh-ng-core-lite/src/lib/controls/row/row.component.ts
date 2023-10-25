import {
  Component,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@Component({
  selector: 'fhng-row',
  templateUrl: './row.component.html',
  styleUrls: ['./row.component.css'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    // FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => RowComponent)},
  ],
})
export class RowComponent extends FhngHTMLElementC implements OnInit {
  @HostBinding('class')
  public class;

  @Input()
  public elementsHorizontalAlign:
    | 'LEFT'
    | 'CENTER'
    | 'RIGHT'
    | 'AROUND'
    | 'BETWEEN'
    | null = 'LEFT';

  @Input()
  public elementsVerticalAlign: 'TOP' | 'MIDDLE' | 'BOTTOM' | null = null;

  @Input() public override hiddenElementsTakeUpSpace: boolean = false;

  public rowStyleClasses: string[] = [];

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    this.mb3 = false;
  }

  override ngOnInit() {
    super.ngOnInit();

    this.rowStyleClasses = [];
    if (this.elementsHorizontalAlign != null) {
      // @ts-ignore
      switch (this.elementsHorizontalAlign) {
        case 'LEFT':
          this.rowStyleClasses.push('justify-content-start');
          break;
        case 'CENTER':
          this.rowStyleClasses.push('justify-content-center');
          break;
        case 'RIGHT':
          this.rowStyleClasses.push('justify-content-end');
          break;
        case 'BETWEEN':
          this.rowStyleClasses.push('justify-content-between');
          break;
        case 'AROUND':
          this.rowStyleClasses.push('justify-content-around');
          break;
        default:
        // GlobalErrorHandler.throwError(new Error(`Unkown elementsHorizontalAlign propety value '${this.elementsHorizontalAlign}'!`));
      }
    }

    if (this.elementsVerticalAlign != null) {
      switch (this.elementsVerticalAlign) {
        case 'TOP':
          this.rowStyleClasses.push('align-items-start');
          break;
        case 'MIDDLE':
          this.rowStyleClasses.push('align-items-center');
          break;
        case 'BOTTOM':
          this.rowStyleClasses.push('align-items-end');
          break;
        default:
        // GlobalErrorHandler.throwError(new Error(`Unkown elementsVerticalAlign propety value '${this.elementsVerticalAlign}'!`));
      }
    }
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override processWidth(value: string, force: boolean = false) {
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
}
