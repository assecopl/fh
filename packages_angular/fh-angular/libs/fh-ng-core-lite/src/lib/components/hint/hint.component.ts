import {Component, ElementRef, Input, OnInit} from "@angular/core";
import {SafeHtml} from "@angular/platform-browser";
import {HintPlacement, HintType} from "../../models/componentClasses/FhngHTMLElementC";

@Component({
  selector: '[fhng-hint]',
  templateUrl: './hint.component.html',
  styleUrls: ['./hint.component.scss']
})
export class HintComponent implements OnInit {
  @Input()
  public hintTrigger:'HOVER' | 'FOCUS' | 'HOVER FOCUS' | string = 'HOVER';

  @Input()
  public hintType: HintType = HintType.STANDARD_POPOVER;

  @Input()
  public hintInputGroup: boolean = false;

  @Input()
  public hintPlacement: HintPlacement = HintPlacement.TOP;

  @Input()
  public hintTitle: string = null;

  @Input('fhng-hint')
  public hint: string = '';

  public hintAriaLabel = '';

  private _tooltipOptions = {
    placement: this.hintPlacement,
    title: this.hintTitle,
    trigger: this.hintTrigger,
    html: true,
    boundary: 'window'
  }

  constructor(private _element: ElementRef) {
  }

  public ngOnInit() {
    this._tooltipOptions
  }
}
