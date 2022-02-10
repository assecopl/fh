import {HTMLFormComponent} from "fh-forms-handler";
import {FileUpload} from "fh-basic-controls";

class FileUploadPortletFhDP extends FileUpload {

    private maxFileSize: number;
    private readonly fhBaseUrl: string;
    private readonly fhContextPath: string;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        this.maxFileSize = this.componentObj.maxFileSize;
        this.fhBaseUrl = this.componentObj.fhBaseUrl;
        this.fhContextPath = this.componentObj.fhContextPath;
    }

    /**
     * @override
     * Funkcja odpowiedzialna za tworzenie komponentu
     */
    create() {
        super.create();
    }

    /**
     * @Override
     * Aktualizacja kontrolki w przypadku gdy z backendu przyjdą nowe/zaktualizowane dane
     * @param change
     */
    update(change) {
        console.log("FileUploadPortlet");
        super.update(change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'maxFileSize':
                        this.maxFileSize = newValue;
                        this.component.maxFileSize = this.maxFileSize;
                        break;
                }
            }.bind(this));
        }
    }

    /**
     * @Override
     * Zmiana url na który wysyłany jest POST z zawartością pliku; w portlecie dodawany jest odpowiednia nazwa hosta
     * @param url
     */
    processURL(url) {
        if (!this.isEmpty(this.fhBaseUrl) && !this.isEmpty(this.fhContextPath)) {
            let path: string;
            path = this.fhBaseUrl + this.fhContextPath + "/" + url;
            console.log("FileUpload urlpath:", path);
            return path;
        } else
            return url;
    }

    //czy string is empty, null or undefined
    isEmpty(str) {
        return (!str || 0 === str.length);
    }

}

export {FileUploadPortletFhDP};
