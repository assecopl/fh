import * as $ from 'jquery';
import getDecorators from "inversify-inject-decorators";
import 'bootstrap/js/dist/modal';
import 'bootstrap/js/dist/tooltip';
import 'jquery-ui/ui/widgets/dialog';
import {injectable} from "inversify";
import {I18n} from "../I18n/I18n";
import {FhContainer} from "../FhContainer";
import {ApplicationLockEN} from "../I18n/ApplicationLock.en";
import {ApplicationLockPL} from "../I18n/ApplicationLock.pl";

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class ApplicationLock {
    @lazyInject('I18n')
    private i18n: I18n;
    rids: string[] = [];
    lockElement: HTMLElement;
    reconnectInfo: HTMLElement;
    opacityTimer: number;

    constructor() {
        this.i18n.registerStrings('en', ApplicationLockEN);
        this.i18n.registerStrings('pl', ApplicationLockPL);

        let lock = document.createElement('div');
        lock.id = 'applicationLock';
        lock.classList.add('modal-backdrop');
        // faded backdrop is causing problems in frequent event firing - duplicates and doesn't remove modal-backdrops
        //modal.classList.add('fade');
        lock.tabIndex = -1;

        let frame = document.createElement('div');
        frame.id = 'applicationLockFrame';
        frame.classList.add('align-self-center');

        let spinner = document.createElement('div');
        spinner.classList.add('spinner-grow');
        spinner.classList.add('text-light');

        let sr = document.createElement('span');
        sr.classList.add('sr-only');
        sr.innerText='Loading...';

        spinner.appendChild(sr);
        frame.appendChild(spinner);
        lock.appendChild(frame);

        this.lockElement = lock;

        document.body.appendChild(this.lockElement);
    }

    public enable(requestId: string) {
        if (!this.rids.length) {
            $(this.lockElement).css('opacity', 0);
            $(this.lockElement).show();

            if (this.opacityTimer == null) {
                this.opacityTimer = setTimeout(function () {
                    if (this.rids.length) {
                        $(this.lockElement).css('opacity', 1);
                    }
                }.bind(this), 1000);
            }
        }
        if (requestId) {
            this.rids.push(requestId);
        }
    }

    public disable(requestId: string) {
        if (this.rids.indexOf(requestId) > -1) {
            this.rids.splice(this.rids.indexOf(requestId), 1);
            if (!this.rids.length) {
                $(this.lockElement).hide();
                if (this.opacityTimer != null) {
                    clearTimeout(this.opacityTimer);
                    this.opacityTimer = null;
                }
            }
        }
    }

    public isActive(): number {
        return this.rids.length;
    }

    // TODO: refactor
    public createErrorDialog(data: Array<any>, callback: any = undefined): any {
        let dialog = document.createElement('div');
        dialog.classList.add('fh-error-dialog');

        let close = document.createElement('span');
        close.classList.add('close');
        close.innerHTML = '&times;';
        close.addEventListener('click', function () {
            ApplicationLock.closeErrorDialog(dialog);
        }.bind(this));

        let content = document.createElement('div');
        content.classList.add('content');

        if (data && data.length > 0) {
            let wrapper = document.createElement('div');

            let modalHeader = this.createElement('div', "modal-header");
            let h5 = this.createElement('h5', "modal-title");
            h5.innerText = this.i18n.__("error.title");
            modalHeader.appendChild(h5);
            wrapper.appendChild(modalHeader);

            let modalBody = this.createElement('div', 'modal-body');
            let row = this.createElement('div', 'row eq-row');
            modalBody.appendChild(row);
            let spacer = this.createElement('div', 'fc wrapper col-md-12 spacer');
            spacer.appendChild(document.createElement('div'));
            row.appendChild(spacer);

            let innerRow = this.createElement('div', 'fc wrapper col-md-12');
            let innerRow2 = document.createElement('div');
            innerRow.appendChild(innerRow2);
            let innerRow3 = this.createElement('div', 'fc wrapper inline');
            innerRow2.appendChild(innerRow3);
            row.appendChild(innerRow);

            data.forEach((entry) => {
                let date = entry.timestamp.year + '.';
                date += (entry.timestamp.monthValue < 10 ? '0' : '') + entry.timestamp.monthValue + '.';
                date += (entry.timestamp.dayOfMonth < 10 ? '0' : '') + entry.timestamp.dayOfMonth;
                let time = (entry.timestamp.hour < 10 ? '0' : '') + entry.timestamp.hour + ':';
                time += (entry.timestamp.minute < 10 ? '0' : '') + entry.timestamp.minute;

                let text = date + ' ' + time;
                text += ' ' + entry.message;

                let span = this.createElement('span', 'fc outputLabel');
                span.appendChild(document.createTextNode(text));
                innerRow3.appendChild(span);
                innerRow3.appendChild(document.createElement('p'));
            });

            let closeDiv = this.createElement('div', 'fc wrapper inline col-md-4 form-group');
            let closeButton = this.createElement('button', 'fc button btn btn-primary btn-block');
            closeButton.appendChild(document.createTextNode(this.i18n.__("close.label")));
            closeButton.addEventListener('click', function () {
                ApplicationLock.closeErrorDialog(dialog);
            }.bind(this));
            closeDiv.appendChild(closeButton);

            row.appendChild(closeDiv);

            wrapper.appendChild(modalBody);

            content.appendChild(wrapper);
        }

        dialog.appendChild(close);
        dialog.appendChild(content);

        if (typeof callback === 'function') {
            callback(content, data);
        }
        $(dialog).dialog({modal: true, width: 600}).siblings('.ui-dialog-titlebar').remove();
        $(dialog).dialog("moveToTop");

        return dialog;
    }

    private createElement(name: string, cssClass: string, callback: any = undefined): HTMLElement {
        let element = document.createElement(name);
        cssClass.split(' ').forEach((cssCls) => element.classList.add(cssCls));
        if (callback) {
            element.addEventListener('click', callback);
        }

        return element;
    }

    public static closeErrorDialog(dialog: any): void {
        if (document.body.contains(dialog)) {
            $(dialog).dialog("close");
        }
    }

    public createInfoDialog(info, button1Name = undefined, button1OnClick = undefined, button2Name = undefined, button2OnClick = undefined): void {
        if (this.reconnectInfo) {
            ApplicationLock.closeErrorDialog(this.reconnectInfo);
            this.reconnectInfo = null;
        }
        this.reconnectInfo = this.createErrorDialog([], function (content) {
            ApplicationLock.appendDialogElements(content, info, button1Name, button1OnClick, button2Name, button2OnClick);
        });
    }

    public closeInfoDialog(): void {
        if (this.reconnectInfo) {
            ApplicationLock.closeErrorDialog(this.reconnectInfo);
            this.reconnectInfo = null;
        }
    }

    private static appendDialogElements(content, info, button1Name, button1OnClick, button2Name, button2OnClick) {
        let wrapper = document.createElement('div');
        let htmlDialog =
            '<div class="modal-header">' +
            '<h5 class="modal-title">Informacja</h5>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row eq-row">' +
            '<div class="fc wrapper col-md-12 spacer"><div></div>' +
            '</div>' +
            '<div class="fc wrapper col-md-4 spacer"><div></div>' +
            '</div>' +
            '<div class="fc wrapper col-md-12">' +
            '<div>' +
            '<div class="fc wrapper inline">' +
            '<span class="fc outputLabel">' + info + '</span><p></p>' +
            '</div>' +
            '</div>' +
            '</div>';
        if (button1Name) {
            htmlDialog +=
                '<div class="fc wrapper inline col-md-4 form-group">' +
                '<button id="Button_282" class="fc button btn btn-primary btn-block" onclick="' + button1OnClick + '">' + button1Name + '</button>' +
                '</div>';
        }
        if (button2Name) {
            htmlDialog +=
                '<div class="fc wrapper inline col-md-4 form-group">' +
                '<button id="Button_283" class="fc button btn btn-primary btn-block" onclick="' + button2OnClick + '">' + button2Name + '</button>' +
                '</div>'
        }
        htmlDialog +=
            '</div>';

        wrapper.innerHTML = htmlDialog;

        content.appendChild(wrapper);
    }
}

export {ApplicationLock};