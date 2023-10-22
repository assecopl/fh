import 'bootstrap/js/dist/toast';
import {BaseEvent} from './BaseEvent';
import * as $ from 'jquery';
import {NotificationService} from "../service/Notification"; //TODO remove Jquery
import {Injectable, inject} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class NotificationEvent extends BaseEvent {

  private notificationService: NotificationService = inject(NotificationService);
  constructor() {
    super();
  }

  public fire(data: any): void {
    this.createToast(data);
  }

  private createToast(data) {


    if (data.level === 'error') {
      this.notificationService.showError(data.message);
      return;
    }
    if (data.level === 'warning') {
      this.notificationService.showWarning(data.message);
      return;
    }
    if (data.level === 'success') {
      this.notificationService.showSuccess(data.message);
      return;
    }
    if (data.level === 'info') {
      this.notificationService.showInfo(data.message);
      return;
    }
    this.notificationService.showDefault(data.message);
    return;

    // let container = document.getElementById('toastContainer');
    // if (!container) {
    //   container = document.createElement('div');
    //   container.id = 'toastContainer';
    //   container.style.position = 'fixed';
    //   container.style.top = '15px';
    //   container.style.right = '30px';
    //   container.style.zIndex = '1061';
    //   document.body.appendChild(container);
    // }
    // let toast = document.createElement('div');
    // toast.classList.add('toast');
    // toast.classList.add('fade');
    // toast.classList.add('bg-' + data.level);
    // toast.classList.add('text-light');
    // toast.setAttribute('role', 'alert');
    // toast.setAttribute('aria-live', 'assertive');
    // toast.setAttribute('aria-atomic', 'true');
    //
    // let body = document.createElement('div');
    // body.classList.add('toast-body');
    // body.innerText = data.message;
    //
    // toast.appendChild(body);
    //
    // //@ts-ignore
    // $(toast).toast({
    //     autohide: true,
    //     animation: true,
    //     delay: 5000
    // }).toast('show');
    // container.appendChild(toast);
    //
    // $(toast).on('click', function () {
    //     // @ts-ignore
    //     $(this).off('click').toast('hide');
    // });
    //
    // $(toast).on('hidden.bs.toast', function () {
    //     // @ts-ignore
    //     $(this).toast('dispose').remove();
    // });
  }
}
