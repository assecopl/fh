import {
  AfterContentInit,
  Component,
  ContentChildren,
  EventEmitter,
  forwardRef,
  Host, HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  QueryList,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {TabComponent} from '../tab/tab.component';
// import {DocumentedComponent} from '@fhng/ng-core';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
// import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

type TabElement = IDataAttributes & {label: string, id: string, selected: boolean};

// @DocumentedComponent({
//   category: DocumentedComponent.Category.ARRANGEMENT,
//   value: 'Tab Container aggregates single tab components',
//   icon: 'fas fa-window-maximize',
// })
@Component({
  selector: 'fhng-tab-container',
  templateUrl: './tab-container.component.html',
  styleUrls: ['./tab-container.component.scss'],
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
      useExisting: forwardRef(() => TabContainerComponent),
    },
  ],
})
export class TabContainerComponent
  extends GroupingComponentC<TabComponent>
  implements OnInit, AfterContentInit {

  public override mb2 = false;

  @Input()
  public override width: string = BootstrapWidthEnum.MD12;

  @Input()
  public activeTabLabel: string;

  @Input()
  public activeTabIndex: number;

  @Input()
  public activeTabId: string;

  @Output()
  public activeTabIndexChange: EventEmitter<number> = new EventEmitter<number>();

  @Output()
  public activeTabIdChange: EventEmitter<string> = new EventEmitter<string>();

  @Output()
  public tabChange: EventEmitter<TabComponent> = new EventEmitter();

  public boundActiveTabIndex: number;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  public updateSubcomponent: (
    subcomponent: TabComponent,
    index: number
  ) => null;

  public getSubcomponentInstance(): new (...args: any[]) => TabComponent {
    return TabComponent;
  }

  public override ngOnInit() {
    super.ngOnInit();
  }

  public override ngAfterContentInit(): void {
    this.activateDefaultTab();
    this.subelements = JSON.parse(JSON.stringify(this.subelements));
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (
        changes['activeTabLabel'] ||
        changes['activeTabId'] ||
        changes['activeTabIndex']
    ) {
      this.deactivateTabs();
      this.activateDefaultTab();
      this.subelements = JSON.parse(JSON.stringify(this.subelements));
    }
  }

  public activateDefaultTab(): void {
    if (this.subelements.length > 0) {
      if (this.activeTabLabel) {
        this.subelements.forEach((tab) => {
          if (tab.label === this.activeTabLabel) {
            tab.selected = true;
            this.boundActiveTabIndex = this.subelements.findIndex(subElement => subElement.id === tab.id);
          }
        });
      } else if (this.activeTabIndex) {
        if (this.activeTabIndex < this.subelements.length) {
          if (this.subelements[this.activeTabIndex]) {
            this.subelements[this.activeTabIndex].selected = true;
            this.boundActiveTabIndex = this.activeTabIndex;
          }
        }
      } else if (this.activeTabId) {
        let tab = this.subelements.find(
          (c) => c.id === this.activeTabId
        );
        if (tab) {
          tab.selected = true;
          this.boundActiveTabIndex = this.subelements.findIndex(subElement => subElement.id === tab.id);
        }
      } else {
        this.subelements[0].selected = true;
        this.boundActiveTabIndex = 0;
      }
    }
  }


  selectTab(tab: TabElement, event: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    this.subelements.forEach((element) => {
      element.selected = false;
    });

    tab.selected = true;

    this.boundActiveTabIndex = this.subelements.findIndex(element => element.id === tab.id);

    if (this.tabChange) {
      this.activeTabIdChange.emit(tab.id);
      this.activeTabIndexChange.emit(this.boundActiveTabIndex);
      this.tabChange.emit(this.subelements.find(subElement => subElement.id === tab.id));
    }

    this.subelements = JSON.parse(JSON.stringify(this.subelements));
  }

  deactivateTabs() {
    this.subelements.forEach((element) => {
      element.selected = false;
    });

    this.boundActiveTabIndex = null;
  }

  public override mapAttributes(data: IDataAttributes) {
    super.mapAttributes(data);

    this.activeTabIndex = data.activeTabIndex;

    this._mapTabs();

    // console.log('TabContainer', data, this);
  }

  private _mapTabs () {
    this.subelements.map((element, index) => {
        element.selected = (index === this.activeTabIndex)
    });
  }
}
