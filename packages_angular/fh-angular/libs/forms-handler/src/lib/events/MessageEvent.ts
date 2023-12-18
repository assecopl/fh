import {BaseEvent} from "./BaseEvent";
import {Injectable, inject} from '@angular/core';
import {NotificationService} from "../service/Notification";
import {MessageData} from "../Base";

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



export { MessageEvent };
