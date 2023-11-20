import {
  AfterContentInit,
  Component,
  EventEmitter,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {ButtonComponent} from '../button/button.component';
import {GroupingComponentC} from '../../models/componentClasses/GroupingComponentC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngButtonGroupComponent, FhngComponent,} from '../../models/componentClasses/FhngComponent';

@Component({
  selector: 'fhng-button-group',
  templateUrl: './button-group.component.html',
  styleUrls: ['./button-group.component.scss'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => ButtonGroupComponent),
    },
    {
      provide: FhngButtonGroupComponent,
      useExisting: forwardRef(() => ButtonGroupComponent),
    },
  ],
})
export class ButtonGroupComponent
  extends GroupingComponentC<ButtonComponent>
  implements OnChanges, OnInit, AfterContentInit {

  public override mb2 = false;

  public override width = BootstrapWidthEnum.MD12;

  @Input()
  public activeButton: number;

  @HostBinding('class.breadcrumbs')
  @Input()
  public breadcrumbs: boolean = false;

  public initialized: boolean = false;

  @Input()
  public selectedButton: ButtonComponent;

  public activeButtonComponent: ButtonComponent = null;

  public activeButtonComponentIndex: number;

  @Output()
  public selectedButtonGroup: EventEmitter<ButtonGroupComponent> =
    new EventEmitter<ButtonGroupComponent>();

  @HostBinding('class')
  public class: string = 'btn-group';

  @HostBinding('attr.role')
  public role: string = 'group';

  public buttonSubcomponents: ButtonComponent[] = [];

  public updateSubcomponent: (
    subcomponent: ButtonComponent,
    index: number
  ) => null;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  public getSubcomponentInstance(): new (...args: any[]) => ButtonComponent {
    return ButtonComponent;
  }

  public override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
    // if (
    //   changes.activeButton &&
    //   this.activeButton !== changes.activeButton.currentValue
    // ) {
    //   this.activeButton = changes.activeButton.currentValue;
    //   this.setActiveButton();
    // }
  }

  public override ngOnInit() {
    super.ngOnInit();

    //Listen to each button selected event emiter and process selection in group.
    if (this.buttonSubcomponents.length > 0) {
      if (this.breadcrumbs) {
        this.activeButton = this.buttonSubcomponents.length - 1;
      }
      this.buttonSubcomponents.forEach((button) => {
        if (this.breadcrumbs) {
          button.bootstrapStyle = 'btn-link';
          button.breadcrumb = true;
        } else {
          button.selectedButton.subscribe((val) => {
            this.processActiveButton(val);
          });
        }
      });
      this.setActiveButton();
    }
    this.initialized = true;
  }

  public override ngAfterContentInit(): void {
    this.processButtonsWidth();
  }

  private processButtonsWidth() {
    this.buttonSubcomponents.forEach((button) => {
      button.hostWidth = button.hostWidth.replace('col-md', 'md');
    });
  }

  setActiveButton() {
    if (this.activeButton < this.buttonSubcomponents.length) {
      this.activeButtonComponentIndex = this.activeButton;
      this.activeButtonComponent = this.buttonSubcomponents[this.activeButton];
      this.activeButtonComponent.active = true;
      this.buttonSubcomponents
        .filter((button) => button != this.activeButtonComponent)
        .forEach((button) => (button.active = false));
    }
  }

  public processActiveButton(selectedButton: ButtonComponent) {
    this.buttonSubcomponents.forEach((button, index) => {
      if (button.innerId === selectedButton.innerId) {
        button.active = !button.active;
        this.activeButtonComponent = button;
        this.activeButtonComponentIndex = index;
      } else {
        button.active = false;
      }
    });

    this.selectedButtonGroup.emit(this);
  }
}
