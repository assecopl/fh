import {Component, Injector, Input, OnInit} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Component({
  selector: '[fhng-input-label]',
  templateUrl: './input-label.component.html',
  styleUrls: ['./input-label.component.scss'],
})
export class InputLabelComponent extends FhngHTMLElementC implements OnInit {
  constructor(public override injector: Injector, private sanitizer: DomSanitizer) {
    super(injector, null);
  }

  public labelHtml: SafeHtml;

  @Input()
  public inputId: string = '';

  @Input()
  public labelSize: string = '40';

  override ngOnInit() {
    // super.ngOnInit();
    this.labelHtml = this.sanitizer.bypassSecurityTrustHtml(this.label);
    if (!this.isFixedSize && this.labelSizeNumber <= 4) {
      this.labelSize = '5';
    }
  }

  get labelSizeNumber(): number {
    if (this.isFixedSize) {
      return Number(this.labelSize.substr(0, this.labelSize.length - 2));
    }
    return Number(this.labelSize);
  }

  get isFixedSize(): boolean {
    return this.labelSize.includes('px');
  }
}
