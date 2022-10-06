import {HTMLFormComponent} from "fh-forms-handler";
import {EmbeddedViewEn} from "./i18n/EmbeddedView.en";
import {EmbeddedViewPl} from "./i18n/EmbeddedView.pl";
import {EmbeddedViewLt} from "./i18n/EmbeddedView.lt";

class EmbeddedView extends HTMLFormComponent {
    private src: any;
    private label: string = null;

    // [index: string]: any;
    public base64regex:RegExp = /^([0-9a-zA-Z+/]{4})*(([0-9a-zA-Z+/]{2}==)|([0-9a-zA-Z+/]{3}=))?$/;


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log("ExampleFrame2", componentObj);
        this.src = this.componentObj.src;
        this.label = this.componentObj.resourceBasePath ? this.componentObj.resourceBasePath : this.label;

        //Przepisanie wszystkich wartości wejściowych na odpowiednie istniejące wartości klasy.
        // for (const prop in componentObj) {
        //     if ((this[prop])!= undefined) {
        //         this[prop] = componentObj[prop];
        //     }
        // }
        this.i18n.registerStrings('en', EmbeddedViewEn);
        this.i18n.registerStrings('pl', EmbeddedViewPl);
        this.i18n.registerStrings('lt', EmbeddedViewLt);
    }

    isSupportPDF() {
        var hasPDFViewer = false;
        console.log(navigator.mimeTypes);
        try {

            var pdf =
                navigator.mimeTypes &&
                navigator.mimeTypes["application/pdf"]
                    ? navigator.mimeTypes["application/pdf"].enabledPlugin
                    : 0;
            if (pdf) hasPDFViewer = true;
        } catch (e) {
            if (navigator.mimeTypes["application/pdf"] != undefined)
                hasPDFViewer = true;
        }


        return hasPDFViewer;
    }

    getAcrobatInfo() {

        const result = {
            browser: this.getBrowserName(),      // Return browser name
            acrobat: this.isAcrobatInstalled() ? true : false,   // return pdf viewer is enabled or not
            acrobatVersion: this.getAcrobatVersion()  // reurn acrobat version for browser

        };
        console.log(result);
        return result;
    }

    getBrowserName() {
            var userAgent = navigator ? navigator.userAgent.toLowerCase() : "other";

            if (userAgent.indexOf("chrome") > -1) { return "chrome"; }
            else if (userAgent.indexOf("safari") > -1) { return "safari"; }
            else if (userAgent.indexOf("msie") > -1 || userAgent.indexOf("trident") > -1) { return "ie"; }
            else if (userAgent.indexOf("firefox") > -1) { return "firefox";}
            return userAgent;


    };

    getActiveXObject(name) {
        try { return new ActiveXObject(name); } catch (e) { return null }
    };

    getNavigatorPlugin(name) {
        console.log("navigator.plugins", navigator.plugins)
        try {
            for (const key in navigator.plugins) {
                var plugin = navigator.plugins[key];
                if (plugin.name.toLowerCase().indexOf(name) > -1) { return plugin; }
            }
        } catch (e) {

        }

    };

    isAcrobatInstalled() {
        return !!this.getPDFPlugin();
    };

    getPDFPlugin() {
        return
            if (this.getBrowserName() == 'ie') {
                return this.getActiveXObject('AcroPDF.PDF') || this.getActiveXObject('PDF.PdfCtrl');
            }
            else {
                return this.getNavigatorPlugin('adobe acrobat') || this.getNavigatorPlugin('pdf') || this.getNavigatorPlugin('foxit reader');  // works for all plugins which has word like 'adobe acrobat', 'pdf' and 'foxit reader'.
            }
    };

    getAcrobatVersion() {
        try {
            var plugin = this.getPDFPlugin();

            if (this.getBrowserName() == 'ie') {
                var versions = plugin.GetVersions().split(',');
                var latest = versions[0].split('=');
                return parseFloat(latest[1]);
            }

            if (plugin.version) return parseInt(plugin.version);
            return plugin.name

        }
        catch (e) {
            return null;
        }
    }

    base64ToBlob( base64, type = "application/octet-stream" ) {
        const binStr = atob( base64 );
        const len = binStr.length;
        const arr = new Uint8Array(len);
        for (let i = 0; i < len; i++) {
            arr[ i ] = binStr.charCodeAt( i );
        }
        return new Blob( [ arr ], { type: type } );
    }


    /**
     * @override
     * Component creator
     */
    create() {
        const matches = this.src.toString().match(/[^:]\w+\/[\w-+\d.]+(?=;|,)/);
        const mimeType = matches ? matches[0] : null;
        const isBase64 = (this.src.toString().includes(";base64") && this.src.toString().includes("data:"));
        let url = this.src;
        let isBlob = false;

        /**
         * In case of base64 string data we need to transform it to blob to properly show it in iframe.
         */
        if(isBase64 && mimeType){
            try {
                const base64 = this.src.toString().split("base64,")[1];
                var blob = this.base64ToBlob(base64, mimeType);
                url = URL.createObjectURL(blob);
                isBlob = true;
            } catch (e) {
                url = this.src;
                console.log("Blob creation problem, fallback to base64");
                isBlob = false;
            }
        }

        /**
         *
         */
        if((mimeType == "application/pdf" && !this.isSupportPDF() && !this.getAcrobatInfo().acrobat && this.getAcrobatInfo().browser != 'firefox')) {

            /**
             * If PDF is not supported we will show only link for download.
             */
            const a: HTMLAnchorElement = document.createElement('a');
            const s: HTMLElement = document.createElement('div');
            const div: HTMLElement = document.createElement('div');
            a.id = this.id;
            a.href = url;

            if(!this.label) {
                const extension = this.src.toString().match(/[^:/]\w+(?=;|,)/)[0];
                this.label = "file."+extension;
            }
            a.download = this.label;
            a.innerText = this.i18n.translateString('embv_download', undefined, this.i18n.selectedLanguage)+" "+ this.label;

            s.classList.add("text-muted");
            s.innerText = this.i18n.translateString('embv_showError', undefined, this.i18n.selectedLanguage);

            div.appendChild(a);
            div.appendChild(s);

            this.component = div;
        } else {
            const iframe: HTMLIFrameElement = document.createElement('iframe');
            iframe.id = this.id;

            //Nigdy nie dodajemy wiecej niż jedną klase na raz - kompatybilność z IE 11
            iframe.classList.add("example-iframe");
            iframe.classList.add("klasa-2");

            iframe.src = url;

            /**
             * Ustawiamy główny komponent jako wytworzony elemnt - musimy ustawić ten komponent aby dalsze funkcja przeszły poprawnie.
             */
            this.component = iframe;
        }



        /**
         * Wywołujemy funkcje odpowiadającą za okalanie komponent odpowiednimi elementami oraz uzupełnia drzewo komponentu (div z szerokością md)
         */
        this.wrap(false, false);

        /**
         * Uruchamiamy przetwarzanie standardowych styli dla kontrolki
         */
        this.addStyles();


        /**
         * Uruchamiamy prezentacje kontrolki , funkcja zapewnia obsługę accesibalitys, podłącza klasy css które przyszły z backendu oraz
         * wstawia kontrolkę w odpowiednie miejsce na stronie(podstawia odpowiedniemu rodzicowi)
         */
        this.display();
    };

    /**
     * @Override
     * Aktualizacja kontrolki w przypadku gdy z backendu przyjdą nowe/zaktualizowane dane
     * @param change
     */
    update(change) {
        super.update(change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'src':
                        const mimeType = newValue.toString().match(/[^:]\w+\/[\w-+\d.]+(?=;|,)/)[0];
                        const isBase64 = this.base64regex.test(newValue);
                        let url = newValue;
                        this.src = newValue;

                        /**
                         * In case of base64 string data we need to trnasform it to blob to properly show it in iframe.
                         */
                        if(isBase64 && mimeType){
                            const blob = this.base64ToBlob( this.src, mimeType );
                            url = URL.createObjectURL( blob );
                        }
                        this.component.src = url;
                        break;
                }
            }.bind(this));
        }
    };

}

export {EmbeddedView};
