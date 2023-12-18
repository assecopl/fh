import {
  Component,
  ElementRef,
  EventEmitter,
  HostBinding,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Output,
  SkipSelf,
} from '@angular/core';
import {FhngComponent, IconAligmentType} from "@fh-ng/forms-handler";

@Component({
  selector: '[fhng-input-group]',
  templateUrl: './input-group.component.html',
  styleUrls: ['./input-group.component.scss'],
})
export class InputGroupComponent implements OnInit, OnDestroy {
  @HostBinding('class.input-group-focus')
  private focus: boolean = false;

  @Input()
  public icon: string = null;

  @Input()
  public iconText: string = null;

  @Input()
  public forceShowBefore: boolean = false;

  @Input()
  public required: boolean = false;

  @Input()
  public inputId: string = '';

  @Input()
  public buttonAriaLabel: string = null;

  @Input()
  public iconAlignment: IconAligmentType = 'BEFORE';

  @Output()
  public onClick: EventEmitter<any> = new EventEmitter();

  public resizeObserver: ResizeObserver = null;

  constructor(
    @Optional() @SkipSelf() public parentFhngComponent: FhngComponent,
    public elementRef: ElementRef
  ) {
  }

  addonClick() {
    this.onClick.emit();
  }

  ngOnInit(): void {
    // this.startAdjustingInputPadding();
  }

  ngOnDestroy(): void {

    // this.stopAdjustingInputPadding();
  }
}
