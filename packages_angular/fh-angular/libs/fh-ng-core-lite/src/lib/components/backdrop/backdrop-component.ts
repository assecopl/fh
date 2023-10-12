import {Component, Host, Injector, Input, OnChanges, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {FhngBackdropService} from '@fhng/ng-core';

@Component({
  selector: 'fhng-backdrop',
  templateUrl: './backdrop.component.html',
  styleUrls: ['./backdrop.component.scss'],
})
export class BackdropComponent implements OnInit, OnChanges {
  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    private backdrop: FhngBackdropService
  ) {
  }

  @Input()
  public show: boolean = false;

  public transparent: boolean = true;

  ngOnInit(): void {
    this.backdrop.backdropSubject.subscribe((c) => {
      if (c != this.show) {
        this.show = c;
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['show'] && changes['show'].currentValue == true) {
      setTimeout(() => {
        this.transparent = false;
      }, 300);
    }
  }
}
