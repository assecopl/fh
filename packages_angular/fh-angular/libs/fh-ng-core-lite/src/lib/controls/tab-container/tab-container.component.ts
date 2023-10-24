import {
  AfterContentInit,
  Component,
  ContentChildren,
  EventEmitter,
  forwardRef,
  Host,
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
import {DocumentedComponent} from '@fhng/ng-core';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@DocumentedComponent({
  category: DocumentedComponent.Category.ARRANGEMENT,
  value: 'Tab Container aggregates single tab components',
  icon: 'fas fa-window-maximize',
})
@Component({
  selector: 'fhng-tab-container',
  templateUrl: './tab-container.component.html',
  styleUrls: ['./tab-container.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    FhngAvailabilityDirective,
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
  @Input() width: string = BootstrapWidthEnum.MD12;
  @Input() activeTabLabel: string;
  @Input() activeTabIndex: number;
  @Input() activeTabId: string;

  @Output()
  public activeTabIndexChange: EventEmitter<number> =
    new EventEmitter<number>();
  @Output()
  public activeTabIdChange: EventEmitter<string> = new EventEmitter<string>();

  @Output()
  public tabChange: EventEmitter<TabComponent> = new EventEmitter();

  boundActiveTabIndex: number;

  @ContentChildren(TabComponent) public subcomponents: QueryList<TabComponent> =
    new QueryList<TabComponent>();
  public subcomponentsArray: TabComponent[] = [];

  public ulStyleClasses: String[] = ['nav', 'nav-tabs', 'ul-tabs'];
  public listStyleClasses: String[] = ['nav-item'];
  public activeLinkStyleClasses: String[] = ['nav-link', 'active'];

  constructor(
    public injector: Injector,
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

  ngOnInit() {
    super.ngOnInit();
  }

  ngAfterContentInit(): void {
    this.subcomponentsArray = this.subcomponents.toArray();
    this.activateDefaultTab();
  }

  activateDefaultTab(): void {
    if (this.subcomponents.length > 0) {
      if (this.activeTabLabel) {
        this.subcomponents.forEach((tab) => {
          if (tab.label === this.activeTabLabel) {
            tab.active = true;
            this.boundActiveTabIndex = this.subcomponents
              .toArray()
              .indexOf(tab);
          }
        });
      } else if (this.activeTabIndex) {
        if (this.activeTabIndex < this.subcomponents.length) {
          if (this.subcomponents.toArray()[this.activeTabIndex]) {
            this.subcomponents.toArray()[this.activeTabIndex].active = true;
            this.boundActiveTabIndex = this.activeTabIndex;
          }
        }
      } else if (this.activeTabId) {
        let tab = this.subcomponents.find(
          (c) => c.innerId === this.activeTabId
        );
        if (tab) {
          tab.active = true;
          this.boundActiveTabIndex = this.subcomponents.toArray().indexOf(tab);
        }
      } else {
        this.subcomponents.toArray()[0].active = true; // activate first Tab by default if none was selected by user
        this.boundActiveTabIndex = 0;
      }
    }
  }

  getTabId(tab: TabComponent): string {
    return tab.tabId;
  }

  selectTab(tab: TabComponent, event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }
    this.subcomponents.forEach((element) => {
      element.active = false;
    });
    tab.active = true;
    this.boundActiveTabIndex = this.subcomponents.toArray().indexOf(tab);
    if (this.tabChange) {
      this.activeTabIdChange.emit(tab.tabId);
      this.activeTabIndexChange.emit(this.boundActiveTabIndex);
      this.tabChange.emit(tab);
    }
  }

  deactivateTabs() {
    this.subcomponents.forEach((element) => {
      element.active = false;
    });
    this.boundActiveTabIndex = null;
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    if (
      changes['activeTabLabel'] ||
      changes['activeTabId'] ||
      changes['activeTabIndex']
    ) {
      this.deactivateTabs();
      this.activateDefaultTab();
    }
  }
}
