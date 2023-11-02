import {Component, forwardRef, Host, Injector, Input, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngInputWithListC} from '../../models/componentClasses/FhngInputWithListC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

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
  public selectedIndex: number;

  @Input()
  public keepRemovedValue: boolean;

  @Input()
  public onChange: string;

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

    this.rawOptions = data.rawOptions;
    this.rawValue = data.rawValue;
    this.require = data.require;
    this.inputSize = data.inputSize;
    this.selectedIndex = data.selectedIndex;
    this.emptyValue = data.emptyValue;
    this.emptyLabel = data.emptyLabel;
    this.keepRemovedValue = data.keepRemovedValue;
  }

  public onChangeEvent ($event: Event): void {
    $event.stopPropagation();

    this.value = this.rawValue = ($event.target as any).value;

    if (this.onChange) {
      if (this.formId === 'FormPreview') {
        this.fireEvent('onChange', this.onChange);
      } else {
        this.fireEventWithLock('onChange', this.onChange);
      }
    }
  }
}
