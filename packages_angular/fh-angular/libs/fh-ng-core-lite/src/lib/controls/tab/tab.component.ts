import {
  AfterContentInit,
  Component,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {DocumentedComponent} from '@fhng/ng-core';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';

@DocumentedComponent({
  category: DocumentedComponent.Category.OTHERS,
  value: 'Tab represents a single tab component',
  icon: 'fa-fw fa fa-window-maximize',
})
@Component({
  selector: 'fhng-tab',
  templateUrl: './tab.component.html',
  styleUrls: ['./tab.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => TabComponent)},
  ],
})
export class TabComponent
  extends FhngHTMLElementC
  implements OnInit, OnChanges, AfterContentInit {
  //FIXME Why tabComponent is GroupingComponentC - it is wrong

  @Input()
  active: boolean = false;

  @HostBinding('id')
  tabId: string;

  constructor(
    public injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = '';
    this.active = false;
  }

  ngOnInit() {
    super.ngOnInit();
    if (this.id) {
      this.tabId = this.id;
    } else {
      this.tabId =
        this.constructor.name + '_' + (Math.random() * 100000000).toFixed();
    }
  }

  public ngAfterContentInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
