import {
  AfterContentInit,
  Component,
  forwardRef,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {TabComponent} from '../tab/tab.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from "@fh-ng/forms-handler";
import {TabContainerComponent} from "../tab-container/tab-container.component";

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
export class WizardComponent extends TabContainerComponent implements OnInit, AfterContentInit {

  @Input()
  public override width: string = BootstrapWidthEnum.MD12;

  public ulStyleClasses: string[] = ['nav', 'nav-tabs', 'wizard-tabs'];

  public listStyleClasses: string[] = ['nav-item'];

  public activeLinkStyleClasses: string[] = ['nav-link', 'active'];

  public isSubWizard:boolean = false;

  public subWizards:Map<number, WizardComponent> = new Map<number, WizardComponent>();

  @Input()
  protected subWizardSteps : number = null;



  @HostBinding('class.wizard')
  public wizardClass: boolean = true;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    @Optional() @SkipSelf() protected parentWizardComponent: WizardComponent,
    @Optional() @SkipSelf() protected parentWizardTabComponent: TabComponent
  ) {
    super(injector, parentFhngComponent);
    this.horizontalAlign = 'CENTER';
  }

  public override ngOnInit() {
    super.ngOnInit();
    this.processSubWizard();
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

  /**
   * Wizard can also be a sub wizard. In that case nav links of this wizard won't be shown.
   * It will act as sub steps of his parent Wizard and Tab.
   * @protected
   */
  protected processSubWizard():void {

    if(this.parentWizardComponent){
      this.isSubWizard = true;
      console.log("WIzard register", this.parentWizardTabComponent.tabIdx)
      this.parentWizardComponent.subWizards.set(this.parentWizardTabComponent.tabIdx, this);
    }
  }

}
