import {
  Component,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {IconAligmentType} from '../../models/CommonTypes';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@Component({
  selector: 'fh-dropdown-item',
  templateUrl: './dropdown-item.component.html',
  styleUrls: ['./dropdown-item.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    // FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => DropdownItemComponent),
    },
  ],
})
export class DropdownItemComponent extends FhngHTMLElementC implements OnInit {
  @Input()
  public icon: string;

  @Input()
  public iconAlignment: IconAligmentType;

  @Output()
  public selectedButton: EventEmitter<DropdownItemComponent> =
    new EventEmitter<DropdownItemComponent>();

  @Input()
  public url: string;

  override mb3 = false;

  constructor(
    public override injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  override ngOnInit() {
    super.ngOnInit();
  }

  onClick() {
    if (this.url) {
      window.location.href = this.url;
    }
    this.selectedButton.emit(this);
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
