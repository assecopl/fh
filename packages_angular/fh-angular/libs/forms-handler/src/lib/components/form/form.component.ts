import {
  AfterViewInit,
  Component,
  forwardRef,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Optional,
  SimpleChanges,
} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhChanges, FormsManager} from "../../Socket/FormsManager";
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";
import {Subscription} from 'rxjs';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';
import {FormComponentRef} from "./form.ref";
import {EventsManager} from "../../service/events-manager.service";
import {IForm} from "../../Base";
import {AvailabilityEnum} from '../../availability/enums/AvailabilityEnum';

@Component({
  selector: '[fh-form]',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => FormComponent)},
    {provide: FormComponentRef, useExisting: forwardRef(() => FormComponent)},

  ],
})
export class FormComponent extends FormComponentRef implements OnInit, OnChanges, OnDestroy, AfterViewInit, IForm {
  // @Input() public model: any = null;
  @Input() public label: string = null;
  @Input() public hideHeader: boolean = false;

  // @HostBinding('class.navbar-nav')
  // header: boolean = false;

  @HostBinding('class.card')
  cssCard: boolean = true;
  @HostBinding('class.form')
  cssForm: boolean = true;

  @HostBinding('class.fc')
  cssFc: boolean = true;

  @HostBinding('id')
  @Input()
  public override id: string = '';

  @HostBinding('class')
  @Input()
  public styleClasses: string = null;

  @Input()
  public modal: boolean = false;

  private changesSubjectSubscription: Subscription;

  @Input()
  public container: any = null;

  constructor(
    public override injector: Injector,
    private formManager: FormsManager,
    private eventsManager: EventsManager,
    @Optional() public activeModal: NgbActiveModal
  ) {
    super(injector, null);
  }

  accessibility: AvailabilityEnum;
  blockFocusForModal: boolean;
  blocked: boolean;
  designDeletable: boolean;
  designMode: boolean;
  effectiveFormType: 'HEADER' | 'MODAL_OVERFLOW' | 'STANDARD' | 'MODAL' | 'FLOATING' | 'MINIMAL';
  @Input()
  focusFirstElement: boolean;
  @Input()
  formType: 'HEADER' | 'MODAL_OVERFLOW' | 'STANDARD' | 'MODAL' | 'FLOATING' | 'MINIMAL';
  fromCloud: boolean;
  hintType: 'STANDARD' | 'STANDARD_POPOVER' | 'STATIC' | 'STATIC_POPOVER' | 'STATIC_POPOVER_LEFT' | 'STATIC_LEFT';
  invisible: boolean;
  @Input()
  modalSize: 'REGULAR' | 'SMALL' | 'LARGE' | 'XLARGE' | 'XXLARGE' | 'FULL';
  push: false;
  @Input()
  state: 'ACTIVE' | 'INACTIVE_PENDING' | 'INACTIVE' | 'SHADOWED' | 'HIDDEN' | 'CLOSED';
  nonVisualSubcomponents?: any[];

  override ngOnDestroy(): void {
    super.ngOnDestroy();
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
            if (Object.keys(change.changedAttributes).length > 0) {
              component.data = change.changedAttributes;
            }
            if (change.addedComponents && Object.keys(change.addedComponents).length > 0) {
              component.processAddedComponents(change.addedComponents);
            }
            if (change.removedComponents && Object.keys(change.removedComponents).length > 0) {
              component.processRemovedComponents(change.removedComponents);
            }
          }
        })
      }
    })

    this.eventsManager.focusEvent.focusSubject.subscribe(value => {
      if (value.containerId == this.container.id) {
        this.focusComponent(value.formElementId);
      }
    })

    if (this.formType == 'HEADER') {
      // this.header = true;
      this.cssCard = false
    }


  }

  override ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
    // if(changes['id'] && !changes['id'].firstChange){
    this.formManager.registerForm(this);
    // }

    if (this.formType == 'HEADER') {
      // this.header = true;
      this.cssCard = false;
    }


  }

  public override mapAttributes(data: IDataAttributes): void {
    let _container = this.container;

    super.mapAttributes(data);
    // @ts-ignore
    this.formType = data.formType;

    if (this.styleClasses && this.styleClasses.includes(",")) {
      this.styleClasses = this.styleClasses.replaceAll(',', ' ')
    }

    this.container = _container;
  }

  public override collectAllChanges() {
    var allChanges = [];
    this.childFhngComponents.forEach(function (component) {
      var changes = component.collectAllChanges();
      allChanges = allChanges.concat(changes);
    }.bind(this));
    return this.collectChanges(allChanges);
  };

  override ngAfterViewInit(): void {
    super.ngAfterViewInit();

  }

  public isFormActive(): boolean {
    return this.state == 'ACTIVE';
  }
}

