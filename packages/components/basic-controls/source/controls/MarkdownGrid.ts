import {HTMLFormComponent} from "fh-forms-handler";

class MarkdownGrid extends HTMLFormComponent {
    private values: any;
    private subsystem: any;
    private grid: any;
    private selectedIndex: any;
    private onFolderDoubleClick:any = null
    private selectedFolder: string;
    private subDirectories:[];

    private showDir: string = null;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.values = this.componentObj.values;
        this.subsystem = this.componentObj.subsystem;
        this.grid = null;
        this.selectedIndex = null;
        this.subDirectories = this.componentObj.subDirectories;
        this.onFolderDoubleClick = this.componentObj.onFolderDoubleClick;
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

    setValues(values:Array<any>) {
        if (values == null) {
            return;
        }

        for (let i = 0; i < values.length; i++) {
                this.addElement(values[i], i);
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


    private addElement(val:any, index:number){
        let wrapper = document.createElement("figure");
        wrapper.setAttribute("class", "figure col-lg-2 text-center pt-1");

        let link = document.createElement("a");
        link.href = "#";
        link.setAttribute("class", "resource border-white pointer");
        if (index === this.selectedIndex) {
            link.classList.add("selected");
            link.classList.add("border-secondary");
            link.classList.remove("border-white");
        }
        link.dataset.index = index.toString();
        wrapper.appendChild(link);

        let image = document.createElement("i");
        // image.setAttribute("src", "markdown?module=" + this.subsystem + "&path=" + values[i].name);
        // image.setAttribute("alt", values[i].name);
        // image.setAttribute("class", "figure-img img-fluid rounded");
        if(val.directory){
            image.setAttribute("class", "fas fa-folder figure-img img-fluid rounded text-warning");
        } else {
            image.setAttribute("class", "fas fa-file-code figure-img img-fluid rounded");
        }
        image.setAttribute("style", "font-size:45px");
        link.appendChild(image);

        let name = document.createElement("figcaption");
        name.classList.add("figure-caption");
        name.textContent = val.name;
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

        if(this.onFolderDoubleClick && val.directory) {
            link.addEventListener('dblclick', function (event) {
                event.stopPropagation();
                event.preventDefault();
                let selected = event.currentTarget;
                let selectedIndex = parseInt(selected.dataset.index);
                this.selectedIndex = selectedIndex;
                this.updateModel();
                this.fireEvent('onChange', this.onChange, event);
                this.fireEvent('onFolderDoubleClick', this.onFolderDoubleClick, event);
            }.bind(this));
        }

        this.grid.appendChild(wrapper);
    }



    private addFolderUp(val:any, index:number){
        let wrapper = document.createElement("figure");
        wrapper.setAttribute("class", "figure col-lg-2 text-center pt-1");

        let link = document.createElement("a");
        link.href = "#";
        link.setAttribute("class", "resource border-white pointer");
        if (index === this.selectedIndex) {
            link.classList.add("selected");
            link.classList.add("border-secondary");
            link.classList.remove("border-white");
        }
        link.dataset.index = index.toString();
        wrapper.appendChild(link);

        let image = document.createElement("i");
        // image.setAttribute("src", "markdown?module=" + this.subsystem + "&path=" + values[i].name);
        // image.setAttribute("alt", values[i].name);
        // image.setAttribute("class", "figure-img img-fluid rounded");
        image.setAttribute("class", "fas fa-folder-open figure-img img-fluid rounded");
        image.setAttribute("style", "font-size:45px");
        link.appendChild(image);

        let name = document.createElement("figcaption");
        name.classList.add("figure-caption");
        name.textContent = "...";
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
}

export {MarkdownGrid};
