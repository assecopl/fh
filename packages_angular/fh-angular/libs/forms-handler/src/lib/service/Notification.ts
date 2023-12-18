import {Injectable} from '@angular/core';
import {ReplaySubject, Subject} from 'rxjs';
import {MessageData} from "../events/MessageEvent";

@Injectable({providedIn: 'root'})
export class NotificationService {
    toastsObserable: Subject<Toast> = new ReplaySubject<Toast>();
    filesObserable: Subject<Map<string, DownloadToast>> = new ReplaySubject<Map<string, DownloadToast>>();
    dialogObservable: ReplaySubject<MessageData> = new ReplaySubject<MessageData>();

    public downloadToasts: Map<string, any> = new Map<string, any>();

    constructor() {
    }

    showDialog(title, message, closeButtonLabel, closeButtonClass, closeCallback){
      this.dialogObservable.next({
        title:title,
        message:message,
        closeButtonLabel:closeButtonLabel,
        closeButtonClass: closeButtonClass,
        closeCallback: closeCallback
      })
    }

    showInfo(body: string, header: string = null, delay: number = null) {
        this.createToast(body, 'bg-info text-light', header, delay);
    }

    showWarning(body: string, header: string = null, delay: number = null) {
        this.createToast(body, 'bg-warning text-light', header, delay);
    }

    showError(body: string, header: string = null, delay: number = null) {
        this.createToast(body, 'bg-danger text-light', header, delay);
    }

    showSuccess(body: string, header: string = null, delay: number = null) {
        this.createToast(body, 'bg-success text-light', header, delay);
    }

    showDefault(body: string, header: string = null, delay: number = null) {
        this.createToast(body, 'bg-default', header, delay);
    }

    showDownload(id: number, header: string = null, body: string = null) {
        this.createDownloadToast(id, 'bg-dark text-light', header, body);
    }


    public removeDownload(id: any): void {
        this.downloadToasts.delete(id);
        this.filesObserable.next(this.downloadToasts);
    }

    public updateProgress(id: any, progress: number, filename): void {
        const t = this.downloadToasts.get(id);
        if (t) {
            t.progress = progress;
            if (filename) t.body = filename;
            if (progress > 0 || filename) t.show = true;
            this.filesObserable.next(this.downloadToasts);
        }
    }


    private createToast(body: string, classname: string, header: string = null, delay: number = 10000) {
        this.toastsObserable.next({
            body: body,
            classname: classname,
            delay: delay && delay >= 0 ? delay : 10000,
            header: header
        });
    }

    private createDownloadToast(id: any, classname: string, header: string = null, body: string = null) {
        const toast = {
            id: id,
            classname: classname,
            delay: 0,
            header: header,
            body: body,
            show: false
        };
        this.downloadToasts.set(id, toast);
        this.filesObserable.next(this.downloadToasts);
    }
}

export class Toast {
    body: string;
    classname: string;
    delay: any;
    header: any;
}

export class DownloadToast {
    id: any;
    progress: number;
    body: string;
    classname: string;
    delay: any;
    header: any;
    show: boolean;
}
