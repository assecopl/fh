import {
  Directive,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Optional,
  SimpleChanges,
  ViewContainerRef,
} from '@angular/core';
import {FormComponent} from "../controls/form/form.component";
import {OutputLabelComponent} from "../controls/output-label/output-label.component";
import {ComponentManager} from "../service/component-manager.service";


@Directive({
  selector: '[fhDynamicComponents]',
})
export class DynamicComponentsDirective
  implements OnInit, OnChanges, OnDestroy {

  @Input() formId: string = null;

  @Input() type: string = null;

  @Input() data: any = null;

  @Input() fhDynamicComponents?: any[] = [];

  @Input() fhNonVisualSubcomponents?: any[] = [];

  protected componentRefs: { [index: string]: any } = {};

  constructor(private viewContainerRef: ViewContainerRef,
              @Optional() private form: FormComponent,
              private componentManager: ComponentManager
  ) {
    if (form) {
      this.formId = this.form.id;
    }
  }

  ngOnDestroy(): void {
    for (let componentRefsKey in this.componentRefs) {
      this.componentRefs[componentRefsKey]?.destroy();
      this.componentRefs[componentRefsKey] = null;
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // throw new Error('Method not implemented.');
    if (changes["formId"]) {
      this.componentRefs = {};
    }
    if (changes["fhDynamicComponents"] || changes["data"]) {
      this.loadOrUpdateComponent();
    }
  }


  public loadOrUpdateComponent() {
    if (this.data) {
      if (this.type) this.data.type = this.type;
      this.fhDynamicComponents[0] = this.data;
    }


    if (this.fhDynamicComponents) {

      for (let componentRefsKey in this.componentRefs) {
        const c = this.viewContainerRef.indexOf(this.componentRefs[componentRefsKey]);
        if (c >= 0) {
          this.viewContainerRef.detach(c)
        }

      }

      let usedComponents = {};
      [...this.fhDynamicComponents, ...this.fhNonVisualSubcomponents].forEach((data, index) => {
        if (this.componentRefs[data.id]) {
          //Jezeli istnieje juz o takim ID to robie insert
          const component = this.componentRefs[data.id];
          component.instance.data = data;
          component.instance.formId = this.formId;
          component.hostView.markForCheck();
          // component.changeDetectorRef.detectChanges();
          this.viewContainerRef.insert(component.hostView, index);
          component.instance.focusOnInit();
        } else {
          this.createComponent(data, index);
        }
        usedComponents[data.id] = data.id;
      })

      for (let componentRefsKey in this.componentRefs) {
        if (!usedComponents[componentRefsKey]) {
          this.componentRefs[componentRefsKey].destroy();
          this.componentRefs[componentRefsKey] = null;
          delete this.componentRefs[componentRefsKey];
        }

      }


    }
  }

  ngOnInit(): void {
    this.loadOrUpdateComponent();
  }

  protected createComponent(data, index) {
    let componentRef = null;
    let componentType = this.componentManager.getComponentFactory(data.type);
    if (componentType) {
      componentRef =
        this.viewContainerRef.createComponent(componentType, {index: index});
      if (componentRef && componentRef.instance && componentRef.instance.mapAttributes) {
        componentRef.instance.data = data;
        componentRef.instance.formId = this.formId;
        // componentRef.hostView.markForCheck();
        // componentRef.changeDetectorRef.detectChanges();
      }
    } else {
      componentRef =
        this.viewContainerRef.createComponent<OutputLabelComponent>(
          OutputLabelComponent, {index: index}
        );
      data["value"] = "Component " + data.type + " does not exist";
      componentRef.instance.data = data;
      componentRef.instance.formId = this.formId;
      // componentRef.instance.value = "Component " + data.type + " does not exist";

    }
    this.componentRefs[componentRef.instance.id] = componentRef;
    componentRef.instance.focusOnInit();
  }


}
