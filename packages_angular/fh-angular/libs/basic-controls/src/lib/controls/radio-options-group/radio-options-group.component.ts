import {
  AfterContentInit,
  Component,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  Output,
  QueryList,
  SimpleChanges,
  SkipSelf,
  ViewChildren,
} from '@angular/core';
import {RadioOptionComponent} from '../radio-option/radio-option.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {FhngComponent, IDataAttributes} from "@fh-ng/forms-handler";

//FIXME - Przebudować i poprawić - teraz nie zadziała
@Component({
  selector: '[fhng-radio-options-group]',
  templateUrl: './radio-options-group.component.html',
  styleUrls: ['./radio-options-group.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => RadioOptionsGroupComponent),
    },
  ],
})
export class RadioOptionsGroupComponent
  extends FhngReactiveInputC
  implements OnInit, AfterContentInit, OnChanges {
  public values: Array<any> = [];

  @Input('values')
  public set _values(value: Array<any> | string | String | Map<any, any>) {
    if (value instanceof String || typeof value === 'string') {
      this.values = value.split('|');
    } else if (value instanceof Map) {
      this.values = ['MultiValueMap not supported yet'];
    } else {
      this.values = value ? value : [];
    }
  }

  //Used for static values mappings
  @Input()
  public override value: any = null;
  @Output()
  public override valueChange: EventEmitter<any> = new EventEmitter<any>();

  @Input()
  public groupName: string;

  @ViewChildren(RadioOptionComponent)
  public subcomponents: QueryList<RadioOptionComponent> =
    new QueryList<RadioOptionComponent>();

  public radioOptionsGroupValues: string[] = [];

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.width = BootstrapWidthEnum.MD3;
    this.groupName = this.groupName ? this.groupName : this.innerId;
  }

  override ngOnInit() {
    super.ngOnInit();
  }

  override ngAfterContentInit(): void {
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public processRadioSelection(option, idx) {
    if (!this.disabled) {
      this.changesQueue.queueValueChange(idx);
    }
    // const selectedRadioOption = option.target;
    // this.subcomponents.forEach((radioOption) => {
    //   if (
    //     radioOption.innerId !== selectedRadioOption.id &&
    //     radioOption.checked
    //   ) {
    //     radioOption.checked = false;
    //   }
    // });
    this.onChangeEvent();
  }

  override mapAttributes(data: IDataAttributes | any) {
    super.mapAttributes(data);

    this.values = data.rawOptions || this.values;
  }
}
