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
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {DomSanitizer} from '@angular/platform-browser';
import {FhMLService} from '../../service/fh-ml.service';

@Component({
  selector: 'fhng-panel-group',
  templateUrl: './panel-group.component.html',
  styleUrls: ['./panel-group.component.scss'],
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
      useExisting: forwardRef(() => PanelGroupComponent),
    },
  ],
})
export class PanelGroupComponent extends FhngHTMLElementC implements OnInit {
  public override width: string = BootstrapWidthEnum.MD12;

  public accordion: boolean = false;

  @Input()
  public collapsed: boolean = false;

  @Input()
  public collapsible: boolean = false;

  @Input()
  public borderVisible: boolean = false;

  @Input()
  public iconOpened: string = 'fa-chevron-down';

  @Input()
  public iconClosed: string = 'fa-chevron-up';

  @Output()
  public headerClick: EventEmitter<boolean> = new EventEmitter<boolean>();

  public isHeaderCLickUsed: boolean = false;

  /**
   * If set to true panel-group will fill its header with specific component from its content.
   * Simply add css class ("panel-header") to component which you want to be placed inside header.
   */
  @Input()
  public customHeader: boolean = false;

  @HostBinding('class.mb-3')
  public override mb3: boolean = true;

  @HostBinding('class.card')
  public hostCard: boolean = false;

  @Output()
  public panelToggle = new EventEmitter<{
    id: string;
    collapsed: boolean;
  }>();

  constructor(
    public override injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    private sanitizer: DomSanitizer
  ) {
    super(injector, parentFhngComponent);
  }

  override ngOnInit() {
    super.ngOnInit();
    let fhngml = this.injector.get(FhMLService);
    this.label = fhngml.transform(this.label);
    this.processCollapsible();

    /**
     * Since EventEmiter extends Subject wchich extends Observable
     * we can check if there is any observer/subsribers.
     */
    this.isHeaderCLickUsed = this.headerClick.observers.length > 0;

    if (this.width) {
      this.processWidth(this.width);
    }
  }

  processCollapsible(): void {
    if (String(this.collapsible) === 'false') {
      this.collapsible = false;
    } else if (String(this.collapsible) === 'true') {
      this.collapsible = true;
    }
  }

  collapseToggle(): void {
    this.panelToggle.emit({id: this.innerId, collapsed: !this.collapsed});
    if (this.collapsed) {
      this.expandPanelGroup();
    } else {
      this.collapsePanelGroup();
    }
  }

  collapsePanelGroup(): void {
    // podmiana ikonek
    this.collapsed = true;
  }

  expandPanelGroup(): void {
    // podmiana ikonek
    this.collapsed = false;
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: any) {
    super.mapAttributes(data);

    this.label = data.label;

    console.log('PanelGroup', data);
  }
}
