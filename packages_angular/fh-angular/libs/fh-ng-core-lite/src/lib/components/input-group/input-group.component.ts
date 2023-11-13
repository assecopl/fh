import {
  Component,
  ElementRef,
  EventEmitter,
  Host,
  HostBinding,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Output,
  SkipSelf,
} from '@angular/core';
import {IconAligmentType} from '../../models/CommonTypes';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';

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
    /**
     * Dostosowanie szerokosci roboczej input-a do wielkości i ilości dodatkowych elementów (ikony,przyciski,teksy)
     */
    // this.resizeObserver = new ResizeObserver((entries, observer) => {
    //   const element = entries[0].target;
    //   const appender = element.querySelector('.input-group-append');
    //   const prepender = element.querySelector('.input-group-prepend');
    //   const input = element.querySelector('input');
    //   const textarea = element.querySelector('textarea');
    //   if (
    //     appender != null &&
    //     (input != null || textarea != null) &&
    //     appender['offsetWidth'] > 0
    //   ) {
    //     const e = input == null ? textarea : input;
    //     e.style.paddingRight = appender['offsetWidth'] + 'px';
    //   } else if (
    //     prepender != null &&
    //     (input != null || textarea != null) &&
    //     prepender['offsetWidth'] > 0
    //   ) {
    //     const e = input == null ? textarea : input;
    //     e.style.paddingLeft = prepender['offsetWidth'] + 'px';
    //   }
    // });
  }

  // public startAdjustingInputPadding() {
  //   this.resizeObserver.observe(this.elementRef.nativeElement);
  // }
  //
  // public stopAdjustingInputPadding() {
  //   if (this.resizeObserver) {
  //     this.resizeObserver.unobserve(this.elementRef.nativeElement);
  //   }
  // }

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
