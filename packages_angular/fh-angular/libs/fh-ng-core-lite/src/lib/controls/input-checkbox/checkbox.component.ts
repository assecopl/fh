import {
  Component,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {InputTypeEnum} from '../../models/enums/InputTypeEnum';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from "../../models/componentClasses/FhngComponent";

@Component({
  selector: '[fhng-input-checkbox]',
  templateUrl: './checkbox.component.html',
  styleUrls: ['./checkbox.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => CheckboxComponent),
    },
  ],
})
export class CheckboxComponent
  extends FhngReactiveInputC
  implements OnInit {
  // @HostBinding('class.form-check')
  // formCheck: boolean = true;

  @Input()
  public checked: boolean = false;

  @Output()
  public selectedCheckbox: EventEmitter<CheckboxComponent> =
    new EventEmitter<CheckboxComponent>();

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.labelPosition = 'RIGHT';
  }

  public override width: string = BootstrapWidthEnum.MD12;

  //public class:string = "";

  override ngOnInit() {
    super.ngOnInit();

    this.inputType = InputTypeEnum.checkbox;
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
