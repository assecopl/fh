import {
  Component,
  forwardRef,
  Injector,
  Input,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

type HElementPosition = 'LEFT' | 'CENTER' | 'RIGHT' | 'AROUND' | 'BETWEEN'| null;
type VElementPosition = 'TOP' | 'MIDDLE' | 'BOTTOM' | null;
interface IRowMapAttributes extends IDataAttributes {
  elementsHorizontalAlign: HElementPosition,
  elementsVerticalAlign: VElementPosition
}

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
  @Input()
  public elementsHorizontalAlign: HElementPosition = 'LEFT';

  @Input()
  public elementsVerticalAlign: VElementPosition = null;

  @Input()
  public override hiddenElementsTakeUpSpace: boolean = false;

  public rowStyleClasses: string[] = [];

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.mb3 = false;
    this.width = 'md-12';
  }

  override ngOnInit() {
    super.ngOnInit();
    this._hElementsAlign();
    this._vElementsAlign();
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: IRowMapAttributes) {
    super.mapAttributes(data);

    this._hElementsAlign(data.elementsHorizontalAlign);
    this._vElementsAlign(data.elementsVerticalAlign);
  }

  private _hElementsAlign (elementHorizontalAlign?: HElementPosition): void {
    if (elementHorizontalAlign) {
      this.elementsHorizontalAlign = elementHorizontalAlign;
    }

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
  }

  private _vElementsAlign (elementsVerticalAlign?: VElementPosition): void {
    if (elementsVerticalAlign) {
      this.elementsVerticalAlign = elementsVerticalAlign;
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
}
