import {FhContainer, HTMLFormComponent} from "fh-forms-handler";
import * as $ from "jquery";

class Anchor extends HTMLFormComponent {

    private scrollOnStart: boolean = false;
    private scroll: boolean = false;
    private animateDuration: number = 0;


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.scrollOnStart = this.componentObj.scrollOnStart? this.componentObj.scrollOnStart : false ;
        this.scroll = this.componentObj.scroll? this.componentObj.scroll : false ;
        this.animateDuration = this.componentObj.animateDuration? this.componentObj.animateDuration : 0;

        if (this.parent instanceof HTMLFormComponent) {
            this.container = this.parent.contentWrapper;
        } else {
            this.container = (<any>this.parent).container;
        }

        this.contentWrapper = this.container;
    }

    create() {
            var element = document.createElement('div');
            element.id = this.id;
            // element.textContent = " ";
            element.style.display = 'inline-block';
            element.style.width = "100%";
            element.style.height = "1px";
            // element.style.background = "";

            this.component = element;
            this.htmlElement = this.component;
            this.contentWrapper = this.htmlElement;
            // this.wrap(true);
            this.display();
    };

    display() {
        this.container.appendChild(this.htmlElement);
        if(this.scrollOnStart){
            this.addAfterInitActions(this.scrollNow.bind(this));
            this.scrollOnStart = false;
        }
    }

    update(change) {
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'scroll':
                        if(newValue){
                            this.scrollNow(true);
                        }
                        this.setAccessibility(newValue);
                        break;
                }
            }.bind(this));
        }
    };

    protected scrollNow(){
        this.util.scrollToComponent(this.id, this.animateDuration);

    }
}

export {Anchor};