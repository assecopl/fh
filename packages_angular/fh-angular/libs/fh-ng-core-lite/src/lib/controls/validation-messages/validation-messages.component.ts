import {
  AfterContentChecked,
  AfterViewInit,
  Component, forwardRef,
  Host,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  SkipSelf,
} from '@angular/core';

import {TypeUtils} from "../../Base";
import {PresentationStyleEnum} from "../../models/enums/PresentationStyleEnum";
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FormComponent} from '../form/form.component';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {of, Subscription} from 'rxjs';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

interface IValidateMessagesDataAttribute extends IDataAttributes {
  level: string,
  componentsIds: string[],
  strictLevel: boolean,
  htmlAccessibilityRole: string
}

interface IValidateMessage {
  elementId: string,
  elementLabel?: string,
  message: string
}

@Component({
  selector: 'fhng-validation-messages',
  templateUrl: './validation-messages.component.html',
  styleUrls: ['./validation-messages.component.scss'],
  providers: [
    { provide: FhngComponent, useExisting: forwardRef(() => ValidateMessagesComponent)}
  ]
})
export class ValidateMessagesComponent
  extends FhngHTMLElementC
  implements OnInit, OnDestroy {

  @Input()
  public componentIds: any | '*' = '*';

  public htmlAccessibilityRole: string = '';

  @Input()
  public level: PresentationStyleEnum = PresentationStyleEnum.BLOCKER;

  @Input()
  public strictLevel: boolean = false;

  @Input()
  public validateMessages: IValidateMessage[] = [];


  // private _components: FhngComponent[] = [];

  // private _mySubscription: Subscription = new Subscription();

  constructor(
    public override injector: Injector,
    @SkipSelf() public iForm: FormComponent,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
    this.width = BootstrapWidthEnum.MD12;
  }

  public override ngOnInit(): void {
    super.ngOnInit();
  }

  public override ngOnDestroy(): void {
    super.ngOnDestroy();

  }

  // protected getErrorsForComponent(
  //   component: FhngComponent & any,
  //   errors: any[] = [], //FieldValidationResult
  //   validationErrors: any[] = null //FieldValidationResult
  // ): any[] { //FieldValidationResult
  //   if (validationErrors) {
  //     const componentErrors = validationErrors.filter(
  //       (error) =>
  //         (component.id === error.knwonFhngCompomentId &&
  //           (error.knwonFhngCompoment == null ||
  //             error.knwonFhngCompoment.name == component.name ||
  //             error.knwonFhngCompoment.name == null)) || // zgloszenie bledu do id kontrolki
  //         component === error.knwonFhngCompoment
  //     );
  //
  //     if (componentErrors.length > 0) {
  //       errors = errors.concat(componentErrors);
  //       // if (component instanceof FhngReactiveInputC) {
  //         if (component.control.disabled) {
  //           component.elementRef.nativeElement.classList.add('input-invalid');
  //         } else {
  //           component.elementRef.nativeElement.classList.remove(
  //             'input-invalid'
  //           );
  //         }
  //       // }
  //     }
  //   }
  //   if (component.childFhngComponents) {
  //     component.childFhngComponents.forEach((child) => {
  //       errors = this.getErrorsForComponent(child, errors);
  //     });
  //   }
  //   return errors;
  // }
  public getBootstrapLevel() {
    switch (this.level) {
      case PresentationStyleEnum.BLOCKER:
      case PresentationStyleEnum.ERROR:
        return 'alert alert-danger';
      case PresentationStyleEnum.INFO:
        return 'alert alert-info';
      case PresentationStyleEnum.OK:
      case PresentationStyleEnum.WARNING:
        return 'alert alert-alert';
      default:
        return 'alert';
    }
  }

  public override mapAttributes(data: IValidateMessagesDataAttribute): void {
    super.mapAttributes(data);

    if (data.level) {
      this.level = data.level as PresentationStyleEnum;
    }

    console.log('VM:message', data, this)
  }


}
