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
var MarkdownGrid = /** @class */ (function (_super) {
    __extends(MarkdownGrid, _super);
    function MarkdownGrid(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.onFolderDoubleClick = null;
        _this.showDir = null;
        _this.values = _this.componentObj.values;
        _this.subsystem = _this.componentObj.subsystem;
        _this.grid = null;
        _this.selectedIndex = null;
        _this.subDirectories = _this.componentObj.subDirectories;
        _this.onFolderDoubleClick = _this.componentObj.onFolderDoubleClick;
        return _this;
    }
    MarkdownGrid.prototype.create = function () {
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
    MarkdownGrid.prototype.update = function (change) {
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
    MarkdownGrid.prototype.clearValues = function () {
        var element = this.grid.firstChild;
        while (element) {
            this.grid.removeChild(element);
            element = this.grid.firstChild;
        }
    };
    ;
    MarkdownGrid.prototype.setValues = function (values) {
        if (values == null) {
            return;
        }
        for (var i = 0; i < values.length; i++) {
            this.addElement(values[i], i);
        }
    };
    ;
    MarkdownGrid.prototype.extractChangedAttributes = function () {
        if (this.designMode == true) {
            return this.changesQueue.extractChangedAttributes();
        }
        return {
            selectedIndex: this.selectedIndex
        };
    };
    ;
    MarkdownGrid.prototype.addElement = function (val, index) {
        var wrapper = document.createElement("figure");
        wrapper.setAttribute("class", "figure col-lg-2 text-center pt-1");
        var link = document.createElement("a");
        link.href = "#";
        link.setAttribute("class", "resource border-white pointer");
        if (index === this.selectedIndex) {
            link.classList.add("selected");
            link.classList.add("border-secondary");
            link.classList.remove("border-white");
        }
        link.dataset.index = index.toString();
        wrapper.appendChild(link);
        var image = document.createElement("i");
        // image.setAttribute("src", "markdown?module=" + this.subsystem + "&path=" + values[i].name);
        // image.setAttribute("alt", values[i].name);
        // image.setAttribute("class", "figure-img img-fluid rounded");
        if (val.directory) {
            image.setAttribute("class", "fas fa-folder figure-img img-fluid rounded text-warning");
        }
        else {
            image.setAttribute("class", "fas fa-file-code figure-img img-fluid rounded");
        }
        image.setAttribute("style", "font-size:45px");
        link.appendChild(image);
        var name = document.createElement("figcaption");
        name.classList.add("figure-caption");
        name.textContent = val.name;
        link.appendChild(name);
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
        if (this.onFolderDoubleClick && val.directory) {
            link.addEventListener('dblclick', function (event) {
                event.stopPropagation();
                event.preventDefault();
                var selected = event.currentTarget;
                var selectedIndex = parseInt(selected.dataset.index);
                this.selectedIndex = selectedIndex;
                this.updateModel();
                this.fireEvent('onChange', this.onChange, event);
                this.fireEvent('onFolderDoubleClick', this.onFolderDoubleClick, event);
            }.bind(this));
        }
        this.grid.appendChild(wrapper);
    };
    MarkdownGrid.prototype.addFolderUp = function (val, index) {
        var wrapper = document.createElement("figure");
        wrapper.setAttribute("class", "figure col-lg-2 text-center pt-1");
        var link = document.createElement("a");
        link.href = "#";
        link.setAttribute("class", "resource border-white pointer");
        if (index === this.selectedIndex) {
            link.classList.add("selected");
            link.classList.add("border-secondary");
            link.classList.remove("border-white");
        }
        link.dataset.index = index.toString();
        wrapper.appendChild(link);
        var image = document.createElement("i");
        // image.setAttribute("src", "markdown?module=" + this.subsystem + "&path=" + values[i].name);
        // image.setAttribute("alt", values[i].name);
        // image.setAttribute("class", "figure-img img-fluid rounded");
        image.setAttribute("class", "fas fa-folder-open figure-img img-fluid rounded");
        image.setAttribute("style", "font-size:45px");
        link.appendChild(image);
        var name = document.createElement("figcaption");
        name.classList.add("figure-caption");
        name.textContent = "...";
        link.appendChild(name);
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
    };
    return MarkdownGrid;
}(fh_forms_handler_1.HTMLFormComponent));
exports.MarkdownGrid = MarkdownGrid;
//# sourceMappingURL=MarkdownGrid.js.map