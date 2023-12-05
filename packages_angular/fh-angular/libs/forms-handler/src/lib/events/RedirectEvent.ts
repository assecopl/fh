import {BaseEvent} from "./BaseEvent";
import {SocketHandler} from "../Socket/SocketHandler";
import {Injectable} from "@angular/core";
import {Utils} from "../service/Utils";


@Injectable({
    providedIn: 'root',
})
class RedirectEvent extends BaseEvent {

    constructor(private socketHandler: SocketHandler, private util: Utils) {
        super();
    }

    public fire(data: { uuid: string, url: string, newWindow: boolean, closeable: boolean }) {
        if (data.url.charAt(0) === '#') {
            // this.fh.changeHash(data.url);
            //TODO Rewrite hash logic to Angular
        } else {
            let link = document.createElement('a');

            link.href = this.util.getPath(data.url);
            if (data.newWindow == true) {
                link.target = '_blank';
                let tab = window.window.open(link.href, link.target || "_self");

                if (data.closeable) {
                    // @ts-ignore
                    let openedExternalUseCases: Map<string, Window> = window.openedExternalUseCases;

                    if (!openedExternalUseCases) {
                        openedExternalUseCases = new Map();
                        // @ts-ignore
                        window.openedExternalUseCases = openedExternalUseCases;
                    }

                    openedExternalUseCases.set(data.uuid, tab);
                }
            } else {
                this.socketHandler.activeConnector.close();
                window.open(link.href, link.target || "_self");
            }
        }
    }
}

export {RedirectEvent};
