import {Component, EventEmitter, Host, Injector, Input, OnInit, Optional, Output, SkipSelf,} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {NotificationService} from '../../service/Notification';

@Component({
  selector: 'fhng-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
})
export class NotificationsComponent implements OnInit {
  constructor(
    public injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent,
    public notification: NotificationService
  ) {
  }

  public downloadToasts: Map<string, any> = new Map<string, any>();

  @Output()
  public onToastDispose: EventEmitter<any> = new EventEmitter<any>();

  @Input()
  public toasts: any[] = [];

  onDispose(toast: any) {
    this.onToastDispose.emit(toast);
  }

  ngOnInit(): void {
    this.notification.filesObserable.subscribe((c) => {
      this.downloadToasts = c;
    });
  }
}
