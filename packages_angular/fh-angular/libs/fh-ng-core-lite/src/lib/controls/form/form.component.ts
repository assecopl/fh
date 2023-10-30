import {
  Component,
  forwardRef,
  HostBinding,
  inject,
  Injector,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhChanges, FormsManager} from "../../Socket/FormsManager";
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";
import {Subscription} from 'rxjs';
import {ContainerComponent} from "../container/container.component";

@Component({
  selector: 'fh-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => FormComponent)},
  ],
})
export class FormComponent extends FhngComponent implements OnInit, OnChanges, OnDestroy {
  // @Input() public model: any = null;
  @Input() public label: string = null;
  @Input() public hideHeader: boolean = false;
  @Input() public formType: string = 'STANDARD';

  @HostBinding('class.navbar-nav')
  header: boolean = false;

  @HostBinding('class.card')
  cssCard: boolean = true;

  @HostBinding('class.fc')
  cssFc: boolean = true;

  private changesSubjectSubscription: Subscription;

  public container: ContainerComponent = inject(ContainerComponent);

  constructor(
    public override injector: Injector, private formManager: FormsManager
  ) {
    super(injector, null);
    // this.formManager.
  }

  ngOnDestroy(): void {
    this.formManager.unregisterForm(this);
    this.changesSubjectSubscription.unsubscribe();
  }

  override ngOnInit() {
    super.ngOnInit();
    this.formManager.registerForm(this);
    this.changesSubjectSubscription = this.formManager.changesSubject.subscribe((changes: FhChanges) => {
      if (changes[this.id]) {
        const componentChanges = changes[this.id]
        componentChanges.forEach(change => {
          const component = this.findFhngComponent(change.formElementId);
          if (component) {
            component.data = change.changedAttributes;
          }
        })

      }
    })

    if (this.formType == 'HEADER') {
      this.header = true;
      this.cssCard = false
    }

  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.formType == 'HEADER') {
      this.header = true;
      this.cssCard = false
    }
  }

  public override mapAttributes(data: IDataAttributes): void {
    super.mapAttributes(data)
    this.formType = data.formType
  }
}
