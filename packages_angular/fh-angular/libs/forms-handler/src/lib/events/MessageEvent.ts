import {BaseEvent} from "./BaseEvent";
import {Injectable, inject} from '@angular/core';
import {NotificationService} from "@fh-ng/forms-handler";

@Injectable({providedIn: 'root'})
class MessageEvent extends BaseEvent {

    private notificationService: NotificationService = inject(NotificationService);

    public fire(data:MessageData) {
        // this.util.showDialog(
        //     this.escapeHtml(data.title),
        //     this.escapeHtml(data.message),
        //     'OK',
        //     'btn-secondary',
        //     null);

        this.notificationService.showDialog(
            this.escapeHtml(data.title),
            this.escapeHtml(data.message),
            'OK',
            'btn-secondary',
            null);
    }

    private escapedMap = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;',
        '/': '&#x2F;',
        '`': '&#x60;',
        '=': '&#x3D;',
        '\n': '<br>'
    };

    private escapeHtml(string) {
        return String(string).replace(/[&<>"'\/]|[\n]/g, (s) => {
            return this.escapedMap[s];
        });
    }
}

type MessageData = {
  title:string,
  message:string,
  closeButtonLabel:string,
  closeButtonClass:string,
  closeCallback:string
}

export { MessageEvent, MessageData };
