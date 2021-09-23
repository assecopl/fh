import {ShutdownEventPL} from './i18n/ShutdownEvent.pl';
import {ShutdownEventEN} from './i18n/ShutdownEvent.en';
import {injectable} from "inversify";
import {BaseEvent} from "./BaseEvent";
import getDecorators from "inversify-inject-decorators";
import {Util} from "../Util";
import {Connector} from "../Socket/Connector";
import {FormsManager} from "../Socket/FormsManager";
import {FhContainer} from "../FhContainer";

let { lazyInject } = getDecorators(FhContainer);

@injectable()
class ShutdownEvent extends BaseEvent {

    @lazyInject("FormsManager")
    protected formsManager: FormsManager;

    @lazyInject("Util")
    protected util: Util;

    @lazyInject("Connector")
    protected connector: Connector;

    constructor() {
        super();
        this.i18n.registerStrings('pl', ShutdownEventPL);
        this.i18n.registerStrings('en', ShutdownEventEN);
    }

    public fire(data) {
        this.formsManager.duringShutdown = true;
        if (data.graceful) {
            this.util.showDialog(this.i18n.__('graceful.title'),
                this.i18n.__('graceful.message'),
                this.i18n.__('graceful.button'),
                'btn-secondary',
                null
            );
        } else {
            this.connector.close();
            this.util.showDialog(this.i18n.__('title'),
                this.i18n.__('message'),
                this.i18n.__('button'),
            'btn-danger',
            function() { location.reload(true); }
            );
        }
    }
}

export { ShutdownEvent };
