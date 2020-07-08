"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var fh_forms_handler_1 = require("fh-forms-handler");
var Image = /** @class */ (function (_super) {
    __extends(Image, _super);
    function Image(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.mapId = '';
        _this.source = _this.componentObj.src;
        _this.mapElement = null;
        _this.onClick = _this.componentObj.onClick;
        _this.onAreaClick = _this.componentObj.onAreaClick;
        return _this;
    }
    Image.prototype.create = function () {
        var image = document.createElement('img');
        image.id = this.id;
        ['fc', 'image'].forEach(function (cssClass) {
            image.classList.add(cssClass);
        });
        image.src = this.processURL(this.source);
        var imageLabel = this.fhml.resolveValueTextOrEmpty(this.componentObj.label) || this.id;
        image.setAttribute('alt', imageLabel);
        if (this.width) {
            image.classList.add('img-fluid');
        }
        function mapAreaClick(event) {
            this.fireEvent('onAreaClick#' + event.target.dataset.id, this.onAreaClick, event);
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
                this.fireEventWithLock('onClick', this.onClick, event);
            }.bind(this));
        }
        this.component = image;
        this.hintElement = this.component;
        if (this.width) {
            this.wrap(true);
        }
        else {
            this.htmlElement = this.component;
            this.contentWrapper = this.htmlElement;
        }
        this.addStyles();
        this.display();
    };
    ;
    Image.prototype.display = function () {
        _super.prototype.display.call(this);
        if (this.mapElement) {
            this.htmlElement.appendChild(this.mapElement);
        }
    };
    ;
    Image.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
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
    ;
    Image.prototype.wrap = function (skipLabel) {
        _super.prototype.wrap.call(this, skipLabel);
        this.htmlElement.classList.add('form-group');
    };
    ;
    /**
     * @Override
     */
    Image.prototype.getDefaultWidth = function () {
        return "md-6";
    };
    return Image;
}(fh_forms_handler_1.HTMLFormComponent));
exports.Image = Image;
//# sourceMappingURL=Image.js.map