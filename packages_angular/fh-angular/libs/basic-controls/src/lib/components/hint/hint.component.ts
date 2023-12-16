import {Component, ElementRef, HostListener, Input, OnInit, Optional, SkipSelf, ViewChild} from "@angular/core";
import {HintPlacement, HintType} from "../../models/componentClasses/FhngHTMLElementC";
import {AvailabilityUtils, FhngComponent, FhngGroupComponent} from "@fh-ng/forms-handler";
import {NgbTooltip} from "@ng-bootstrap/ng-bootstrap";

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

  @Input()
  public hintDisplay: boolean = true;

  @Input('fhng-hint')
  public hint: string = null;

  @ViewChild('tooltip', {read: NgbTooltip})
  public tooltipObject: NgbTooltip = null;

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

  public get placement(): string {
    switch(this.hintPlacement) {
      case HintPlacement.TOP:
      case HintPlacement.BOTTOM:
        return this.hintPlacement.toLowerCase();
      case HintPlacement.RIGHT:
        return 'end';
      case HintPlacement.LEFT:
      default:
        return 'start';
    }
  }

  constructor(
    private _element: ElementRef,
    @Optional() @SkipSelf() public _parentFhngComponent: FhngComponent,
  ) {

  }

  public ngOnInit() {

  }

  public getHintTooltip(): string {
    return AvailabilityUtils.processOnEdit(this._parentFhngComponent.availability,
      () => {
        if (this.hint && ['STATIC', 'STANDARD'].includes(this.hintType)) {
          return this.hint;
        }

        return null;
      },
      null
    )
  }

  public getHintPopover(): string {
    return AvailabilityUtils.processOnEdit(this._parentFhngComponent.availability,
      () => {
        if (this.hint && this.hintType === 'STANDARD_POPOVER') {
          return this.hint;
        }

        return null;
      },
      null
    )
  }

  @HostListener('mouseenter', ['$event'])
  public onHover () {
    if (this.tooltipObject && !this.tooltipObject.isOpen()) {
      this.tooltipObject.open('body');
    }
  }

  @HostListener('mouseleave', ['$event'])
  public onHoverLeave () {
    if (this.tooltipObject && this.tooltipObject.isOpen()) {
      this.tooltipObject.close();
    }
  }
}
