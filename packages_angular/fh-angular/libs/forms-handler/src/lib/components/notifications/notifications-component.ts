import {Component, EventEmitter, Host, Injector, Input, OnInit, Optional, Output, SkipSelf,TemplateRef,ViewChild } from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {NotificationService} from '../../service/Notification';
import {NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'fhng-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss'],
})
export class NotificationsComponent implements OnInit {
  constructor(
      public injector: Injector,
      @Optional() @SkipSelf() parentFhngComponent: FhngComponent,
      public notification: NotificationService,
      private modalService: NgbModal,

  ) {
  }

  public downloadToasts: Map<string, any> = new Map<string, any>();

  @Output()
  public onToastDispose: EventEmitter<any> = new EventEmitter<any>();

  @Input()
  public toasts: any[] = [];

  public modal:any = null;

  @ViewChild("modalContent", {read: TemplateRef, static: true})
  public modalContent: TemplateRef<void>;

  public removeToast(toast: any): void {
    this.toasts = this.toasts.filter(t => t !== toast);
  }

  onDispose(toast: any) {
    this.onToastDispose.emit(toast);
  }

  ngOnInit(): void {
    this.notification.filesObserable.subscribe((c) => {
      this.downloadToasts = c;
    });
    this.toasts = [];
    this.notification.toastsObserable.subscribe(c => {
      this.toasts.push(c);
    });

    this.notification.toastsObserable.subscribe(c => {
      this.modal = c;
      this.modalService.open(this.modalContent);
    });


  }
}
