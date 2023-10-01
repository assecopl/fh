import {
  AfterContentInit,
  Component, EventEmitter, forwardRef, Host, Injector,
  Input,
  OnChanges,
  OnInit, Optional, Output,
  QueryList, SimpleChanges, SkipSelf,
  ViewChildren,
} from '@angular/core';
import {GroupingComponentC} from "../../models/componentClasses/GroupingComponentC";
import {RadioOptionComponent} from "../radio-option/radio-option.component";
import {DocumentedComponent, IForm} from "@ewop/ng-core";
import {BootstrapWidthEnum} from "../../models/enums/BootstrapWidthEnum";
import {EwopInputWithListC} from '../../models/componentClasses/EwopInputWithListC';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {EwopReactiveInputC} from '../../models/componentClasses/EwopReactiveInputC';

//FIXME - Przebudować i poprawić - teraz nie zadziała
@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value: "Radio Options Group aggregates single radio components",
  icon: "fa-fw fa fa-ellipsis-v"
})
@Component({
  selector: 'ewop-radio-options-group',
  templateUrl: './radio-options-group.component.html',
  styleUrls: ['./radio-options-group.component.scss'],
  providers: [
    /**
     * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
     */
    EwopAvailabilityDirective,
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Ewop.
     */
    {provide: EwopComponent, useExisting: forwardRef(() => RadioOptionsGroupComponent)}
  ]
})
export class RadioOptionsGroupComponent extends EwopReactiveInputC implements OnInit, AfterContentInit, OnChanges {

  public values: Array<any> = [];

  @Input('values')
  public set _values(value: Array<any> | string | String | Map<any, any>) {
    if (value instanceof String || typeof value === 'string') {
      this.values = value.split("|");
    } else if (value instanceof Map) {
      this.values = ["MultiValueMap not supported yet"]
    } else {
      this.values = value ? value : []
    }
  }

  //Used for static values mappings
  @Input()
  public value: any = null;
  @Output()
  public valueChange: EventEmitter<any> = new EventEmitter<any>();

  @Input()
  public groupName: string;

  @ViewChildren(RadioOptionComponent)
  public subcomponents: QueryList<RadioOptionComponent> = new QueryList<RadioOptionComponent>();

  public radioOptionsGroupValues: string[] = [];


  constructor(public injector: Injector,
              @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
              @Optional() @Host() @SkipSelf() iForm: IForm<any>
  ) {
    super(injector, parentEwopComponent, iForm);
    this.width = BootstrapWidthEnum.MD3;
    this.groupName = this.groupName ? this.groupName : this.innerId;
  }

  ngOnInit() {
    super.ngOnInit();

  }

  ngAfterContentInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }

  public processRadioSelection(option) {
    const selectedRadioOption = option.target;

    this.subcomponents.forEach(radioOption => {
      if (radioOption.innerId !== selectedRadioOption.id && radioOption.checked) {
        radioOption.checked = false;
      }
    });
  }

}
