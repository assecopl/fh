import {Directive, ElementRef, Input, OnInit} from '@angular/core';
import {AvailabilityUtils} from "../AvailabilityUtils";
import {AvailabilityEnum} from "../enums/AvailabilityEnum";


/**
 * Avaliability Html Element Directive sets HTML ELement for parent EwopAvailabilityDirective
 * Tag element where [formControl] is passed
 */
@Directive({
  selector: '[availabilityInput]'
})
export class FhngAvailabilityInputDirective implements OnInit {

  public _availability: AvailabilityEnum = AvailabilityEnum.EDIT;
  @Input('availabilityInput')
  public set availability(value: AvailabilityEnum | string) {
    this._availability = AvailabilityUtils.stringToEnum(value);
  }

  constructor(protected htmlElement: ElementRef,
  ) {

  }

  public ngOnInit(): void {
    AvailabilityUtils.setInputELementAvaialbility(this.htmlElement.nativeElement, this._availability)
  }


}
