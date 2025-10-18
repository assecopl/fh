import {createElement} from 'react';
import * as ReactDOM from 'react-dom';
// import * as eml from 'eml-parser';
import {HTMLFormComponent} from "fh-forms-handler";
import { parseEml, readEml, GBKUTF8, decode } from 'eml-parse-js';
import {ReadedEmlJson} from "eml-parse-js/dist/interface";

class EMLViewerFhDP extends HTMLFormComponent {
    private content: string = null;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log("EMLViewerFhDP log", componentObj);

        this.content = componentObj.content;
    }

    create() {

        let eml = readEml(this.content),
            iframe = this.renderEML(eml as ReadedEmlJson),
            mainElement = document.createElement('div');

        // iframe.innerHTML = this.content;

        mainElement = this.styleMainElement(mainElement);
        mainElement.id = this.id;

        mainElement.append(iframe)
        mainElement.append(this.parseEmlAttachments(eml as ReadedEmlJson));


        this.component = mainElement;
        this.wrap(false, false);
        this.addStyles();
        this.display();
    }

    update(change) {
        super.update(change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'content':
                        this.content = newValue;

                        let eml = readEml(this.content),
                            iframe = this.renderEML(eml as ReadedEmlJson);

                        this.component.innerHTML = "";
                        this.component.append(iframe);
                        this.component.append(this.parseEmlAttachments(eml as ReadedEmlJson));

                        break;
                }
            }.bind(this));
        }
    };

    renderEML(ReadEmlJson: ReadedEmlJson): HTMLDivElement {
        let emlDiv = document.createElement('div'),
            iframe = document.createElement('iframe');

        emlDiv.classList.add('eml-view');

        //Nigdy nie dodajemy wiecej niż jedną klase na raz - kompatybilność z IE 11
        iframe.classList.add("example-iframe");
        iframe.classList.add("eml-view__iframe");
        iframe.classList.add("klasa-2");
        iframe.style.width = '100%';
        iframe.style.height = '100%';
        iframe.style.border = '0';
        iframe.style.minHeight = '400px';

        if (!(ReadEmlJson instanceof Error)){
            console.log('EML render:', (ReadEmlJson as ReadedEmlJson));
            iframe.srcdoc = (ReadEmlJson as ReadedEmlJson).html
            this.parseEmlImageIntoIframe(iframe, ReadEmlJson as ReadedEmlJson);
            console.log('EML render:', iframe, ReadEmlJson);
        }

        emlDiv.append(iframe);

        return emlDiv;
    }

    parseEmlImageIntoIframe (iframe: HTMLIFrameElement, ReadEmlJson: ReadedEmlJson) {
        iframe.onload = function () {
            let images = iframe.contentDocument.querySelectorAll('img');

            for (let i = 0; i<(images || []).length; i++) {
                let id = images[i].src.replace('cid:', ''),
                    attachment = ReadEmlJson.attachments.find(element => element.id === `<${id}>`);

                images[i].src = `data:${attachment.contentType.split(';')[0]};base64,${attachment.data64}`;
                images[i].alt = attachment.name;
            }
        }
    }

    parseEmlAttachments(ReadEmlJson: ReadedEmlJson) {
        let attachmentsDiv = document.createElement('div'),
            attachmentsOl = document.createElement('ol');

        attachmentsDiv.classList.add('attachments');
        attachmentsOl.classList.add('attachments__list');

        for (let i = 0; i<(ReadEmlJson.attachments || []).length; i++) {
            let attachmentLi = document.createElement('li'),
                attachment = ReadEmlJson.attachments[i],
                link = document.createElement('a');

            attachmentLi.classList.add('attachments__item');

            link.classList.add('attachments__link');
            link.text = attachment.name;
            link.href='#';
            link.onclick = function () {
                const blob = new Blob([Uint8Array.from(atob(attachment.data64), c => c.charCodeAt(0))], { type: attachment.contentType.split(';')[0] });
                const url = URL.createObjectURL(blob);
                window.open(url, '_blank');
            }

            attachmentLi.append(link);
            attachmentsOl.append(attachmentLi);
        }

        if (!(ReadEmlJson instanceof Error)) {
            attachmentsDiv.innerHTML = this.i18n.__("fh.attachments");
            attachmentsDiv.append(attachmentsOl);
        }

        return attachmentsDiv;
    }

    styleMainElement(element) {
        element.style.background = "var(--color-bg-xml-view)";
        element.style.padding = "10px";
        element.style.lineHeight = "1.3rem";
        element.style.fontFamily = "'Verdana', monospace";
        return element;
    }
}

export {EMLViewerFhDP}
