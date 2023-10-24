import {
  Component,
  forwardRef,
  Host,
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
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {RepeaterComponent} from '../repeater/repeater.component';

@Component({
  selector: 'fhng-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    // FhngAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => GroupComponent)},
  ],
})
export class GroupComponent extends FhngHTMLElementC implements OnInit {
  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);

    this.width = BootstrapWidthEnum.MD12;
    if (parentFhngComponent instanceof RepeaterComponent) {
      this.mb3 = false;
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
}
