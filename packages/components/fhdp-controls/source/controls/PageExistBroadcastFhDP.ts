import {createElement} from 'react';
import * as ReactDOM from 'react-dom';
import {HTMLFormComponent} from "fh-forms-handler";
import {XMLViewerFhDPR} from './XMLViewerFhDPR'

class PageExistBroadcastFhDP extends HTMLFormComponent {
    private _type: string = null;
    private trigger: boolean = false;
    private checkUrl: string = null;
    private successUrl: string = null;
    private failUrl: string = null;
    private timeout: string = null;
    private parentOrigin: string = null;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log("PageExistsBroadcastFhDP log", componentObj);

        this._type = componentObj.type;
        this.trigger = false;
        if (this._type === 'parent') {
            this.checkUrl = componentObj.checkUrl;
            this.successUrl = componentObj.successUrl;
            this.failUrl = componentObj.failUrl;
            this.timeout = componentObj.timeout;
            
            if (!this.checkUrl) {
                throw new Error('Missing checkUrl property');
            }
            if (!this.successUrl) {
                throw new Error('Missing successUrl property');
            }

            if (!this.failUrl) {
                throw new Error('Missing failUrl property');
            }

            if (!this.timeout) {
                throw new Error('Missing timeout property');
            }
        } else if (this._type === 'child') {
            this.parentOrigin = componentObj.parentOrigin;
            if (!this.parentOrigin) {
                throw new Error('Missing parentOrigin property');
            }
            window.addEventListener(
              "message",
              this.childListener,
              false
            );
        }
    }

    childListener(event) {
        if (!event.origin.startsWith(this.parentOrigin)) {
            return;
        }
        if (event.message === 'ping') {
            window.postMessage('pong', event.origin);
        } else if (event.message.startsWith('open=')) {
            const url = event.message.split('open=')[1];
            window.location.href = url;
        }
    }

    create() {

        let mainElement = document.createElement('div');
        mainElement = this.styleMainElement(mainElement);

        this.component = mainElement;
        this.wrap(false, false);
        this.addStyles();
        this.display();
    }


    styleMainElement(mainElement: HTMLDivElement) {
        mainElement.style.display = 'none';
        return mainElement;
    }

    update(change) {
        super.update(change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'checkUrl':
                        this.buildInnerHTML();
                        this.checkUrl = newValue;
                        break;
                    case 'successUrl':
                        this.buildInnerHTML();
                        this.successUrl = newValue;
                        break;
                    case 'failUrl':
                        this.buildInnerHTML();
                        this.failUrl = newValue;
                        break;
                    case 'type':
                        this.buildInnerHTML();
                        this._type = newValue;
                        break;
                    case 'parentOrigin':
                        this.buildInnerHTML();
                        this.parentOrigin = newValue;
                        break;
                    case 'trigger':
                        this.buildInnerHTML();
                        this.trigger = newValue;
                        this._trigger();
                        break;
                }
            }.bind(this));
        }
    };

    private _trigger() {
        if (this.trigger) {
            this.trigger = false;
            const listener = (event) => {
                if (!event.origin.startsWith(this.checkUrl)) {
                    return;
                }
                if (event.message === 'pong') {
                    console.log(`id: ${this.id} success`);
                    window.postMessage(`open=${this.successUrl}`, event.origin);
                    clearTimeout(timeoutId);
                    window.removeEventListener(
                        "message",
                        listener,
                        false
                    );
                }
            };

            const timeoutId = setTimeout(() => {
                console.log(`id: ${this.id} fail - timeout`);
                window.removeEventListener(
                    "message",
                    listener,
                    false
                );
                window.open(this.failUrl);
            }, Number(this.timeout) || 1000);

            window.addEventListener(
            "message",
            listener,
            false
            );

            window.postMessage("ping", this.checkUrl);
        }
    }
}

export {PageExistBroadcastFhDP}
