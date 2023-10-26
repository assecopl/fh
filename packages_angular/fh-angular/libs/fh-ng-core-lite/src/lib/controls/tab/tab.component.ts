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
// import {DocumentedComponent} from '@fhng/ng-core';
// import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

// @DocumentedComponent({
//   category: DocumentedComponent.Category.OTHERS,
//   value: 'Tab represents a single tab component',
//   icon: 'fa-fw fa fa-window-maximize',
// })
@Component({
  selector: 'fhng-tab',
  templateUrl: './tab.component.html',
  styleUrls: ['./tab.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    // FhngAvailabilityDirective,
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

  public override mb3 = false;

  public override wrapperClass = false;

  @HostBinding('id')
  public tabId: string;

  @HostBinding('class.tab-pane')
  public classTabPane:boolean = true;

  @HostBinding('class.fc-editable')
  public classFcEditable:boolean = false;

  @HostBinding('class.d-none')
  public classDNone:boolean = false;

  @HostBinding('class.active')
  public selected: boolean = false;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = 'md-12';
  }

  public override ngOnInit() {
    super.ngOnInit();

    if (this.id) {
      this.tabId = this.id;
    } else {
      this.tabId =
        this.constructor.name + '_' + (Math.random() * 100000000).toFixed();
    }

    this.classFcEditable = this.accessibility === 'EDIT';
    this.classDNone = this.accessibility === 'HIDDEN';
  }

  public override  ngAfterContentInit(): void {
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: IDataAttributes & {selected: boolean}) {
    super.mapAttributes(data);

    this.selected = data.selected;
  }
}
