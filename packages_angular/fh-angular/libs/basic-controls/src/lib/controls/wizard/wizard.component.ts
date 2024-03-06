import {
  AfterContentInit,
  Component,
  ContentChildren,
  forwardRef,
  Host,
  HostBinding,
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
import {AvailabilityEnum, FhngComponent} from "@fh-ng/forms-handler";
import {TabContainerComponent} from "@fh-ng/basic-controls";

@Component({
  selector: 'fh-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => WizardComponent)},
  ],
})
export class WizardComponent
  extends TabContainerComponent
  implements OnInit, AfterContentInit {

  @Input()
  public override width: string = BootstrapWidthEnum.MD12;

  public ulStyleClasses: string[] = ['nav', 'nav-tabs', 'wizard-tabs'];

  public listStyleClasses: string[] = ['nav-item'];

  public activeLinkStyleClasses: string[] = ['nav-link', 'active'];

  @HostBinding('class.wizard')
  public wizardClass: boolean = true;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.horizontalAlign = 'CENTER';
  }

  public override ngOnInit() {
    super.ngOnInit();
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override ngAfterContentInit(): void {
    // this.subcomponentsArray = this.subcomponents.toArray();
    this.activateDefaultTab();
  }

  public getTabId(tab: TabComponent): string {
    return tab.tabId;
  }

}
