import {Component, forwardRef, Host, Injector, Input, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngInputWithListC} from '../../models/componentClasses/FhngInputWithListC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent, FORM_VALUE_ATTRIBUTE_NAME, IDataAttributes} from "@fh-ng/forms-handler";

interface ISelectOneMenuDataAttributes extends IDataAttributes {
  rawOptions: string[];
  rawValue: string;
  require: boolean;
  inputSize: number;
  selectedIndex: number;
  emptyValue: boolean;
  emptyLabel: boolean;
  keepRemovedValue: boolean;
  onChange: string;
}

@Component({
  selector: 'fhng-select-one-menu',
  templateUrl: './select-one-menu.component.html',
  styleUrls: ['./select-one-menu.component.scss'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => SelectOneMenuComponent),
    },
  ],
})

export class SelectOneMenuComponent
  extends FhngInputWithListC
  implements OnInit {

  @Input()
  public rawOptions: string[] = [];

  @Input()
  public require: boolean;

  @Input()
  public selectedIndex: number = -1;

  @Input()
  public keepRemovedValue: boolean;

  @Input()
  public emptyLabelText: string = '';

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.width = BootstrapWidthEnum.MD3;
  }

  public override ngOnInit(): void {
    super.ngOnInit();
  }

  public override ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: ISelectOneMenuDataAttributes): void {
    super.mapAttributes(data);

    this.rawOptions = data.rawOptions || this.rawOptions;
    this.rawValue = data.rawValue || this.rawValue;
    this.require = data.require || this.require;
    this.inputSize = data.inputSize || this.inputSize;
    this.selectedIndex = typeof data.selectedIndex !== 'undefined' ? data.selectedIndex : this.selectedIndex;
    this.emptyValue = data.emptyValue || this.emptyValue;
    this.emptyLabel = data.emptyLabel || this.emptyLabel;
    this.keepRemovedValue = data.keepRemovedValue || this.keepRemovedValue;
  }

  public override extractChangedAttributes() {
    let attrs = {};
    if (this.valueChanged) {
      attrs[FORM_VALUE_ATTRIBUTE_NAME] = this.rawValue;
      this.valueChanged = false;
    }

    return attrs;
  };

  public override onChangeEvent() {
    if (this.onChange && !this.disabled) {
      this.fireEventWithLock('onChange', this.onChange);
    }
  }

  public override updateModel(event) {
    this.valueChanged = true;

    if (!this.disabled) {
      this.rawValue = this.rawOptions.findIndex(option => option === event.target.value);
      this.selectedIndex = this.rawValue;
    }
  };

  public onSelectChangeEvent($event: Event): void {
    $event.preventDefault();

    this.updateModel($event);
    this.onChangeEvent();
  }

  emptyValueClickEvent() {
    if (this.emptyValue) {
      this.selectedIndex = -1;
      this.rawValue = -1;
      this.changesQueue.queueValueChange(this.rawValue);
      if (this.onChange) {
        this.fireEventWithLock('onChange', this.onChange);
      }
    }
  }


}
