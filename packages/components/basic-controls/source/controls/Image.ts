import {HTMLFormComponent} from "fh-forms-handler";

class Image extends HTMLFormComponent {
    private mapElement: any;
    private mapId: string;
    private source: any;
    private onClick: any;
    private onAreaClick: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.mapId = '';
        this.source = this.componentObj.src;

        this.mapElement = null;

        this.onClick = this.componentObj.onClick;
        this.onAreaClick = this.componentObj.onAreaClick;
    }

    create() {
        var image = document.createElement('img');
        image.id = this.id;
        ['fc', 'image'].forEach(function (cssClass) {
            image.classList.add(cssClass);
        });
        image.src = this.processURL(this.source);

        let imageLabel = this.fhml.resolveValueTextOrEmpty(this.componentObj.label) || this.id;
        image.setAttribute('alt', imageLabel);

        if (this.width) {
            image.classList.add('img-fluid');
        }

        function mapAreaClick(event) {
            this.fireEvent('onAreaClick#' + event.target.dataset.id, this.onAreaClick);
        }

        if (this.componentObj.imageAreas) {
            this.mapId = this.id + '_map';
            image.useMap = '#' + this.mapId;

            var map = document.createElement('map');
            map.name = this.mapId;

            this.componentObj.imageAreas.forEach(function (imageArea) {
                var area = document.createElement('area');
                area.id = this.mapId + imageArea.id;
                area.shape = 'rect';
                area.coords =
                    imageArea.xl + ',' + imageArea.yl + ',' + imageArea.xp + ',' + imageArea.yp;
                area.dataset.id = imageArea.id;

                if (this.accessibility === 'EDIT' && this.onAreaClick) {
                    area.addEventListener('click', mapAreaClick.bind(this));
                }
                map.appendChild(area);
            }.bind(this));

            this.mapElement = map;
        }

        if (this.accessibility === 'EDIT' && this.onClick) {
            image.addEventListener('click', function (event) {
                event.stopPropagation();
                this.fireEventWithLock('onClick', this.onClick);
            }.bind(this));
        }

        this.component = image;
        this.hintElement = this.component;
        if (this.width) {
            this.wrap(true);
        } else {
            this.htmlElement = this.component;
            this.contentWrapper = this.htmlElement;
        }
        this.addStyles();
        this.display();
    };

    display() {
        super.display();

        if (this.mapElement) {
            this.htmlElement.appendChild(this.mapElement);
        }
    };

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

    wrap(skipLabel) {
        super.wrap(skipLabel);
        this.htmlElement.classList.add('form-group');
    };

    /**
     * @Override
     */
    public getDefaultWidth(): string {
        return "md-6";
    }
}

export {Image};
