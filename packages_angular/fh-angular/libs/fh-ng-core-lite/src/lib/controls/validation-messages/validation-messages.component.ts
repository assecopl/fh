import {AfterContentChecked, AfterViewInit, Component, Host, Injector, Input, OnInit, SkipSelf,} from '@angular/core';
import {
  DocumentedComponent,
  FieldValidationResult,
  IValidatedComponent,
  PresentationStyleEnum,
  TypeUtils,
  ValidationResults,
} from '@fhng/ng-core';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FormComponent} from '../form/form.component';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {Subscription} from 'rxjs';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';

@DocumentedComponent({
  category: DocumentedComponent.Category.INPUTS_AND_VALIDATION,
  value:
    'Component responsible for displaying field, where use can set only number.',
  icon: 'fa fa-exclamation',
})
@Component({
  selector: 'fhng-validation-messages',
  templateUrl: './validation-messages.component.html',
  styleUrls: ['./validation-messages.component.scss'],
})
export class ValidationMessagesComponent
  extends FhngHTMLElementC
  implements OnInit, AfterViewInit, AfterContentChecked {
  errors: FieldValidationResult[] = [];
  _errors: any;

  errorLevel: PresentationStyleEnum = PresentationStyleEnum.BLOCKER;

  @Input()
  level: string;

  @Input()
  strictLevel: boolean = false;

  @Input()
  public componentIds: any | '*' = '*';

  public components: (FhngComponent & IValidatedComponent)[] = [];

  private mySubscription: Subscription = new Subscription();

  constructor(
    public injector: Injector,
    @Host() @SkipSelf() private iForm: FormComponent,
    public validationHandler: ValidationResults
  ) {
    super(injector, null);

    if (PresentationStyleEnum[this.level]) {
      this.errorLevel = PresentationStyleEnum[this.level];
    }

    this.width = BootstrapWidthEnum.MD12;
  }

  ngOnInit(): void {
    super.ngOnInit();
    //Subscribe to ValidationResultsCHanges
    //FIXME Zoptymalizować aby ograniczyć liczbę wywołań
    this.mySubscription =
      this.validationHandler.fieldsValidationResultsSubject.subscribe(
        (value) => {
          this.processCurrentErrors();
        }
      );
  }

  ngOnDestroy(): void {
    // FIXME unsubscribe powoduje ze formularz na powrocie nie pokazuje sie!
    // bez unsubscribe kolejne zakladki dodaja coraz wiecej obserwatorow!
    this.mySubscription.unsubscribe();
  }

  protected getErrorsForComponent(
    component: FhngComponent,
    errors: FieldValidationResult[] = [],
    validationErrors: FieldValidationResult[] = null
  ): FieldValidationResult[] {
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
        if (component instanceof FhngReactiveInputC) {
          if (component.control.disabled) {
            component.elementRef.nativeElement.classList.add('input-invalid');
          } else {
            component.elementRef.nativeElement.classList.remove(
              'input-invalid'
            );
          }
        }
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
    this.errors = [];
    const allErrors = this.validationHandler.getValidationErrors();
    if (allErrors.length > 0 && this.components && this.components.length > 0) {
      this.components.forEach((component) => {
        const componentErrors = this.getErrorsForComponent(
          component,
          [],
          allErrors
        );
        this.errors = this.errors.concat(componentErrors);
      });
    } else {
      this.errors = this.componentIds == '*' ? allErrors : [];
    }
  }

  getBootstrapLevel() {
    switch (this.errorLevel) {
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

  ngAfterContentChecked(): void {
  }

  ngAfterViewInit() {
    super.ngAfterViewInit();

    if (this.componentIds === '*') {
      //
      this.components = null;
    } else if (TypeUtils.isObject(this.componentIds)) {
      //Obsługujemy obiekt kontrolki FHNG.
      this.components = [this.componentIds];
    } else if (TypeUtils.isArray(this.componentIds)) {
      //Obsługujemy listę kontrolek FHNG.
      this.components = this.componentIds;
    } else if (typeof this.componentIds === 'string') {
      //obsługujemy liste ID kontrolek
      const ids = this.componentIds.replace(/ /g, '').split(',');

      this.components = this.iForm.findFhngComponents(ids, []);
    }

    //FIXME Think off better solution. setTimeout to prevent "Expresion changed after it was Checked."
    setTimeout(() => {
      this.processCurrentErrors();
    }, 0);
  }
}
