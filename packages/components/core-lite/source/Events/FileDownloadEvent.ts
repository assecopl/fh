import {injectable} from "inversify";
import {BaseEvent} from "./BaseEvent";
import getDecorators from "inversify-inject-decorators";
import {FormsManager} from "../Socket/FormsManager";
import {Util} from "../Util";

import {FhContainer} from "../FhContainer";
let {lazyInject} = getDecorators(FhContainer);

@injectable()
class FileDownloadEvent extends BaseEvent{

    @lazyInject("FormsManager")
    protected formsManager: FormsManager;

    @lazyInject("Util")
    protected util: Util;

    public fire(data: any) {
        if (this.formsManager.ensureFunctionalityUnavailableDuringShutdown()) {

            var link = document.createElement('a');

            link.href = this.util.getPath(data.url);

            window.open(link.href, '_blank');
        }
    }
}

export { FileDownloadEvent};