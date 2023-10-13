import {Component, Host, Injector, Input, OnChanges, OnInit, Optional, SimpleChanges, SkipSelf,} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {ApplicationLockService} from '../../service/application-lock.service';

@Component({
  selector: 'fhng-application-lock',
  templateUrl: './application-lock.component.html',
  styleUrls: ['./application-lock.component.scss'],
})
export class ApplicationLockComponent implements OnInit, OnChanges {
  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    private backdrop: ApplicationLockService
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
