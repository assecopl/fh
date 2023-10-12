import {
  Component,
  ContentChildren,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  Optional,
  Output,
  QueryList,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {PanelGroupComponent} from '../panel-group/panel-group.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {DocumentedComponent} from '@fhng/ng-core';
import {FhngAvailabilityDirective} from '@fhng/ng-availability';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

@DocumentedComponent({
  category: DocumentedComponent.Category.ARRANGEMENT,
  value:
    'PanelGroup component responsible for the grouping of sub-elements, only one uncollapsed group will be allowed.',
  icon: 'fa fa-caret-down',
})
@Component({
  selector: 'fhng-accordion',
  templateUrl: './accordion.component.html',
  styleUrls: ['./accordion.component.scss'],
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
      useExisting: forwardRef(() => AccordionComponent),
    },
    {
      provide: GroupingComponentC,
      useExisting: forwardRef(() => AccordionComponent),
    },
  ],
})
export class AccordionComponent extends GroupingComponentC<PanelGroupComponent> {
  width: string = BootstrapWidthEnum.MD12;

  /**
   *  If `true`, only one panel could be opened at a time.
   *
   *  Opening a new panel will close others.
   */
  public closeOtherPanels: boolean = true;

  @Input() public activeGroup: number = 0;
  @Output() onGroupChange = new EventEmitter();

  @Input() public iconOpened: string = null;
  @Input() public iconClosed: string = null;

  @ContentChildren(PanelGroupComponent)
  public subcomponents: QueryList<PanelGroupComponent> =
    new QueryList<PanelGroupComponent>();

  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  public getSubcomponentInstance(): new (
    ...args: any[]
  ) => PanelGroupComponent {
    return PanelGroupComponent;
  }

  public changeTab(index: number): void {
    this.activeGroup = index;
    this.onGroupChange.emit(this.activeGroup);
  }

  public updateSubcomponent = (
    subcomponent: PanelGroupComponent,
    index: number
  ) => {
    subcomponent.collapsible = true;
    subcomponent.panelToggle.subscribe((value) => {
      this._closeOthers(value.id);
    });

    subcomponent.accordion = true; //Tell PanelGroupcomponent that is inside accordion
    subcomponent.mb3 = false; // Remove bottom mirgins
    subcomponent.hostCard = true; //Add card class to host element
    subcomponent.hostWidth = ''; //Remove width clsss from host element (remove padding)
    subcomponent.borderVisible = true; // Turn on borders.

    subcomponent.iconClosed = this.iconClosed;
    subcomponent.iconOpened = this.iconOpened;
    subcomponent.collapsed = index !== this.activeGroup;
  };

  private _closeOthers(panelId: string) {
    this.subcomponents.forEach((panel) => {
      if (panel.innerId !== panelId) {
        panel.collapsed = true;
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
