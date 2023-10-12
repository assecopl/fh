import {
  AfterContentInit,
  Component,
  ContentChildren,
  forwardRef,
  Host,
  Injector,
  Input,
  OnInit,
  Optional,
  QueryList,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {TabComponent} from '../tab/tab.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {AvailabilityEnum, FhngAvailabilityDirective,} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@Component({
  selector: 'fhng-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => WizardComponent)},
  ],
})
export class WizardComponent
  extends GroupingComponentC<TabComponent>
  implements OnInit, AfterContentInit {
  @Input() width: string = BootstrapWidthEnum.MD12;
  @Input() activeTabLabel: string;
  @Input() activeTabIndex: any;

  boundActiveTabIndex: number;

  @ContentChildren(TabComponent) public subcomponents: QueryList<TabComponent> =
    new QueryList<TabComponent>();
  public subcomponentsArray: TabComponent[] = [];

  public ulStyleClasses: String[] = ['nav', 'nav-tabs', 'wizard-tabs'];
  public listStyleClasses: String[] = ['nav-item'];
  public activeLinkStyleClasses: String[] = ['nav-link', 'active'];

  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.horizontalAlign = 'CENTER';
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
        this.subcomponents.toArray()[this.activeTabIndex].active = true;
        this.boundActiveTabIndex = this.activeTabIndex;
      } else {
        this.subcomponents.toArray()[0].active = true; // activate first Tab by default if none was selected by user
        this.boundActiveTabIndex = 0;
      }
    }
  }

  getTabId(tab: TabComponent): string {
    return tab.tabId;
  }

  selectTab(tab: TabComponent): void {
    if (this.availabilityDirective._availability != AvailabilityEnum.EDIT) {
      return;
    }
    this.subcomponents.forEach((element) => {
      element.active = false;
    });
    tab.active = true;
    this.boundActiveTabIndex = this.subcomponents.toArray().indexOf(tab);
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
