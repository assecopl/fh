import {
  Component,
  EventEmitter,
  forwardRef,
  Injector,
  Input,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {PanelGroupComponent} from '../panel-group/panel-group.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent, IDataAttributes } from '@fh-ng/forms-handler';

interface IAccordionDataAttributes extends IDataAttributes {
  iconOpened: string,
  iconClosed: string
}


@Component({
  selector: 'fhng-accordion',
  templateUrl: './accordion.component.html',
  styleUrls: ['./accordion.component.scss'],
  providers: [

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
  public override width = BootstrapWidthEnum.MD12;

  public override mb2 = false;

  /**
   *  If `true`, only one panel could be opened at a time.
   *
   *  Opening a new panel will close others.
   */
  public closeOtherPanels: boolean = true;

  @Input()
  public activeGroup: number = 0;

  @Input()
  public iconOpened: string = null;

  @Input()
  public iconClosed: string = null;

  @Output()
  public onGroupChange = new EventEmitter();

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.mb2 = false;
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

  public updateSubcomponent = (subcomponent: PanelGroupComponent, index: number): void => {
    subcomponent.collapsible = true;

    if (subcomponent.panelToggle) {
      subcomponent.panelToggle.subscribe((value) => {
        this._closeOthers(value.id);
      });
    }

    subcomponent.accordion = true; //Tell PanelGroupcomponent that is inside accordion
    subcomponent.mb2 = false; // Remove bottom mirgins
    subcomponent.hostWidth = ''; //Remove width clsss from host element (remove padding)
    subcomponent.borderVisible = false; // Turn on borders.

    subcomponent.iconClosed = this.iconClosed;
    subcomponent.iconOpened = this.iconOpened;
    subcomponent.collapsed = index !== this.activeGroup;
  }

  private _closeOthers(panelId: string) {
    this.subelements.forEach((panel) => {
      if (panel.innerId !== panelId) {
        panel.collapsed = true;
      }
    });

    this.subelements = JSON.parse(JSON.stringify(this.subelements));
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: IAccordionDataAttributes) {
    super.mapAttributes(this._updateSubElements(data));

    this.iconClosed = data.iconClosed;
    this.iconOpened = data.iconOpened;
  }

  private _updateSubElements(data: IAccordionDataAttributes): IAccordionDataAttributes {
    for (let index = 0; index < data.subelements?.length; index++) {
      if (data.subelements[index].type === 'PanelGroup') {
        this.updateSubcomponent(data.subelements[index], index);
      }
    }

    return data;
  }
}
