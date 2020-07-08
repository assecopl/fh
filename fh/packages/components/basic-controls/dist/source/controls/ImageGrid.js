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
var ImageGrid = /** @class */ (function (_super) {
    __extends(ImageGrid, _super);
    function ImageGrid(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.values = _this.componentObj.values;
        _this.subsystem = _this.componentObj.subsystem;
        _this.grid = null;
        _this.selectedIndex = null;
        return _this;
    }
    ImageGrid.prototype.create = function () {
        var grid = document.createElement('div');
        grid.id = this.id;
        grid.classList.add('resources');
        var row = document.createElement('div');
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
    ;
    ImageGrid.prototype.update = function (change) {
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
    ;
    ImageGrid.prototype.clearValues = function () {
        var element = this.grid.firstChild;
        while (element) {
            this.grid.removeChild(element);
            element = this.grid.firstChild;
        }
    };
    ;
    ImageGrid.prototype.setValues = function (values) {
        if (values == null) {
            return;
        }
        for (var i = 0; i < values.length; i++) {
            var wrapper = document.createElement("figure");
            wrapper.setAttribute("class", "figure col-lg-4");
            var link = document.createElement("a");
            link.setAttribute("class", "resource");
            if (i === this.selectedIndex) {
                link.classList.add("selected");
            }
            link.dataset.index = i.toString();
            wrapper.appendChild(link);
            var image = document.createElement("img");
            image.setAttribute("src", "image?module=" + this.subsystem + "&path=" + values[i].name);
            image.setAttribute("alt", values[i].name);
            image.setAttribute("class", "figure-img img-fluid rounded");
            link.appendChild(image);
            var name_1 = document.createElement("figcaption");
            name_1.classList.add("figure-caption");
            name_1.textContent = values[i].name;
            link.appendChild(name_1);
            link.addEventListener('click', function (event) {
                var selected = event.currentTarget;
                var selectedIndex = parseInt(selected.dataset.index);
                if (selected.classList.contains('selected')) {
                    this.selectedIndex = null;
                }
                else {
                    this.selectedIndex = selectedIndex;
                }
                this.updateModel();
                this.fireEvent('onChange', this.onChange, event);
            }.bind(this));
            this.grid.appendChild(wrapper);
        }
    };
    ;
    ImageGrid.prototype.extractChangedAttributes = function () {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        return {
            selectedIndex: this.selectedIndex
        };
    };
    ;
    return ImageGrid;
}(fh_forms_handler_1.HTMLFormComponent));
exports.ImageGrid = ImageGrid;
//# sourceMappingURL=ImageGrid.js.map