import {Directive, ElementRef, Input, OnInit, TemplateRef, ViewContainerRef} from '@angular/core';
import {AvailabilityUtils} from '../AvailabilityUtils';
import {AvailabilityEnum} from '../enums/AvailabilityEnum';


/**
 * Avaliability Directive for fhng components tahat clear view if component is hidden;
 */
@Directive({
  selector: '[availabilityView]'
})
export class FhngAvailabilityDirective implements OnInit {

  public _availability: AvailabilityEnum = AvailabilityEnum.EDIT;


  /**
   * Setter that will transform string values to AvailabilityEnum values.
   * @param value
   */
  @Input()
  public set availabilityView(value: AvailabilityEnum | string) {
    this._availability = AvailabilityUtils.stringToEnum(value);
    if (!AvailabilityUtils.isHidden(this._availability)) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    } else {
      this.viewContainer.clear();
    }
  }


  public htmlElement: ElementRef;

  constructor(private element: ElementRef,
              private templateRef: TemplateRef<any>,
              private viewContainer: ViewContainerRef
  ) {
  }

  ngOnInit(): void {
  }

}

