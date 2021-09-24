import {HTMLFormComponent} from "fh-forms-handler";

class ImageGrid extends HTMLFormComponent {
    private values: any;
    private subsystem: any;
    private grid: any;
    private selectedIndex: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.values = this.componentObj.values;
        this.subsystem = this.componentObj.subsystem;
        this.grid = null;
        this.selectedIndex = null;
    }

    create() {
        let grid = document.createElement('div');
        grid.id = this.id;
        grid.classList.add('resources');

        let row = document.createElement('div');
        row.classList.add('row');

        grid.appendChild(row);

        this.grid = row;
        this.component = grid;
        this.contentWrapper = row;
        this.hintElement = this.component;
        this.handlemarginAndPAddingStyles();
        this.setValues(this.values);

        this.wrap();
        this.display();
    };

    update(change) {
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'selectedItem':
                        this.selectedIndex = newValue;

                        this.clearValues();
                        this.setValues(this.values);
                        break;
                    case 'values':
                        this.values = newValue;

                        this.clearValues();
                        this.setValues(newValue);
                        break;
                }
            }.bind(this));
        }
    };

    clearValues() {
        let element = this.grid.firstChild;

        while (element) {
            this.grid.removeChild(element);
            element = this.grid.firstChild;
        }
    };

    setValues(values) {
        if (values == null) {
            return;
        }

        for (let i = 0; i < values.length; i++) {
            let wrapper = document.createElement("figure");
            wrapper.setAttribute("class", "figure col-lg-4");

            let link = document.createElement("a");
            link.setAttribute("class", "resource");
            if (i === this.selectedIndex) {
                link.classList.add("selected");
            }
            link.dataset.index = i.toString();
            wrapper.appendChild(link);

            let image = document.createElement("img");
            image.setAttribute("src", "image?module=" + this.subsystem + "&path=" + values[i].name);
            image.setAttribute("alt", values[i].name);
            image.setAttribute("class", "figure-img img-fluid rounded");
            link.appendChild(image);

            let name = document.createElement("figcaption");
            name.classList.add("figure-caption");
            name.textContent = values[i].name;
            link.appendChild(name);

            link.addEventListener('click', function (event) {
                let selected = event.currentTarget;
                let selectedIndex = parseInt(selected.dataset.index);

                if (selected.classList.contains('selected')) {
                    this.selectedIndex = null;
                } else {
                    this.selectedIndex = selectedIndex;
                }

                this.updateModel();
                this.fireEvent('onChange', this.onChange, event);
            }.bind(this));

            this.grid.appendChild(wrapper);
        }
    };

    extractChangedAttributes() {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        return {
            selectedIndex: this.selectedIndex
        };
    };
}

export {ImageGrid};