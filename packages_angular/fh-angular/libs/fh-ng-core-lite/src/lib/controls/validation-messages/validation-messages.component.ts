import {
  AfterContentChecked,
  AfterViewInit,
  Component, forwardRef,
  Host,
  Injector,
  Input,
  OnDestroy,
  OnInit,
  SkipSelf,
} from '@angular/core';
// import {
//   // DocumentedComponent,
//   FieldValidationResult,
//   IValidatedComponent,
//   TypeUtils,
//   ValidationResults,
// } from '@fhng/ng-core';
import {TypeUtils} from "../../Base";
import {PresentationStyleEnum} from "../../models/enums/PresentationStyleEnum";
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FormComponent} from '../form/form.component';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {of, Subscription} from 'rxjs';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";


// import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';

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
  implements OnInit, AfterViewInit, AfterContentChecked, OnDestroy {

  @Input()
  public componentIds: any | '*' = '*';

  public htmlAccessibilityRole: string = '';

  @Input()
  public level: PresentationStyleEnum = PresentationStyleEnum.BLOCKER;

  @Input()
  public strictLevel: boolean = false;

  public validateMessages: IValidateMessage[] = [];

  private _components: FhngComponent[] = [];

  private _mySubscription: Subscription = new Subscription();

  constructor(
    public override injector: Injector,
    @SkipSelf() private iForm: FormComponent,
    // public validationHandler: ValidationResults
  ) {
    super(injector, null);

    this.formsManager.changesSubject.subscribe((data)=> {
      console.log('VM:testEvent:?', data)
    });

    this.width = BootstrapWidthEnum.MD12;
  }

  public override ngOnInit(): void {
    super.ngOnInit();
    //Subscribe to ValidationResultsCHanges
    //FIXME Zoptymalizować aby ograniczyć liczbę wywołań
    this._mySubscription = of().subscribe();
      // this.validationHandler.fieldsValidationResultsSubject.subscribe(
      //   (value) => {
      //     this.processCurrentErrors();
      //   }
      // );
  }

  public override ngAfterViewInit(): void {
    super.ngAfterViewInit();

    this._componentsIdsMap();
    //FIXME Think off better solution. setTimeout to prevent "Expresion changed after it was Checked."
    setTimeout(() => {
      this.processCurrentErrors();
    }, 0);
  }

  public ngOnDestroy(): void {
    // FIXME unsubscribe powoduje ze formularz na powrocie nie pokazuje sie!
    // bez unsubscribe kolejne zakladki dodaja coraz wiecej obserwatorow!
    this._mySubscription.unsubscribe();
  }

  protected getErrorsForComponent(
    component: FhngComponent & any,
    errors: any[] = [], //FieldValidationResult
    validationErrors: any[] = null //FieldValidationResult
  ): any[] { //FieldValidationResult
    if (validationErrors) {
      const componentErrors = validationErrors.filter(
        (error) =>
          (component.id === error.knwonFhngCompomentId &&
            (error.knwonFhngCompoment == null ||
              error.knwonFhngCompoment.name == component.name ||
              error.knwonFhngCompoment.name == null)) || // zgloszenie bledu do id kontrolki
          component === error.knwonFhngCompoment
      );

      if (componentErrors.length > 0) {
        errors = errors.concat(componentErrors);
        // if (component instanceof FhngReactiveInputC) {
          if (component.control.disabled) {
            component.elementRef.nativeElement.classList.add('input-invalid');
          } else {
            component.elementRef.nativeElement.classList.remove(
              'input-invalid'
            );
          }
        // }
      }
    }
    if (component.childFhngComponents) {
      component.childFhngComponents.forEach((child) => {
        errors = this.getErrorsForComponent(child, errors);
      });
    }
    return errors;
  }

  processCurrentErrors() {
    // this.errors = [];
    // const allErrors = [];//this.validationHandler.getValidationErrors();
    // if (allErrors.length > 0 && this.components && this.components.length > 0) {
    //   this.components.forEach((component) => {
    //     const componentErrors = this.getErrorsForComponent(
    //       component,
    //       [],
    //       allErrors
    //     );
    //     // this.errors = this.errors.concat(componentErrors);
    //   });
    // } else {
    //   // this.errors = this.componentIds == '*' ? allErrors : [];
    // }
  }

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

  public ngAfterContentChecked(): void {
  }

  public override mapAttributes(data: IValidateMessagesDataAttribute): void {
    super.mapAttributes(data);

    this.level = data.level as PresentationStyleEnum;
    this.componentIds = data.componentsIds;

    this._componentsIdsMap();

    console.log('VM:message', data, this)
  }

  private _componentsIdsMap (): void {
    if (this.componentIds === '*') {
      this._components = [];
    } else if (TypeUtils.isObject(this.componentIds)) {
      //Obsługujemy obiekt kontrolki FHNG.
      this._components = [this.componentIds];
    } else if (TypeUtils.isArray(this.componentIds)) {
      //Obsługujemy listę kontrolek FHNG.
      this._components = this.componentIds;
    } else if (typeof this.componentIds === 'string') {
      //obsługujemy liste ID kontrolek
      const ids = this.componentIds.replace(/ /g, '').split(',');

      this._components = this.iForm.findFhngComponents(ids, []);
    }
  }
}
