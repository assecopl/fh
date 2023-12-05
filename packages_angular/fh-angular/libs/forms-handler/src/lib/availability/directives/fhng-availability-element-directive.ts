import {Directive, ElementRef, Input, OnInit} from '@angular/core';
import {AvailabilityEnum} from "../enums/AvailabilityEnum";
import {AvailabilityUtils} from "../AvailabilityUtils";

/**
 * Avaliability Html Element Directive sets HTML ELement for parent EwopAvailabilityDirective
 * Put this tag on html element to add css classes on availability process.
 */
@Directive({
  selector: '[availabilityHtmlElement]'
})
export class FhngAvailabilityElementDirective implements OnInit {

  public _availability: AvailabilityEnum = AvailabilityEnum.EDIT;
  @Input('availabilityHtmlElement')
  public set availability(value: AvailabilityEnum | string) {
    this._availability = AvailabilityUtils.stringToEnum(value);
    AvailabilityUtils.setHtmlElementAvailability(this.htmlElement.nativeElement, this._availability)
  }

  constructor(protected htmlElement: ElementRef,
  ) {
  }


  public ngOnInit(): void {

  }


}
