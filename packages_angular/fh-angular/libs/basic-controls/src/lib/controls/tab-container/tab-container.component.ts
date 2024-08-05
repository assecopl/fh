import {
  AfterContentInit,
  AfterViewInit,
  Component,
  EventEmitter,
  forwardRef,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {TabComponent} from '../tab/tab.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent, IDataAttributes} from "@fh-ng/forms-handler";

type Tab = { label: string, id: string, selected: boolean };
type TabElement = IDataAttributes & Tab;

@Component({
  selector: '[fhng-tab-container]',
  templateUrl: './tab-container.component.html',
  styleUrls: ['./tab-container.component.scss'],
  providers: [
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
  implements OnInit, AfterContentInit, AfterViewInit, OnDestroy {

  public override mb2 = false;

  @Input()
  public override width: string = BootstrapWidthEnum.MD12;

  @Input()
  public activeTabLabel: string;

  @Input()
  public activeTabIndex: number = 0;

  @Input()
  public activeTabId: string;

  @Input()
  public onTabChange: string = null;

  @Output()
  public activeTabIndexChange: EventEmitter<number> = new EventEmitter<number>();

  @Output()
  public activeTabIdChange: EventEmitter<string> = new EventEmitter<string>();

  @Output()
  public tabChange: EventEmitter<TabComponent> = new EventEmitter();

  public boundActiveTabIndex: number;

  public tabs: { id: string, label: string, selected: boolean }[] = [];

  private _subscriptions = [];

  public override childFhngComponents: TabComponent[] = [];

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
    }
  }

  public override ngAfterViewInit(): void {
    super.ngAfterViewInit();
    this._mapSubscriptions();
  }

  public override ngOnDestroy(): void {
    this._unsubscribe();
  }

  public activateDefaultTab(): void {
    if (this.childFhngComponents.length > 0) {
      if (this.activeTabLabel) {
        this.childFhngComponents.forEach((tab) => {
          if (tab.label === this.activeTabLabel) {
            tab.selected = true;
            this.boundActiveTabIndex = this.childFhngComponents.findIndex(subElement => subElement.id === tab.id);
            this.activeTabIndex = this.boundActiveTabIndex;
          }
        });
      } else if (this.activeTabIndex) {
        if (this.activeTabIndex < this.childFhngComponents.length) {
          if (this.childFhngComponents[this.activeTabIndex]) {
            this.childFhngComponents[this.activeTabIndex].selected = true;
            this.boundActiveTabIndex = this.activeTabIndex;
          }
        }
      } else if (this.activeTabId) {
        let tab = this.childFhngComponents.find(
          (c) => c.id === this.activeTabId
        );
        if (tab) {
          tab.selected = true;
          this.boundActiveTabIndex = this.childFhngComponents.findIndex(subElement => subElement.id === tab.id);
          this.activeTabIndex = this.boundActiveTabIndex;
        }
      } else {
        this.childFhngComponents[0].selected = true;
        this.boundActiveTabIndex = 0;
        this.activeTabIndex = this.boundActiveTabIndex
      }
    }
  }


  public selectTab(tab: TabComponent, event: Event): void {
    if (event) {
      event.preventDefault();
      event.stopPropagation();
    }

    this.childFhngComponents.forEach((element) => {
      element.selected = false;
    });

    tab.selected = true;

    this.boundActiveTabIndex = this.childFhngComponents.findIndex(element => element.id === tab.id);
    this.activeTabIndex = this.boundActiveTabIndex

    if (this.tabChange) {
      this.activeTabIdChange.emit(tab.id);
      this.activeTabIndexChange.emit(this.boundActiveTabIndex);
      this.tabChange.emit(this.childFhngComponents.find(subElement => subElement.id === tab.id));
    }
    this.changesQueue.queueValueChange(this.activeTabIndex);
    if (this.onTabChange) {
      this.fireEvent('onTabChange', this.onTabChange);
    }

    if (this.onTabChange) {
      this.fireEventWithLock('onTabChange', this.onTabChange);
    }
  }

  public deactivateTabs(): void {
    this.childFhngComponents.forEach((element) => {
      element.selected = false;
    });

    this.boundActiveTabIndex = null;
  }

  public override mapAttributes(data: IDataAttributes & {onTabChange?: string}) {
    super.mapAttributes(data);

    if(data.activeTabIndex != undefined) {
      this.activeTabIndex = data.activeTabIndex;
    }

    // this.subelements = [...this.subelements];

    this.onTabChange = data.onTabChange || this.onTabChange;

    this._mapTabs();
  }

  private _mapTabs() {
    this.childFhngComponents.map((element, index) => {
      element.selected = (index === this.activeTabIndex);
    });
  }

  private _mapSubscriptions(): void {
    this._unsubscribe();
    this.childFhngComponents.forEach((element: any, index: number) => {
      if (element.update$) {
        this._subscriptions.push(element.update$.subscribe((data: TabElement) => this._updateSubscribeEvent(data)));
      }
    });
  }

  private _unsubscribe(): void {
    this._subscriptions.forEach(element => element.unsubscribe());
    this._subscriptions = [];
  }

  private _updateSubscribeEvent(data: TabElement): void {
    let index = this.subelements.findIndex(element => element.id === data.id);

    // this.subelements[index] = {...this.subelements[index], label: data.label, selected: data.selected}
  }
}
