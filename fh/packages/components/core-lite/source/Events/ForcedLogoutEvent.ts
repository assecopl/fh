import {injectable} from "inversify";
import {BaseEvent} from "./BaseEvent";
import {Util} from "../Util";
import getDecorators from "inversify-inject-decorators";
import {FhContainer} from "../FhContainer";
let { lazyInject } = getDecorators(FhContainer);

@injectable()
class ForcedLogoutEvent extends BaseEvent {
    @lazyInject("Util")
    protected util: Util;

    public fire(data) {
        window.location.href = this.util.getPath('autologout?reason=') + data.reason;
    }
}

export { ForcedLogoutEvent };