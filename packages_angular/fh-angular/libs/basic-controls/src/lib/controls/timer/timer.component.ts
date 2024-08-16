import {Component, forwardRef, inject, Injector, Input, OnDestroy, OnInit, Optional, SkipSelf,} from '@angular/core';
import {ApplicationLockService, Connector, FhngComponent, FormComponent, IDataAttributes} from "@fh-ng/forms-handler";

interface ITimerDataAttributes extends IDataAttributes {
  interval: number;
  active: boolean;
  onTimer: string;
}

@Component({
  selector: 'fhng-timer',
  templateUrl: './timer.component.html',
  styleUrls: ['./timer.component.scss'],
  providers: [
    {provide: FhngComponent, useExisting: forwardRef(() => TimerComponent)},
  ],
})
export class TimerComponent extends FhngComponent implements OnInit, OnDestroy {
  @Input()
  public interval: number;

  private applicationLock: ApplicationLockService = inject(ApplicationLockService);
  private connector: Connector = inject(Connector);

  @Input()
  public active: boolean;

  @Input()
  public onTimer: string;

  private _timer: number;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
    @SkipSelf() public iForm: FormComponent,
  ) {
    super(injector, parentFhngComponent);
  }

  public override ngOnInit(): void {
    super.ngOnInit();
    this._setupTimer();
  }

  public override ngOnDestroy() {
    super.ngOnDestroy();
    this._destroy();
  }

  public override mapAttributes(data: ITimerDataAttributes): void {
    super.mapAttributes(data);

    this.interval = data.interval || data.interval == 0 ? data.interval : this.interval;
    this.active = data.active ? data.active : this.active;
    this.onTimer = data.onTimer ? data.onTimer : this.onTimer;

    this._setupTimer();
  }

  private _destroy():void {
    if (this._timer) {
      clearTimeout(this._timer);
      this._timer = null;
    }
  }

  private _setupTimer(): void {
    if (this.onTimer) {
      this._destroy();

      if (this.active && this.interval > 0) {
        this._timer = setTimeout(this._timeout.bind(this), 1000 * this.interval);
      }
    }
  }

  private _timeout(): void {
    if (!this.destroyed) {
      if (this.applicationLock.isActive() || !this.connector.isOpen() || !this.iForm.isFormActive()) {
        this._destroy();
        // delay until application lock is taken down or form if activated
        this._timer = setTimeout(this._timeout.bind(this), 200);
      } else {
      this._setupTimer();
      this.fireEventWithLock('onTimer', this.onTimer);
      }
    }
  }
}
