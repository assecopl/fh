import {Component, Injector, Input, OnInit} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import { FhngHTMLElementC } from '../../models/componentClasses/FhngHTMLElementC';

@Component({
  selector: '[fhng-input-label]',
  templateUrl: './input-label.component.html',
  styleUrls: ['./input-label.component.scss'],
})
export class InputLabelComponent extends FhngHTMLElementC implements OnInit {

  public labelHtml: SafeHtml;

  @Input()
  public inputId: string = '';

  @Input()
  public labelSize: string = '40';

  @Input()
  public required = false;

  get isFixedSize(): boolean {
    return this.labelSize.includes('px');
  }

  get labelSizeNumber(): number {
    if (this.isFixedSize) {
      return Number(this.labelSize.substr(0, this.labelSize.length - 2));
    }
    return Number(this.labelSize);
  }

  constructor(public override injector: Injector, private sanitizer: DomSanitizer) {
    super(injector, null);
  }

  override ngOnInit() {
    // super.ngOnInit();
    this.labelHtml = this.sanitizer.bypassSecurityTrustHtml(this.label);
    if (!this.isFixedSize && this.labelSizeNumber <= 4) {
      this.labelSize = '5';
    }
  }

  public override mapAttributes(data: any) {
    super.mapAttributes(data);
  }


}
