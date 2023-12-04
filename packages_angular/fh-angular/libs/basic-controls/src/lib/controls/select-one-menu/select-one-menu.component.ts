import {Component, forwardRef, Host, Injector, Input, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngInputWithListC} from '../../models/componentClasses/FhngInputWithListC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent, IDataAttributes} from "@fh-ng/forms-handler";

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

  public onSelectChangeEvent ($event: Event): void {
    $event.preventDefault();

    this.updateModel($event);
    this.onChangeEvent();
  }
}
