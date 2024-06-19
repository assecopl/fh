import {injectable,} from "inversify";
import getDecorators from "inversify-inject-decorators";
import {BaseEvent} from "./BaseEvent";
import {FH} from "../Socket/FH";
import {SocketHandler} from "../Socket/SocketHandler";
import {FhContainer} from "../FhContainer";
import {Util} from "../Util";

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class RedirectHomeEvent extends BaseEvent {
    @lazyInject('FH')
    public fh: FH;
    @lazyInject('SocketHandler')
    private socketHandler: SocketHandler;
    @lazyInject("Util")
    protected util: Util;

    public fire() {
        this.fh.changeHash('#');
        this.socketHandler.activeConnector.close();
        window.location.reload();
    }
}

export {RedirectHomeEvent};
