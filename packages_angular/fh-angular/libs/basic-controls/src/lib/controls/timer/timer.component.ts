import {Component, forwardRef, Injector, Input, OnDestroy, OnInit, Optional, SkipSelf,} from '@angular/core';
import {FhngComponent, IDataAttributes} from "@fh-ng/forms-handler";

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

  @Input()
  public active: boolean;

  @Input()
  public onTimer: string;

  private _timer: number;

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
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

    this.interval = data.interval;
    this.active = data.active;
    this.onTimer = data.onTimer;

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
      this._setupTimer();
      this.fireEventWithLock('onTimer', this.onTimer);
    }
  }
}
