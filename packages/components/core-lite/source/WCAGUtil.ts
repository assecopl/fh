import {injectable} from "inversify";

declare var contextRoot: string;
declare var fhBaseUrl: string;
declare const $: any;

@injectable()
class WCAGUtil {

    public fireFocusOnReturn: boolean = false;

    constructor() {
    }

    initWCAG() {
        if (this.isHighContrastActive()) {
            this.turnOnHighContrast();
        }
        if (this.is4xFontSizeActive()) {
            this.turnOn4xFontSize()
        } else if (this.is2xFontSizeActive()) {
            this.turnOn2xFontSize()
        }
    }

    turnOnHighContrast(cssClass: string = null) {
        if (cssClass == null) {
            cssClass = localStorage.getItem("fh-high-contrast");
        }
        if (cssClass) {
            document.body.classList.add(cssClass);
            localStorage.setItem("fh-high-contrast", cssClass);
        }
    }

    turnOffHighContrast(cssClass: string = null) {
        if (cssClass == null) {
            cssClass = localStorage.getItem("fh-high-contrast");
        }

        if (cssClass) {
            document.body.classList.remove(cssClass);
        }
        localStorage.setItem("fh-high-contrast", "");

    }

    isHighContrastActive() {
        const contrast = localStorage.getItem("fh-high-contrast");
        if (contrast) {
            return true;
        }
        return false;
    }

    turnOnNormalFontSize(cssClass2: string = null, cssClass4: string = null) {
        if (cssClass2 == null) {
            cssClass2 = localStorage.getItem("fh-fontsize-2");
        }
        if (cssClass4 == null) {
            cssClass4 = localStorage.getItem("fh-fontsize-4");
        }
        if (cssClass2) document.documentElement.classList.remove(cssClass2);
        if (cssClass4) document.documentElement.classList.remove(cssClass4);
        localStorage.setItem("fh-fontsize-2", "");
        localStorage.setItem("fh-fontsize-4", "");
    }

    turnOn2xFontSize(cssClass2: string = null) {
        if (cssClass2 == null) {
            cssClass2 = localStorage.getItem("fh-fontsize-2");
        }

        if (cssClass2) document.documentElement.classList.add(cssClass2);
        localStorage.setItem("fh-fontsize-2", cssClass2);


        let cssClass4 = localStorage.getItem("fh-fontsize-4");
        if (cssClass4) document.documentElement.classList.remove(cssClass4);
        localStorage.setItem("fh-fontsize-4", "");
    }

    turnOn4xFontSize(cssClass4: string = null) {
        if (cssClass4 == null) {
            cssClass4 = localStorage.getItem("fh-fontsize-4");
        }

        if (cssClass4) document.documentElement.classList.add(cssClass4);
        localStorage.setItem("fh-fontsize-4", cssClass4);


        let cssClass2 = localStorage.getItem("fh-fontsize-2");
        if (cssClass2) document.documentElement.classList.remove(cssClass2);
        localStorage.setItem("fh-fontsize-2", "");
    }

    is2xFontSizeActive() {
        const contrast = localStorage.getItem("fh-fontsize-2");
        if (contrast) {
            return true;
        }
        return false;
    }

    is4xFontSizeActive() {
        const contrast = localStorage.getItem("fh-fontsize-4");
        if (contrast) {
            return true;
        }
        return false;
    }

    setLastFocusedElementId(id: string, formId: string) {
        localStorage.setItem("fh-last-focus-id", id);
        localStorage.setItem("fh-last-focus-form-id", formId);
    }

    getLastFocusedElementId() {
        return {
            id: localStorage.getItem("fh-last-focus-id"), formId:
                localStorage.getItem("fh-last-focus-form-id")
        }
    }


}

export {WCAGUtil};
