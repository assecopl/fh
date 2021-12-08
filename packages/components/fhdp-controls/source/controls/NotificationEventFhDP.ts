import {injectable} from "inversify";
import {BaseEvent} from "fh-forms-handler";
import 'bootstrap/js/dist/toast';

type TCP = 'left-top' | 'right-top' | 'right-bottom' | 'left-bottom';

@injectable()
class NotificationEventFhDP extends BaseEvent {
  public static _version = '1.20.12.10';

    constructor() {
      super();
      console.log('Notification Event v.', NotificationEventFhDP._version);
    }

    public fire(data: any): void {
        this.createToast(data);
    }

    private createToast(data) {
        if (data.level === 'error') {
            data.level = 'danger';
        }

        const toastContainerPosition: TCP = (window.localStorage.getItem('tc-position') || 'right-bottom') as TCP;

        let container = document.getElementById('toastContainer');
        if (!container) {
            container = document.createElement('div');
            container.id = 'toastContainer';
            container.style.position ='fixed';
            if (toastContainerPosition === 'right-top') {
                container.style.top = '15px';
                container.style.right = '30px';
            } else if (toastContainerPosition === 'right-bottom') {
                container.style.bottom = '15px';
                container.style.right = '30px';
            } else if (toastContainerPosition === 'left-bottom') {
                container.style.bottom = '15px';
                container.style.left = '30px';
            } else if (toastContainerPosition === 'left-top') {
                container.style.top = '15px';
                container.style.left = '30px';
            } else {
                container.style.bottom = '15px';
                container.style.right = '30px';
            }
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
        $(toast).toast({
            autohide: true,
            animation: true,
            delay: Number(window.localStorage.getItem('notification-delay')) || 30000
        }).toast('show');
        container.appendChild(toast);

        $(toast).on('click', function () {
            // @ts-ignore
            $(this).off('click').toast('hide');
        });

        $(toast).on('hidden.bs.toast', function () {
            // @ts-ignore
            $(this).toast('dispose').remove();
        });
    };
}

export { NotificationEventFhDP };
