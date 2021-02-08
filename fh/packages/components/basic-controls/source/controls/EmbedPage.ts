import {HTMLFormComponent} from "fh-forms-handler";

class EmbedPage extends HTMLFormComponent {
    private src: any;
    private label: string = null;

    // [index: string]: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.src = this.componentObj.src;
        this.label = this.componentObj.resourceBasePath;

        //Przepisanie wszystkich wartości wejściowych na odpowiednie istniejące wartości klasy.
        // for (const prop in componentObj) {
        //     if ((this[prop])!= undefined) {
        //         this[prop] = componentObj[prop];
        //     }
        // }

    }

    /**
     * @override
     * Funkcja odpowiedzialan za tworzenie komponentu
     */
    create() {
        const iframe:HTMLIFrameElement = document.createElement('iframe');
        iframe.id = this.id;




        //Nigdy nie dodajemy wiecej niż jedną klase na raz - kompatybilność z IE 11
        iframe.classList.add("example-iframe");
        iframe.classList.add("klasa-2");

        iframe.src = this.src;

        /**
         * Ustawiamy główny komponent jako wytworzony elemnt - musimy ustawić ten komponent aby dalsze funkcja przeszły poprawnie.
         */
        this.component = iframe;
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
                        this.src = newValue;
                        this.component.src = this.src;
                        break;
                }
            }.bind(this));
        }
    };

}

export {EmbedPage};
