import {BaseEvent} from "./BaseEvent";
import {Connector} from "../Socket/Connector";
import {FormsManager} from "../Socket/FormsManager";
import {inject, Injectable} from "@angular/core";
import {NotificationService, Utils} from "@fh-ng/forms-handler";


@Injectable({providedIn: 'root'})
class ShutdownEvent extends BaseEvent {

    protected formsManager: FormsManager = inject(FormsManager);

    protected notificationService: NotificationService = inject(NotificationService);

    protected connector: Connector = inject(Connector);

    constructor() {
        super();
    }

    public fire(data) {
        this.formsManager.duringShutdown = true;
        if (data.graceful) {
            this.notificationService.showDialog(this.i18n.__('graceful.title'),
                this.i18n.__('graceful.message'),
                this.i18n.__('graceful.button'),
                'btn-secondary',
                null
            );
        } else {
            this.connector.close();
            this.notificationService.showDialog(this.i18n.__('title'),
                this.i18n.__('message'),
                this.i18n.__('button'),
            'btn-danger',
            function () {
                location.reload();
            }
            );
        }
    }
}

export { ShutdownEvent };
