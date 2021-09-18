import {injectable,} from "inversify";
import getDecorators from "inversify-inject-decorators";
import {BaseEvent} from "./BaseEvent";
import {FH} from "../Socket/FH";
import {SocketHandler} from "../Socket/SocketHandler";
import {FhContainer} from "../FhContainer";
import {Util} from "../Util";

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class RedirectEvent extends BaseEvent {
    @lazyInject('FH')
    public fh: FH;
    @lazyInject('SocketHandler')
    private socketHandler: SocketHandler;
    @lazyInject("Util")
    protected util: Util;

    public fire(data: { uuid: string, url: string, newWindow: boolean, closeable: boolean }) {
        if (data.url.charAt(0) === '#') {
            this.fh.changeHash(data.url);
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
