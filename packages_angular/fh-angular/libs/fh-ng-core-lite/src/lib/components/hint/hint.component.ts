import {Component, ElementRef, HostBinding, Input, OnInit, Optional, SkipSelf} from "@angular/core";
import {SafeHtml} from "@angular/platform-browser";
import {HintPlacement, HintType} from "../../models/componentClasses/FhngHTMLElementC";
import {FhngComponent, FhngGroupComponent} from "../../models/componentClasses/FhngComponent";
import {NgbTooltip, NgbTooltipModule} from "@ng-bootstrap/ng-bootstrap";

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

  private _hintIcon: string = '';

  @Input()
  public set hintIcon(value: string) {
    if (value) {
      let classList = [];

      value.split(' ').forEach(value => classList.push(value.trim()))

      this._hintIcon = classList.join(' ');
    }
  }

  public get hintIcon(): string {
    return this._hintIcon || 'fa-question-circle';
  }

  public get classList() {
    let classList: string[] = [];

    if (!(this._parentFhngComponent instanceof FhngGroupComponent)) {
      classList.push('border-0');
      classList.push('px-0');
      classList.push('p-0');
    }

    return [this.hintIcon, ...classList];
  }

  private _tooltipOptions = {
    placement: this.hintPlacement,
    title: this.hintTitle,
    trigger: this.hintTrigger,
    html: true,
    boundary: 'window'
  }

  constructor(
    private _element: ElementRef,
    @Optional() @SkipSelf() private _parentFhngComponent: FhngComponent,
  ) {

  }

  public ngOnInit() {

  }
}
