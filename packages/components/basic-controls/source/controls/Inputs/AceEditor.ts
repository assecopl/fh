import {HTMLFormComponent} from "fh-forms-handler";

import * as AceLib from 'ace-builds';

import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/mode-xml';
import 'ace-builds/src-noconflict/mode-text';
import 'ace-builds/src-noconflict/theme-github_light_default';
import {beautify} from "ace-builds/src-noconflict/ext-beautify";

class AceEditor extends HTMLFormComponent {
    private src: any;
    private mode: "json" | "xml" | null | "" | "text" = "xml";
    private label: string = null;

    private ace:any = null;

    // [index: string]: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.src = this.componentObj.src;
        this.label = this.componentObj.label;
        this.mode = this.componentObj.mode ? this.componentObj.mode.toLowerCase() : "text";

    }

    /**
     * @override
     * Funkcja odpowiedzialan za tworzenie komponentu
     */
    create() {
        const div= document.createElement('div');
        div.id = this.id;
        div.textContent = this.src;
        div.style.width = "100%";
        div.style.height = "300px";


        /**
         * Ustawiamy główny komponent jako wytworzony elemnt - musimy ustawić ten komponent aby dalsze funkcja przeszły poprawnie.
         */
        this.component = div;
        /**
         * Wywołujemy funkcje odpowiadającą za okalanie komponent odpowiednimi elementami oraz uzupełnia drzewo komponentu (div z szerokością md)
         */
        this.wrap(false, false);

        /**
         * Uruchamiamy przetwarzanie standardowych styli dla kontrolki
         */
        this.addStyles();

        this.initAce();

        /**
         * Uruchamiamy prezentacje kontrolki , funkcja zapewnia obsługę accesibalitys, podłącza klasy css które przyszły z backendu oraz
         * wstawia kontrolkę w odpowiednie miejsce na stronie(podstawia odpowiedniemu rodzicowi)
         */
        this.display();
    };

    private initAce() {
        this.ace = AceLib.edit(this.component, { useWorker: false });
        // this.ace.setTheme("ace/theme/github_light_default");
        this.ace.setReadOnly(true);

        // this.ace.fullWidth = true;
        // this.ace.height = "300px";

        this.resolveMode();

        beautify(this.ace.session);
    }

    private reloadAce(value:string, mode:string) {
        this.ace.setValue("");
        this.resolveMode(mode);
        this.ace.setValue(value);
        beautify(this.ace.session);
    }

    destroy(removeFromParent: any) {
        super.destroy(removeFromParent);
        this.ace.destroy()
    }


    private  resolveMode(mode:string = this.mode){
        if(this.mode == "xml"){
            this.ace.session.setMode("ace/mode/xml");
        } else if (this.mode == "json") {
            this.ace.session.setMode("ace/mode/json");
        }else {
            this.ace.session.setMode("ace/mode/text");
        }
    }

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
                    case 'mode':
                        this.mode = newValue.toString().toLowerCase();
                        this.reloadAce(this.src, this.mode);
                        break;
                    case 'src':
                        this.src = newValue;
                        this.reloadAce(newValue, this.mode)

                        break;
                }
            }.bind(this));
        }
    };

}

export {AceEditor};
