import {BaseEvent} from "./BaseEvent";
import {Injectable} from "@angular/core";
import {Utils} from "../service/Utils";

@Injectable({providedIn: 'root'})
class FileDownloadEvent extends BaseEvent{


  constructor(private util: Utils) {
    super();

  }


  public fire(data: any) {
        // if (this.formsManager.ensureFunctionalityUnavailableDuringShutdown()) {

            var link = document.createElement('a');

            link.href = this.util.getPath(data.url);

            window.open(link.href, '_blank');
        // }
    }
}

export { FileDownloadEvent};
