// <reference path="../../node_modules/fh-forms-handler/dist/source/Forms/HTMLFormComponent.d.ts"/>
import {HTMLFormComponent} from "fh-forms-handler";
// import * as marked from "marked";
// import {Renderer} from "marked";
// import * as highlightjs from "highlightjs";

class MSReportsView extends HTMLFormComponent {
    private content: any;
    private link: string = null;
    private shadowRoot: ShadowRoot;
    private onLinkClicked: any;

    // [index: string]: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        console.log("MSReportsView", componentObj);
        this.content = this.componentObj.content;
        this.link = this.componentObj.link;
        this.onLinkClicked = this.componentObj.onLinkClicked;

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
        const wrapper:HTMLElement = document.createElement('div');
        wrapper.id = this.id;

        this.shadowRoot = wrapper.attachShadow({mode: 'open'});

       
        this.component = wrapper;
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

        this.initShadowRoot();
    };

    initShadowRoot() {
        this.shadowRoot.innerHTML = this.content;
        const anchorsLinks: HTMLAnchorElement[] = Array.from(this.shadowRoot.querySelectorAll('a'));
        const hyperLinks: HTMLLinkElement[] = Array.from(this.shadowRoot.querySelectorAll('link'));
        anchorsLinks.forEach((el) => {
            el.addEventListener('click', this.handleAnchorClick.bind(this));
        })
        hyperLinks.forEach((el) => {
            el.addEventListener('click', this.handleAnchorClick.bind(this));
        })
    }

    handleAnchorClick(e: MouseEvent) {
        const clickedTarget: any = e.target;
        console.log(clickedTarget);
        this.changesQueue.queueAttributeChange('link', clickedTarget.href);
        this.fireEventWithLock(this.onLinkClicked, clickedTarget.href);
        e.preventDefault();
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
                    case 'content':
                        this.content = newValue;
                        this.component.content = this.content;
                        this.initShadowRoot();
                        break;
                    case 'link':
                        this.link = newValue;
                        this.component.link = this.link;
                        break;
                }
            }.bind(this));
        }
    };

}

export {MSReportsView};
