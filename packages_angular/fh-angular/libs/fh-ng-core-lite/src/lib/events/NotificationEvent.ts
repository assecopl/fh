import {BaseEvent} from 'projects/fh-forms-manager-ng/src/lib/events/BaseEvent';
import 'bootstrap/js/dist/toast';

export class NotificationEvent extends BaseEvent {
  constructor() {
    super();
  }

  public fire(data: any): void {
    this.createToast(data);
  }

  private createToast(data) {
    if (data.level === 'error') {
      data.level = 'danger';
    }

    let container = document.getElementById('toastContainer');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toastContainer';
      container.style.position = 'fixed';
      container.style.top = '15px';
      container.style.right = '30px';
      container.style.zIndex = '1061';
      document.body.appendChild(container);
    }
    let toast = document.createElement('div');
    toast.classList.add('toast');
    toast.classList.add('fade');
    toast.classList.add('bg-' + data.level);
    toast.classList.add('text-light');
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    let body = document.createElement('div');
    body.classList.add('toast-body');
    body.innerText = data.message;

    toast.appendChild(body);

    // @ts-ignore
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
