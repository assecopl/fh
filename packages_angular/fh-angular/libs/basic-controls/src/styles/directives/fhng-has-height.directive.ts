import {Directive, ElementRef, Input, OnInit} from '@angular/core';

/**
 * Avaliability Html Element Directive sets HTML ELement for parent EwopAvailabilityDirective
 * Put this tag on html element to add css classes on availability process.
 */
@Directive({
  selector: '[hasHeight]'
})
export class FhngHasHeightDirective implements OnInit {

  public _height: string = null;

  @Input('hasHeight')
  public set height(value: string) {
    this._height = value;
    this.setStyle()
  }

  constructor(protected htmlElement: ElementRef,
  ) {
  }


  public ngOnInit(): void {
  }

  setStyle(): void {
    this.htmlElement.nativeElement.classList.remove('hasHeight');

    if (this._height) {
      this.htmlElement.nativeElement.classList.add('hasHeight');
    }


  }

}
