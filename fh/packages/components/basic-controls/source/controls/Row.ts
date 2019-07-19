import {HTMLFormComponent} from "fh-forms-handler";

enum ElementsHorizontalAlign {
    LEFT, CENTER, RIGHT, BETWEEN, AROUND
}

enum ElementsVerticalAlign {
    TOP, MIDDLE, BOTTOM
}

class Row extends HTMLFormComponent {
    private readonly elementsHorizontalAlign: ElementsHorizontalAlign;
    private readonly elementsVerticalAlign: ElementsVerticalAlign;
    private readonly height: string;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.height = componentObj.height;

        if (componentObj.elementsHorizontalAlign) {
            let elementsHorizontalAlignValue: string = componentObj.elementsHorizontalAlign;
            this.elementsHorizontalAlign = ElementsHorizontalAlign[elementsHorizontalAlignValue];
        }

        if (componentObj.elementsVerticalAlign) {
            let elementsVerticalAlignValue: string = componentObj.elementsVerticalAlign;
            this.elementsVerticalAlign = ElementsVerticalAlign[elementsVerticalAlignValue];
        }
    }

    create() {
        let row = document.createElement('div');
        row.id = this.id;
        row.classList.add('fc');
        row.classList.add('row');
        row.classList.add('sort-row-inner');

        if (this.height) {
            row.style.height = this.height;
        }

        if (this.elementsHorizontalAlign != null) {
            // @ts-ignore
            switch (this.elementsHorizontalAlign) {
                case ElementsHorizontalAlign.LEFT:
                    row.classList.add('justify-content-start');
                    break;
                case ElementsHorizontalAlign.CENTER:
                    row.classList.add('justify-content-center');
                    break;
                case ElementsHorizontalAlign.RIGHT:
                    row.classList.add('justify-content-end');
                    break;
                case ElementsHorizontalAlign.BETWEEN:
                    row.classList.add('justify-content-between');
                    break;
                case ElementsHorizontalAlign.AROUND:
                    row.classList.add('justify-content-around');
                    break;
                default:
                    throw new Error(`Unkown elementsHorizontalAlign propety value '${this.elementsHorizontalAlign}'!`);
            }
        }

        if (this.elementsVerticalAlign != null) {
            switch (this.elementsVerticalAlign) {
                case ElementsVerticalAlign.TOP:
                    row.classList.add('align-items-start');
                    break;
                case ElementsVerticalAlign.MIDDLE:
                    row.classList.add('align-items-center');
                    break;
                case ElementsVerticalAlign.BOTTOM:
                    row.classList.add('align-items-end');
                    break;
                default:
                    throw new Error(`Unkown elementsVerticalAlign propety value '${this.elementsVerticalAlign}'!`);
            }
        }

        this.component = row;
        this.hintElement = this.component;
        this.wrap(true);
        this.addStyles();
        this.display();

        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
    };

    // noinspection JSUnusedGlobalSymbols
    setPresentationStyle(presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
    }
}

export {Row};