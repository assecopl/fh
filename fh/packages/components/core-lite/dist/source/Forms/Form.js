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
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var inversify_1 = require("inversify");
var HTMLFormComponent_1 = require("./HTMLFormComponent");
var $ = require("jquery");
var FhContainer_1 = require("../FhContainer");
var LayoutHandler_1 = require("../LayoutHandler");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var Form = /** @class */ (function (_super) {
    __extends(Form, _super);
    function Form(formObj, parent) {
        if (parent === void 0) { parent = null; }
        var _this = _super.call(this, formObj, parent) || this;
        _this.combinedId = _this.id;
        _this.formId = _this.id;
        _this.formType = formObj.formType;
        _this.viewMode = _this.componentObj.viewMode;
        _this.state = _this.componentObj.state;
        /* TODO: remove after changing Java */
        if (_this.componentObj.modal) {
            _this.formType = 'MODAL';
        }
        if (_this.viewMode != 'NORMAL') {
            var editorClasses = $('#designedFormContainer')[0].classList;
            editorClasses.remove('editing-modal-sm');
            editorClasses.remove('editing-modal-md');
            editorClasses.remove('editing-modal-lg');
            editorClasses.remove('editing-modal-xlg');
            editorClasses.remove('editing-modal-xxlg');
            editorClasses.remove('editing-modal-full');
            if (_this.formType == 'MODAL' || _this.formType == 'MODAL_OVERFLOW') {
                switch (_this.componentObj.modalSize) {
                    case 'SMALL':
                        editorClasses.add('editing-modal-sm');
                        break;
                    case 'REGULAR':
                        editorClasses.add('editing-modal-md');
                        break;
                    case 'LARGE':
                        editorClasses.add('editing-modal-lg');
                        break;
                    case 'XLARGE':
                        editorClasses.add('editing-modal-xlg');
                        break;
                    case 'XXLARGE':
                        editorClasses.add('editing-modal-xxlg');
                        break;
                    case 'FULL':
                        editorClasses.add('editing-modal-full');
                        break;
                }
            }
            _this.formType = 'STANDARD';
        }
        if (_this.formType === 'FLOATING') {
            _this.dragging = false;
            _this.resizing = false;
            _this.position = { top: 0, left: 0 };
            _this.dimensions = { width: 0, height: 0 };
            _this.drag = { startX: 0, startY: 0, posX: 0, posY: 0, width: 0, height: 0 };
        }
        else if (_this.formType === 'HEADER') {
        }
        _this.resourcesUrlPrefix = _this.componentObj.resourcesUrlPrefix;
        _this.onManualModalClose = _this.componentObj.onManualModalClose;
        _this.components = [];
        if (_this.id && _this.id == "formDesignerComponents") {
            //FIXME ever executed ?? Looks like never. There is no form witdh with this id.
            FhContainer_1.FhContainer.get('Designer').beforeDesignerFormCreated();
        }
        if (_this.formType == 'MODAL') {
            _this.layoutHandler.blockLayoutChangeForModal();
        }
        if (_this.designMode) {
            _this.layoutHandler.blockLayoutChangeForDesigner();
        }
        if (_this.parent != null) {
            _this.container = _this.parent.contentWrapper;
        }
        else { // FORM
            _this.container = _this.layoutHandler.getLayoutContainer(_this.parentId);
            // this.mainLayout = this.componentObj.layout;
            if (_this.container == null && _this.parentId != 'MODAL_VIRTUAL_CONTAINER') {
                throw "Container '" + _this.parentId + "' not found";
            }
        }
        return _this;
    }
    Form.prototype.create = function () {
        var form = this.buildForm();
        this.htmlElement = form;
        this.component = this.htmlElement;
        if (this.formType === 'STANDARD') {
            this.container.innerHTML = '';
        }
        this.display();
        this.createComponents();
        function isDescendant(parent, child) {
            var node = child.parentNode;
            while (node != null) {
                if (node == parent) {
                    return true;
                }
                node = node.parentNode;
            }
            return false;
        }
        if (this.formType === 'MODAL' || this.formType === 'MODAL_OVERFLOW') {
            this.htmlElement.dataset.backdrop = 'static';
            this.htmlElement.dataset.keyboard = false;
            this.modalDeferred = $.Deferred();
            $(this.htmlElement).modal({ show: true, focus: true });
            $(this.htmlElement).one('shown.bs.modal', function () {
                while (this.contentWrapper != null && this.contentWrapper.firstChild)
                    this.contentWrapper.removeChild(this.contentWrapper.firstChild);
                this.renderSubcomponents();
                this.modalDeferred.resolve();
                this.focusFirstActiveInputElement();
            }.bind(this));
        }
        else {
            this.renderSubcomponents();
            this.focusFirstActiveInputElement();
        }
        if (this.viewMode == 'DESIGN') {
            this.component.dataset.designable = 'true';
            FhContainer_1.FhContainer.get('Designer').onEditedFormCreated(this);
        }
        if (this.component.clientHeight > this.container.clientHeight) {
            localStorage.setItem('updatedFormHeight', this.component.clientHeight);
            var updatedFormHeight = localStorage.getItem('updatedFormHeight');
            var parsedFormHeightWithBorder = parseInt(updatedFormHeight, 10) + 2;
            this.container.style.height = parsedFormHeightWithBorder + 'px';
            localStorage.removeItem('updatedFormHeight');
        }
        else if (this.container.clientHeight === 0) {
            this.container.style.height = 'auto';
        }
    };
    ;
    Form.prototype.destroy = function (removeFromParent) {
        this.destroyed = true;
        if (this.windowListenerMouseUp != null) {
            window.removeEventListener('mouseup', this.windowListenerMouseUp);
        }
        if (this.windowListenerMouseMove != null) {
            window.removeEventListener('mousemove', this.windowListenerMouseMove);
        }
        if (this.formType === 'STANDARD') {
            if (this.container.id === 'designedFormContainer') {
                var containerHeight = localStorage.getItem('formContainerHeight');
                this.container.style.height = parseInt(containerHeight, 10) + 'px';
                localStorage.removeItem('formContainerHeight');
                FhContainer_1.FhContainer.get('Designer').destroy();
            }
            this.container.innerHTML = '';
        }
        else if (this.formType === 'MODAL' || this.formType === 'MODAL_OVERFLOW') {
            $(this.htmlElement).on('hidden.bs.modal', function () {
                document.body.removeChild(this.container);
                this.components.forEach(function (component) {
                    component.destroy(false); // do NOT remove from parent as parent is already being removed with animation - bad visual effect in case of froms
                });
                this.nonVisualComponents.forEach(function (component) {
                    component.destroy(false); // do NOT remove from parent as parent is already being removed with animation - bad visual effect in case of froms
                });
                if (this.toolbox != null) {
                    $(this.toolbox).unbind().remove();
                    this.toolbox = null;
                }
                if (this.labelElement != null) {
                    $(this.labelElement).unbind().remove();
                    this.labelElement = null;
                }
                if (this.contentWrapper != null) {
                    $(this.contentWrapper).unbind().remove();
                    this.contentWrapper = null;
                }
            }.bind(this));
            this.modalDeferred.promise().then(function () {
                $(this.htmlElement).modal('hide');
            }.bind(this));
            $(document).off('keyup', this.keyupEvent);
        }
        else if (this.formType === 'FLOATING') {
            this.container.removeChild(this.htmlElement);
        }
        if (this.formType === 'STANDARD' || this.formType === 'FLOATING') {
            this.components.forEach(function (component) {
                component.destroy(false); // do NOT remove from parent as parent is already being removed with animation - bad visual effect in case of froms
            });
            this.nonVisualComponents.forEach(function (component) {
                component.destroy(false); // do NOT remove from parent as parent is already being removed with animation - bad visual effect in case of froms
            });
            if (this.toolbox != null) {
                $(this.toolbox).unbind().remove();
                this.toolbox = null;
            }
            if (this.labelElement != null) {
                $(this.labelElement).unbind().remove();
                this.labelElement = null;
            }
            if (this.contentWrapper != null) {
                $(this.contentWrapper).unbind().remove();
                this.contentWrapper = null;
            }
        }
    };
    ;
    Form.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'state':
                        this.state = newValue;
                        break;
                }
            }.bind(this));
        }
    };
    ;
    Form.prototype.buildForm = function () {
        var form = null;
        switch (this.formType) {
            case 'STANDARD':
                form = this.buildStandardForm();
                break;
            case 'MODAL':
            case 'MODAL_OVERFLOW':
                form = this.buildModalForm();
                break;
            case 'FLOATING':
                form = this.buildFloatingForm();
                break;
            case 'HEADER':
                form = this.buildHeaderForm();
                break;
        }
        return form;
    };
    ;
    Form.prototype.buildModalForm = function () {
        var container = document.createElement('div');
        container.id = this.id + '_modal';
        document.body.appendChild(container);
        this.container = container;
        var form = document.createElement('div');
        form.id = this.id;
        ['fc', 'form', 'modal', 'fade'].forEach(function (cssClass) {
            form.classList.add(cssClass);
        });
        form['role'] = 'dialog';
        var dialog = document.createElement('div');
        dialog.classList.add('modal-dialog');
        switch (this.componentObj.modalSize) {
            case 'SMALL':
                dialog.classList.add('modal-sm');
                break;
            case 'REGULAR':
                dialog.classList.add('modal-md');
                break;
            case 'LARGE':
                dialog.classList.add('modal-lg');
                break;
            case 'XLARGE':
                dialog.classList.add('modal-xlg');
                break;
            case 'XXLARGE':
                dialog.classList.add('modal-xxlg');
                break;
            case 'FULL':
                dialog.classList.add('modal-full');
                break;
        }
        var content = document.createElement('div');
        content.classList.add('modal-content');
        if (!this.componentObj.hideHeader) {
            var header = document.createElement('div');
            header.classList.add('modal-header');
            var title = document.createElement('h5');
            title.classList.add('modal-title');
            this.addCloudIcon(title);
            this.labelElement = document.createElement("span");
            title.appendChild(this.labelElement);
            this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
            header.appendChild(title);
            if (this.onManualModalClose != null) {
                header.appendChild(this.buildCloseButton());
            }
            content.appendChild(header);
        }
        var body = document.createElement('div');
        body.classList.add('modal-body');
        if (this.componentObj.modalMaxHeight) {
            body.style['overflow-y'] = 'auto';
            body.style['height'] = this.componentObj.modalMaxHeight + 'px';
        }
        var row = document.createElement('div');
        row.classList.add('row');
        row.classList.add('eq-row');
        this.contentWrapper = row;
        body.appendChild(row);
        content.appendChild(body);
        dialog.appendChild(content);
        form.appendChild(dialog);
        return form;
    };
    ;
    Form.prototype.focusFirstActiveInputElement = function () {
        if (this.designMode) {
            return;
        }
        $(this.component).find(':input:enabled:not([readonly]):not(button):first').trigger('focus');
    };
    Form.prototype.buildFloatingForm = function () {
        var form = document.createElement('div');
        form.id = this.id;
        ['fc', 'form', 'floating', 'card', 'card-default'].forEach(function (cssClass) {
            form.classList.add(cssClass);
        });
        var heading = document.createElement('div');
        heading.classList.add('card-header');
        if (this.onManualModalClose != null) {
            heading.appendChild(this.buildCloseButton());
        }
        var title = document.createElement('h4');
        title.id = this.id + '_label';
        title.classList.add('card-title');
        this.addCloudIcon(title);
        this.labelElement = document.createElement("span");
        title.appendChild(this.labelElement);
        this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
        heading.appendChild(title);
        heading.addEventListener('mousedown', function (event) {
            event.preventDefault();
            this.mouseDown(event);
            this.dragging = true;
        }.bind(this));
        form.appendChild(heading);
        var body = document.createElement('div');
        body.classList.add('card-body');
        var row = document.createElement('div');
        row.classList.add('row');
        row.classList.add('eq-row');
        this.contentWrapper = row;
        body.appendChild(row);
        form.appendChild(body);
        var resizeHandle = document.createElement('div');
        resizeHandle.classList.add('resizeHandle');
        resizeHandle.addEventListener('mousedown', function (event) {
            event.preventDefault();
            this.mouseDown(event);
            this.resizing = true;
        }.bind(this));
        form.appendChild(resizeHandle);
        window.addEventListener('mousemove', this.windowListenerMouseMove = function (event) {
            if (this.dragging || this.resizing) {
                event.preventDefault();
                var xMove = event.clientX - this.drag.startX;
                var yMove = event.clientY - this.drag.startY;
                var newX = this.drag.posX + (this.dragging ? xMove : 0);
                var newY = this.drag.posY + (this.dragging ? yMove : 0);
                var newWidth = this.drag.width + (this.resizing ? xMove : 0);
                var newHeight = this.drag.height + (this.resizing ? yMove : 0);
                var leftBorder = newX;
                var rightBorder = leftBorder + newWidth;
                var topBorder = newY;
                var bottomBorder = topBorder + newHeight;
            }
            if (this.dragging) {
                if (leftBorder >= 0 && rightBorder <= window.innerWidth) {
                    this.position.left = leftBorder;
                    this.htmlElement.style.left = this.position.left + 'px';
                }
                if (topBorder >= 50 && bottomBorder <= window.innerHeight) {
                    this.position.top = topBorder;
                    this.htmlElement.style.top = this.position.top + 'px';
                }
            }
            if (this.resizing) {
                if (leftBorder >= 0 && rightBorder <= window.innerWidth) {
                    this.dimensions.width = newWidth;
                    this.htmlElement.style.width = this.dimensions.width + 'px';
                }
                if (topBorder >= 50 && bottomBorder <= window.innerHeight) {
                    this.dimensions.height = newHeight;
                    this.htmlElement.style.height = this.dimensions.height + 'px';
                }
            }
        }.bind(this));
        window.addEventListener('mouseup', this.windowListenerMouseUp = function (event) {
            this.dragging = false;
            this.resizing = false;
        }.bind(this));
        return form;
    };
    ;
    Form.prototype.buildStandardForm = function () {
        var form = document.createElement('div');
        form.id = this.id;
        ['fc', 'form', 'card'].forEach(function (cssClass) {
            form.classList.add(cssClass);
        });
        if (this.componentObj.label && !this.componentObj.hideHeader) {
            var heading = document.createElement('div');
            heading.classList.add('card-header');
            var title = document.createElement('div');
            title.id = this.id + '_label';
            this.addCloudIcon(title);
            this.labelElement = document.createElement("strong");
            this.labelElement.classList.add('card-title');
            title.appendChild(this.labelElement);
            this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
            heading.appendChild(title);
            form.appendChild(heading);
        }
        var body = document.createElement('div');
        body.classList.add('card-body');
        var row = document.createElement('div');
        row.classList.add('row');
        row.classList.add('eq-row');
        this.contentWrapper = row;
        body.appendChild(row);
        form.appendChild(body);
        return form;
    };
    ;
    Form.prototype.buildHeaderForm = function () {
        var fluid = document.createElement('div');
        fluid.id = this.id;
        var collapse = document.createElement('div');
        collapse.id = 'navbar-main';
        this.contentWrapper = collapse;
        fluid.appendChild(collapse);
        return fluid;
    };
    ;
    Form.prototype.buildCloseButton = function () {
        var button = document.createElement('button');
        button.type = 'button';
        button.classList.add('close');
        button.innerHTML = '&times;';
        button.addEventListener('click', function (event) {
            this.fireEventWithLock('onManualModalClose', this.onManualModalClose, event);
        }.bind(this));
        return button;
    };
    ;
    Form.prototype.mouseDown = function (event) {
        var styles = window.getComputedStyle(this.htmlElement);
        this.drag.posX = this.position.left = parseFloat(styles.getPropertyValue('left'));
        this.drag.posY = this.position.top = parseFloat(styles.getPropertyValue('top'));
        this.drag.width = this.dimensions.width = parseFloat(styles.getPropertyValue('width'));
        this.drag.height = this.dimensions.height = parseFloat(styles.getPropertyValue('height'));
        this.drag.startX = event.clientX;
        this.drag.startY = event.clientY;
    };
    ;
    Form.prototype.addCloudIcon = function (titleElement) {
        if (this.componentObj.fromCloud) {
            var cloudIcon = document.createElement('span');
            cloudIcon.classList.add('fa');
            cloudIcon.classList.add('fa-cloud');
            cloudIcon.style['margin-right'] = '5px';
            cloudIcon.style['top'] = '2px';
            cloudIcon.title = this.componentObj.cloudServerName;
            titleElement.appendChild(cloudIcon);
        }
    };
    ;
    Form.prototype.setPresentationStyle = function (presentationStyle) {
        return;
    };
    __decorate([
        lazyInject('LayoutHandler'),
        __metadata("design:type", LayoutHandler_1.LayoutHandler)
    ], Form.prototype, "layoutHandler", void 0);
    Form = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [Object, Object])
    ], Form);
    return Form;
}(HTMLFormComponent_1.HTMLFormComponent));
exports.Form = Form;
//# sourceMappingURL=Form.js.map