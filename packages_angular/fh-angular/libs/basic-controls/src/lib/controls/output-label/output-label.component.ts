import {
  Component,
  EventEmitter,
  forwardRef,
  HostBinding,
  HostListener,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent, IDataAttributes} from '@fh-ng/forms-handler';

@Component({
  selector: '[fhng-output-label]',
  templateUrl: './output-label.component.html',
  styleUrls: ['./output-label.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => OutputLabelComponent),
    },
  ],
})
export class OutputLabelComponent extends FhngHTMLElementC implements OnInit {
  public override width: string = BootstrapWidthEnum.MD2;

  @Input()
  public value: string = null;

  @HostBinding('class.justify-content-end')
  justifyContentRight: boolean = false;

  @HostBinding('class.justify-content-center')
  justifyContentCenter: boolean = false;

  @Input()
  public headingType:
    | 'H1'
    | 'H2'
    | 'H3'
    | 'H4'
    | 'H5'
    | 'H6'
    | 'Default'
    | 'Auto' = 'Default';

  @Output()
  public override click: EventEmitter<any> = new EventEmitter<any>();

  @HostBinding('attr.tabindex')
  public override tabindex: number = null;

  @HostBinding('style')
  public override styles;

  @HostListener('keydown.enter', ['$event']) onEnterHandler(
    event: KeyboardEvent
  ) {
    if (this.click?.observers.length > 0) {
      event.stopPropagation();
      event.preventDefault();
      this.click.emit(event);
    }
  }

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.mb2 = false;
  }

  override ngOnInit() {
    super.ngOnInit();


    this.processTextAlign();
    if (this.click?.observers.length > 0) {
      this.tabindex = 0;
    }
  }

  processTextAlign() {
    switch (this.horizontalAlign) {
      case 'RIGHT':
        this.justifyContentRight = true;
        this.justifyContentCenter = false;
        break;
      case 'CENTER':
        this.justifyContentCenter = true;
        this.justifyContentRight = false;
        break;
      case 'LEFT':
      default:
        this.justifyContentRight = false;
        this.justifyContentCenter = false;
        break;
    }
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: IDataAttributes) {
    super.mapAttributes(data);
    this.value = data.value;
  }
}
