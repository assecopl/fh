import {HTMLFormComponent} from "fh-forms-handler";

class PanelHeaderFhDP extends HTMLFormComponent{

    private info: string = null;
    private title: string = null;
    private onClick: any;

    private div_title:HTMLDivElement;
    private div_info:HTMLDivElement;


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log("PanelHeaderFhDP log", componentObj);


        this.info = componentObj.info;
        this.title = componentObj.title;
        this.onClick = componentObj.onClick;

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
        let parent_div = document.createElement('div');
        parent_div.id = this.id;


        let header_div = document.createElement('div');

        header_div.classList.add("panel-header");
        header_div.classList.add("panel-header--transparent");

        let header_group_div = document.createElement('div');
        header_group_div.classList.add("panel-header-group");

        let div_info = document.createElement('div');
        div_info.classList.add("panel-info")

        let div_title = document.createElement('div');
        div_title.classList.add("panel-title");

        header_group_div.appendChild(div_info);
        header_group_div.appendChild(div_title);

        header_div.appendChild(header_group_div);

        this.div_title = div_title;
        this.div_info = div_info;
        if(!!this.info) {
            div_info.innerText = this.info;
        }
        
        if(!!this.title) {
            div_title.innerText = this.title;
        }

        let div_panel_options = document.createElement('div');
        div_panel_options.classList.add("panel-options");

        let button = document.createElement('button');
        button.classList.add('panel-option');
        button.ariaLabel = this.i18n.__("fh.close") + ":" + this.title;

        if (this.onClick) {
            console.log('add event listener');
            button.addEventListener('click', this.onClickEvent.bind(this));
        }

        let i = document.createElement('i');
        i.classList.add('fas');
        i.classList.add('fa-times');
        let sronly = document.createElement('span');
        sronly.classList.add('sr-only');
        sronly.innerText = this.i18n.__("fh.close") + ":" + this.title


        button.appendChild(i);
        button.appendChild(sronly);
        div_panel_options.appendChild(button);

        header_div.appendChild(div_panel_options);

        parent_div.appendChild(header_div);


        /**
         * Ustawiamy główny komponent jako wytworzony elemnt - musimy ustawić ten komponent aby dalsze funkcja przeszły poprawnie.
         */
        this.component = parent_div;
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
                    case 'title':
                        this.title = newValue;
                        this.component.title = this.title;
                        this.div_title.innerText = this.title;
                        break;
                    case 'info':
                        this.info = newValue;
                        this.component.info = this.info;
                        this.div_info.innerText = this.info;
                        break;
                }
            }.bind(this));
        }
    };

    onClickEvent(event) {
        console.log(' onClickEvent ' +  event);


        event.stopPropagation();
        if (this._formId === 'FormPreview') {
            this.fireEvent('onClick', this.onClick);
        } else {
            this.fireEventWithLock('onClick', this.onClick);
        }
        event.target.blur();
    }
}
export {PanelHeaderFhDP}
