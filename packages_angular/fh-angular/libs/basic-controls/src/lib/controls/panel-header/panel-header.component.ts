import {
  Component,
  EventEmitter,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent, I18nService, IDataAttributes} from "@fh-ng/forms-handler";

@Component({
  selector: 'fhng-panel-header',
  templateUrl: './panel-header.component.html',
  styleUrls: ['./panel-header.component.scss'],
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
      useExisting: forwardRef(() => PanelHeaderFhDPComponent),
    },
  ],
})
export class PanelHeaderFhDPComponent extends FhngHTMLElementC implements OnInit {

  @Input()
  public info: string = '';

  @Input()
  public onClickNameEvent: string = '';

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    public i18n: I18nService
  ) {
    super(injector, parentFhngComponent);
  }

  public override ngOnInit(): void {
    super.ngOnInit();
  }

  public override ngOnChanges(changes: SimpleChanges): void{
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: IDataAttributes & {info: string}): void {
    super.mapAttributes(data);

    this.info = data.info;
  }

  public onClickEvent($event: Event): void {
    $event.stopPropagation();

    if (this.formId === 'FormPreview') {
      this.fireEvent('onClick', this.onClickNameEvent);
    } else {
      this.fireEventWithLock('onClick', this.onClickNameEvent)
    }
  }
}
