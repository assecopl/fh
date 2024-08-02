import {
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
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {RepeaterComponent} from '../repeater/repeater.component';
import {FhngComponent, IDataAttributes} from '@fh-ng/forms-handler';

@Component({
  selector: '[fhng-group]',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss'],
  providers: [

    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => GroupComponent)},
  ],
})
export class GroupComponent extends FhngHTMLElementC implements OnInit {
  @Input()
  public onClickNameEvent: string = null;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    this.valignTop = true;
    if (parentFhngComponent instanceof RepeaterComponent) {
      this.mb2 = false;
    }
  }

  @Input() class: string = ''; // override the standard class attr with a new one.

  @HostBinding('class')
  get hostClasses(): any {
    // Override host classes. Prevent adding it to Host

    const classArray = this.class.split(' ');
    const classMap = {};
    classArray.forEach((value) => {
      if (value) {
        classMap[value.trim()] = false;
      }
    });
    return classMap; //Clear Host classes.
  }

  override ngOnInit() {
    super.ngOnInit();
    //Pass external ID to availability processor
    // this.fhngAvailabilityDirective.ngOnInit();
    // this.fhngAvailabilityDirective.id = this.id;
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: IDataAttributes) {
    super.mapAttributes(data);
    this.onClickNameEvent = data.onClick || this.onClickNameEvent;
  }

  public onClickEvent($event: Event): void {


    if (this.onClickNameEvent) {
      $event.stopPropagation();
      $event.preventDefault();
      this.fireEventWithLock('onClick', this.onClickNameEvent);
    }
  }
}
