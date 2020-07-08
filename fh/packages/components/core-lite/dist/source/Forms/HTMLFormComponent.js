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
require("bootstrap/js/dist/tooltip");
var FormComponent_1 = require("./FormComponent");
var FhContainer_1 = require("../FhContainer");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var I18n_1 = require("../I18n/I18n");
var FHML_1 = require("../FHML");
var $ = require("jquery");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var HTMLFormComponent = /** @class */ (function (_super) {
    __extends(HTMLFormComponent, _super);
    function HTMLFormComponent(componentObj, parent) {
        var _this = _super.call(this, componentObj, parent) || this;
        _this.styleClasses = [];
        _this.hintType = 'STANDARD';
        _this.hintInputGroup = false;
        _this.hintInitialized = false;
        _this.autocomplete = null;
        _this.ariaLabel = null;
        _this.htmlAccessibilityRole = null;
        if (_this.parent != null) {
            _this.container = _this.parent.contentWrapper;
        }
        else { // FORM
            _this.container = document.getElementById(_this.parentId);
            if (_this.container == null && _this.parentId != 'MODAL_VIRTUAL_CONTAINER') {
                throw 'Container \'' + _this.parentId + '\' not found';
            }
        }
        _this.combinedId = _this.parentId + '_' + _this.id;
        _this.hintType = _this.componentObj.hintType;
        _this.hintInputGroup = _this.componentObj.hintInputGroup ? _this.componentObj.hintInputGroup : false;
        _this.accessibility = _this.componentObj.accessibility;
        _this.invisible = _this.componentObj.invisible;
        _this.presentationStyle = _this.componentObj.presentationStyle;
        _this.requiredField = _this.componentObj.required;
        _this.autocomplete = _this.componentObj.autocomplete ? _this.componentObj.autocomplete : null;
        _this.ariaLabel = _this.componentObj.ariaLabel ? _this.componentObj.ariaLabel : null;
        _this.htmlAccessibilityRole = _this.componentObj.htmlAccessibilityRole ? _this.componentObj.htmlAccessibilityRole : null;
        _this.designMode = _this.componentObj.designMode || (_this.parent != null && _this.parent.designMode);
        if (_this.designMode) {
            _this.buildDesingerToolbox();
        }
        _this.handleWidth();
        if (typeof _this.oldValue === 'undefined') {
            if (_this.componentObj.rawValue != null) {
                _this.oldValue = _this.componentObj.rawValue;
            }
            else {
                _this.oldValue = _this.componentObj.value;
            }
        }
        _this.inlineStyle = _this.componentObj.inlineStyle;
        _this.wrapperStyle = _this.componentObj.wrapperStyle;
        _this.push = _this.componentObj.push;
        _this.rawValue = _this.oldValue;
        _this.htmlElement = null;
        _this.component = _this.htmlElement;
        _this.contentWrapper = _this.component;
        _this.styleClasses = (_this.componentObj.styleClasses || '').split(/[, ]/);
        _this.hint = _this.componentObj.hint || null;
        _this.hintPlacement = _this.componentObj.hintPlacement || 'auto';
        if (_this.componentObj.hintTrigger) {
            switch (_this.componentObj.hintTrigger) {
                case 'HOVER_FOCUS':
                    _this.hintTrigger = 'hover focus';
                    break;
                case 'HOVER':
                    _this.hintTrigger = 'hover';
                    break;
            }
        }
        else {
            _this.hintTrigger = 'hover focus';
        }
        _this.hintElement = null;
        if (_this.formId === 'designerComponents' || _this.formId === 'designerToolbox') {
            var a = _this;
            while ((a = a.parent) != null) {
                if (a.component != null && a.component.classList.contains('designerToolbox')) {
                    FhContainer_1.FhContainer.get('Designer').onDesignerToolboxComponentCreated(a);
                    break;
                }
            }
        }
        _this.requiredElement = null;
        _this.areSubcomponentsRendered = false;
        /* Languages */
        _this.language = _this.componentObj.language || null;
        _this.translationItems = [];
        _this.labelElement = null;
        return _this;
    }
    /* Create component and assign it's HTML to this.htmlElement */
    HTMLFormComponent.prototype.create = function () {
        this.component = document.createElement('div');
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;
        this.hintElement = this.component;
        this.display();
        /* Add subcomponents */
        _super.prototype.create.call(this);
    };
    /* Append component to container */
    HTMLFormComponent.prototype.display = function () {
        this.processAutocomplete(this.autocomplete);
        this.setAccessibility(this.accessibility);
        this.setPresentationStyle(this.presentationStyle);
        this.enableStyleClasses();
        this.setRequiredField(this.requiredField);
        if (this.designMode) {
            FhContainer_1.FhContainer.get('Designer').addToolboxListeners(this);
            if (this.toolbox) {
                this.htmlElement.appendChild(this.toolbox);
            }
            if (this.component.classList.contains('tabContainer')) {
                this.component.addEventListener('click', function (e) {
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    // get the tab's id and click on the tab to edit its properties
                    var clickedTabId = e.target.getAttribute('data-tab-id');
                    var clickedTab = document.getElementById(clickedTabId);
                    if (clickedTab !== null) {
                        clickedTab.click();
                    }
                });
            }
            // click on Column Value Label opens Column properties
            if (this.component.classList.contains('valueBasedLabel')) {
                this.component.addEventListener('click', function (e) {
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    var columnId = this.component.dataset.columnId;
                    var column = this.component.closest('.fc.table').querySelector('.' + columnId);
                    column.click();
                }.bind(this));
            }
            if (this.component.classList.contains('outputLabel')) {
                this.component.addEventListener('click', function (e) {
                    var _this = this;
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    this.component.focus();
                    this.fireEvent('onformedit_elementedit', 'elementedit');
                    document.addEventListener('keyup', function (event) {
                        _this.handleDeleteBtnEvent(event);
                    });
                }.bind(this));
            }
            this.htmlElement.addEventListener('click', function (e) {
                var _this = this;
                e.stopImmediatePropagation();
                e.preventDefault();
                this.component.focus();
                this.fireEvent('onformedit_elementedit', 'elementedit');
                document.addEventListener('keyup', function (event) {
                    _this.handleDeleteBtnEvent(event);
                });
            }.bind(this));
            if (this.contentWrapper && this.contentWrapper.classList.contains('button')) {
                this.component.addEventListener('click', function (e) {
                    var _this = this;
                    this.component.focus();
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    this.fireEvent('onformedit_elementedit', 'elementedit');
                    document.addEventListener('keyup', function (event) {
                        _this.handleDeleteBtnEvent(event);
                    });
                }.bind(this));
            }
            if (this.contentWrapper && this.contentWrapper.classList.contains('selectOneMenu')) {
                this.component.addEventListener('click', function (e) {
                    var _this = this;
                    this.component.focus();
                    e.stopImmediatePropagation();
                    this.fireEvent('onformedit_elementedit', 'elementedit');
                    document.addEventListener('keyup', function (event) {
                        _this.handleDeleteBtnEvent(event);
                    });
                }.bind(this));
            }
            if (this.contentWrapper && this.contentWrapper.classList.contains('fileUpload')) {
                this.component.addEventListener('click', function (e) {
                    var _this = this;
                    this.component.focus();
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    this.fireEvent('onformedit_elementedit', 'elementedit');
                    document.addEventListener('keyup', function (event) {
                        _this.handleDeleteBtnEvent(event);
                    });
                }.bind(this));
            }
        }
        this.container.appendChild(this.htmlElement);
        if (this.hint !== null) {
            this.initHint();
        }
    };
    HTMLFormComponent.prototype.render = function () {
        if (this.htmlElement) {
            this.display();
        }
        if (!this.areSubcomponentsRendered) {
            this.renderSubcomponents();
            this.areSubcomponentsRendered = true;
        }
    };
    HTMLFormComponent.prototype.renderSubcomponents = function () {
        this.components.forEach(function (component) {
            component.render();
        });
    };
    HTMLFormComponent.prototype.initHint = function () {
        if (this.hintElement && !this.hintInitialized && this.hint) {
            var tooltipOptions = {
                placement: this.hintPlacement,
                title: this.hint,
                trigger: this.hintTrigger,
                html: true,
                boundary: 'window'
            };
            if (this.hintType == 'STATIC') {
                if (tooltipOptions.placement == 'auto') {
                    tooltipOptions.placement = 'top';
                }
                //Change tooltip trigger to click
                tooltipOptions.trigger = 'click';
                //Create tooltip element
                var ttip = document.createElement('i');
                ttip.className = 'hint-ico-help fa';
                ttip.setAttribute('aria-hidden', 'true');
                this.hintElement = ttip;
                if (this.inputGroupElement && this.hintInputGroup) {
                    var ttipButton = document.createElement('div');
                    this.inputGroupElement.classList.add('hint-static');
                    var span = document.createElement('span');
                    span.classList.add('input-group-text');
                    span.appendChild(ttip);
                    ttip.classList.add('fa-question');
                    ttipButton.appendChild(span);
                    this.htmlElement.classList.add('hasInputHelpIcon');
                    this.hintElement = ttipButton;
                    ttipButton.classList.add('input-group-append');
                    this.inputGroupElement.appendChild(ttipButton);
                }
                else {
                    /**
                     * Change typical behaviour by overwrite this function in component.
                     */
                    ttip.classList.add('fa-question-circle');
                    ttip.classList.add('pl-2');
                    var element = this.processStaticHintElement(ttip);
                    element ? element.classList.add('hint-static') : null;
                }
            }
            $(this.hintElement).tooltip(tooltipOptions);
            this.hintInitialized = true;
        }
    };
    /**
     *
     * @param ttip
     */
    HTMLFormComponent.prototype.processStaticHintElement = function (ttip) {
        if (this.labelElement && !this.labelElement.classList.contains('sr-only')) {
            this.labelElement.appendChild(ttip);
            return this.labelElement;
        }
        else {
            return null;
        }
    };
    HTMLFormComponent.prototype.destroyHint = function () {
        if (this.hintElement && this.hintInitialized) {
            $(this.hintElement).tooltip('hide');
            $(this.hintElement).tooltip('disable');
            $(this.hintElement).tooltip('dispose');
            this.hintInitialized = false;
        }
    };
    /* Destroy component */
    HTMLFormComponent.prototype.destroy = function (removeFromParent) {
        this.destroyHint();
        if (removeFromParent && this.parent != null && this.parent.contentWrapper != null && this.htmlElement != null) {
            try {
                if (this.htmlElement.parentNode === this.parent.contentWrapper) {
                    this.parent.contentWrapper.removeChild(this.htmlElement);
                }
            }
            catch (e) {
                // //console.log("Error while destroying ", this, e);
            }
        }
        if (this.toolbox != null) {
            $(this.toolbox).unbind().remove();
            this.toolbox = null;
        }
        if (this.labelElement != null) {
            $(this.labelElement).unbind().remove();
            this.labelElement = null;
        }
        if (this.inputGroupElement != null) {
            $(this.inputGroupElement).unbind().remove();
            this.inputGroupElement = null;
        }
        if (this.focusableComponent != null) {
            $(this.focusableComponent).unbind().remove();
            this.focusableComponent = null;
        }
        if (this.hintElement != null) {
            $(this.hintElement).unbind().remove();
            this.hintElement = null;
        }
        if (this.component != null) {
            $(this.component).unbind().remove();
            this.component = null;
        }
        if (this.htmlElement != null) {
            $(this.htmlElement).unbind().remove();
            this.htmlElement = null;
        }
        _super.prototype.destroy.call(this, removeFromParent);
        document.removeEventListener('keyup', this.handleDeleteBtnEvent);
    };
    /* Update component */
    HTMLFormComponent.prototype.update = function (change) {
        _super.prototype.update.call(this, change);
        this.processAddedComponents(change);
        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'accessibility':
                        this.setAccessibility(newValue);
                        break;
                    case 'autocomplete':
                        this.processAutocomplete(newValue);
                        break;
                    case 'presentationStyle':
                        this.setPresentationStyle(newValue);
                        break;
                    case 'messageForField':
                        this.component.title = newValue || '';
                        break;
                    case 'width':
                        this.setWrapperWidth(this.htmlElement, this.width, newValue);
                        this.width = newValue;
                        break;
                    case 'language':
                        this.language = newValue;
                        this.changeLanguage(this.language);
                        break;
                    case 'required':
                        this.requiredField = newValue;
                        this.setRequiredField(this.requiredField);
                        break;
                    case 'label':
                        if (this.labelElement != null) {
                            this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(newValue);
                            this.updateLabelClass(newValue);
                        }
                        break;
                    case 'hint':
                        this.hint = newValue;
                        if (this.hintElement) {
                            this.hintElement.dataset.title = this.hint;
                        }
                        this.destroyHint();
                        this.initHint();
                        break;
                }
            }.bind(this));
        }
    };
    HTMLFormComponent.prototype.updateLabelClass = function (newLabel) {
        if (this.labelElement != null) {
            if (newLabel != null && newLabel != '') {
                this.labelElement.classList.remove('empty-label');
            }
            else {
                this.labelElement.classList.add('empty-label');
            }
        }
    };
    HTMLFormComponent.prototype.processAddedComponents = function (change) {
        if (change.addedComponents) {
            Object.keys(change.addedComponents).forEach(function (afterId) {
                var referenceNode = null;
                if (afterId === '-') {
                    referenceNode = this.contentWrapper.firstChild || null;
                }
                else if (this.findComponent(afterId)) {
                    var elem = this.findComponent(afterId).htmlElement;
                    referenceNode = typeof elem !== 'undefined' ? elem.nextSibling || null : null;
                }
                change.addedComponents[afterId].forEach(function (componentObj) {
                    var component = this.findComponent(componentObj.id);
                    if (component instanceof HTMLFormComponent) {
                        if (referenceNode) {
                            if (component.htmlElement.id != referenceNode.id) {
                                this.contentWrapper.insertBefore(component.htmlElement, referenceNode);
                            }
                            var thisNodeComponent = this.findComponent(component.htmlElement.id);
                            if (thisNodeComponent.htmlElement &&
                                thisNodeComponent.htmlElement.nextSibling) {
                                referenceNode = thisNodeComponent.htmlElement.nextSibling;
                            }
                        }
                        else {
                            this.contentWrapper.appendChild(component.htmlElement);
                        }
                    }
                    else if (referenceNode) {
                        component.referenceNode = referenceNode.nextSibling || null;
                    }
                }.bind(this));
            }.bind(this));
        }
    };
    HTMLFormComponent.prototype.updateModel = function () {
        this.rawValue = this.component.value;
    };
    HTMLFormComponent.prototype.accessibilityResolve = function (node, access) {
        switch (node.nodeName) {
            case 'BUTTON':
            case 'INPUT':
            case 'TEXTAREA':
            case 'SELECT':
            case 'OPTION':
            case 'OPTGROUP':
            case 'FIELDSET':
                if (access !== 'EDIT') {
                    node.setAttribute('disabled', 'disabled');
                }
                else {
                    node.removeAttribute('disabled');
                }
                node.classList.add('fc-editable');
                break;
            default:
                if (access !== 'EDIT') {
                    node.classList.add('fc-disabled');
                    node.classList.add('disabled');
                }
                else {
                    node.classList.add('fc-editable');
                }
        }
    };
    HTMLFormComponent.prototype.setAccessibility = function (accessibility) {
        if (accessibility !== 'HIDDEN') {
            this.htmlElement.classList.remove('d-none');
            this.htmlElement.classList.remove('invisible');
        }
        if (accessibility !== 'DEFECTED' || accessibility !== 'VIEW') {
            this.component.classList.remove('fc-disabled');
            this.component.classList.remove('disabled');
        }
        if (accessibility !== 'EDIT') {
            this.component.classList.remove('fc-editable');
        }
        if (accessibility !== 'VIEW') {
            this.component.classList.remove('disabledElement');
        }
        switch (accessibility) {
            case 'EDIT':
                this.accessibilityResolve(this.component, 'EDIT');
                break;
            case 'VIEW':
                this.accessibilityResolve(this.component, 'VIEW');
                break;
            case 'HIDDEN':
                if (!this.designMode) {
                    if (this.invisible) {
                        this.htmlElement.classList.add('invisible');
                    }
                    else {
                        this.htmlElement.classList.add('d-none');
                    }
                }
                break;
            case 'DEFECTED':
                this.accessibilityResolve(this.component, 'DEFECTED');
                this.component.title = 'Broken control';
                break;
        }
        this.accessibility = accessibility;
    };
    HTMLFormComponent.prototype.setPresentationStyle = function (presentationStyle) {
        if (this.parent != null) {
            // @ts-ignore
            this.parent.setPresentationStyle(presentationStyle);
        }
        ['border', 'border-success', 'border-info', 'border-warning', 'border-danger', 'is-invalid'].forEach(function (cssClass) {
            this.getMainComponent().classList.remove(cssClass);
        }.bind(this));
        switch (presentationStyle) {
            case 'BLOCKER':
            case 'ERROR':
                ['is-invalid', 'border', 'border-danger'].forEach(function (cssClass) {
                    this.getMainComponent().classList.add(cssClass);
                }.bind(this));
                break;
            case 'OK':
                ['border', 'border-success'].forEach(function (cssClass) {
                    this.getMainComponent().classList.add(cssClass);
                }.bind(this));
                break;
            case 'INFO':
                ['border', 'border-info'].forEach(function (cssClass) {
                    this.getMainComponent().classList.add(cssClass);
                }.bind(this));
                break;
            case 'WARNING':
                ['border', 'border-warning'].forEach(function (cssClass) {
                    this.getMainComponent().classList.add(cssClass);
                }.bind(this));
                break;
        }
        this.presentationStyle = presentationStyle;
    };
    HTMLFormComponent.prototype.getMainComponent = function () {
        return this.component;
    };
    HTMLFormComponent.prototype.addStyles = function () {
        this.handleHeight();
        this.resolveLabelPosition();
        this.addAlignStyles();
        this.handlemarginAndPAddingStyles();
    };
    HTMLFormComponent.prototype.hasHeight = function () {
        return this.componentObj.height != undefined && this.componentObj.height != '' && this.componentObj.height != null;
    };
    HTMLFormComponent.prototype.handleHeight = function () {
        if (this.componentObj.height != undefined) {
            var height = this.componentObj.height;
            if (height.indexOf('%') !== -1) {
                height = height.replace('px', '');
                this.htmlElement.style.height = height;
                height = '100%';
            }
            this.component.classList.add('hasHeight');
            this.component.classList.add('container' + this.componentObj.type);
            this.component.style.height = height;
            this.component.style['overflow-y'] = 'auto';
        }
        if (this.componentObj.inputSize != undefined) {
            this.component.style.width = this.inputSize + '%';
        }
    };
    HTMLFormComponent.prototype.addAlignStyles = function () {
        if (this.componentObj.horizontalAlign && this.htmlElement) {
            this.htmlElement.classList.add('align-' + this.componentObj.horizontalAlign.toLowerCase());
        }
        if (this.componentObj.verticalAlign && this.htmlElement) {
            this.htmlElement.classList.add('valign-' + this.componentObj.verticalAlign.toLowerCase());
            switch (this.componentObj.verticalAlign.toLowerCase()) {
                case 'bottom':
                    this.htmlElement.classList.add('align-self-end');
                    break;
                case 'top':
                    this.htmlElement.classList.add('align-self-start');
                    break;
                case 'middle':
                    this.htmlElement.classList.add('align-self-center');
                    break;
            }
        }
    };
    HTMLFormComponent.prototype.resolveLabelPosition = function () {
        if (this.componentObj.labelPosition != undefined) {
            var labelPosition = this.componentObj.labelPosition.toLowerCase();
            if (labelPosition != 'up') {
                $(this.component).closest('.fc.wrapper').addClass('positioned');
                $(this.component).closest('.fc.wrapper').addClass(labelPosition);
            }
            if (labelPosition === 'left' || labelPosition === 'right') {
                this.resolveInputSize();
            }
        }
    };
    HTMLFormComponent.prototype.setInputAndLabelPosition = function (property, labelElement, inputElement) {
        if (property.toString().indexOf('px', property.length - 2) !== -1) {
            if (labelElement !== null) {
                labelElement.style.width = property;
            }
            var group = this.htmlElement.querySelector('.input-group');
            if (group != null) {
                inputElement = group;
            }
            if (inputElement) {
                inputElement.classList.add('stretched');
                inputElement.style.width = 'calc(100% - ' + property + ')';
            }
        }
        else {
            var labelWidth = Math.max(0, Math.min(99, parseInt(property)));
            labelElement.style.width = labelWidth + '%';
            var inputWidth = 100 - labelWidth;
            if (inputElement) {
                inputElement.style.width = inputWidth + '%';
            }
        }
    };
    HTMLFormComponent.prototype.resolveInputSize = function () {
        if (this.componentObj.labelSize != undefined) {
            if (this.componentObj.labelPosition == 'LEFT') {
                this.setInputAndLabelPosition(this.componentObj.labelSize, this.htmlElement.querySelector('.col-form-label'), this.getQueryForInputSize());
            }
            else if (this.componentObj.labelPosition == 'RIGHT') {
                this.setInputAndLabelPosition(this.componentObj.labelSize, this.htmlElement.querySelector('.col-form-label'), this.getQueryForInputSize());
            }
            return; // jesli labelSize jest zdefiniowane, to inputSize nie dziala
        }
        if (this.componentObj.inputSize != undefined) {
            var inputWidth = Math.max(0, Math.min(99, parseInt(this.componentObj.inputSize)));
            var labelWidth = 100 - inputWidth;
            var label = this.htmlElement.querySelector('.col-form-label');
            if (label) {
                label.style.width = labelWidth + '%';
            }
            var input = this.getQueryForInputSize();
            if (input) {
                input.style.width = inputWidth + '%';
            }
        }
    };
    HTMLFormComponent.prototype.getQueryForInputSize = function () {
        if (this.inputGroupElement != null) {
            return this.htmlElement.querySelector('.input-group');
        }
        else {
            return this.component.querySelector('.form-control');
        }
    };
    HTMLFormComponent.prototype.enableStyleClasses = function () {
        if (this.styleClasses.length && this.styleClasses[0] != '') {
            this.styleClasses.forEach(function (cssClass) {
                if (cssClass) {
                    this.component.classList.add(cssClass);
                }
            }.bind(this));
        }
    };
    HTMLFormComponent.prototype.setWrapperWidth = function (wrapper, oldWidth, newWidth) {
        if (oldWidth) {
            oldWidth.forEach(function (width) {
                if (HTMLFormComponent.bootstrapColRegexp.test(width)) {
                    //In bootstrap 4 "co-xs-12" was replaced with "col-12" so we need to delete it from string.
                    wrapper.classList.remove('col-' + width.replace('xs-', '-'));
                }
                else if (HTMLFormComponent.bootstrapColWidthRegexp.test(width)) {
                    wrapper.classList.remove('exactWidth');
                    wrapper.style.width = undefined;
                }
                else {
                    console.error("Invalid width '" + width + "' for component '" + this.id + "'.");
                }
            }.bind(this));
        }
        newWidth.forEach(function (width) {
            if (HTMLFormComponent.bootstrapColRegexp.test(width)) {
                //In bootstrap 4 "co-xs-12" was replaced with "col-12" so we need to delete it from string.
                wrapper.classList.add('col-' + width.replace('xs-', '-'));
            }
            else if (HTMLFormComponent.bootstrapColWidthRegexp.test(width)) {
                wrapper.classList.add('exactWidth');
                wrapper.style.width = width;
            }
            else {
                console.error("Invalid width '" + width + "' for component '" + this.id + "'.");
            }
        }.bind(this));
    };
    HTMLFormComponent.prototype.wrap = function (skipLabel, isInputElement) {
        if (skipLabel === void 0) { skipLabel = false; }
        if (isInputElement === void 0) { isInputElement = false; }
        var wrappedComponent = this.innerWrap();
        var wrapper = document.createElement('div');
        ['fc', 'wrapper'].forEach(function (cssClass) {
            wrapper.classList.add(cssClass);
        });
        this.wrapInner(wrapper, wrappedComponent, skipLabel, isInputElement);
    };
    HTMLFormComponent.prototype.wrapInner = function (wrapper, wrappedComponent, skipLabel, isInputElement) {
        if (skipLabel === void 0) { skipLabel = false; }
        if (isInputElement === void 0) { isInputElement = false; }
        if (this.width) {
            // @ts-ignore
            this.setWrapperWidth(wrapper, undefined, this.width);
        }
        else {
            wrapper.classList.add('inline');
        }
        //if (!skipLabel) {
        //    let label = document.createElement('label');
        //    label.classList.add('col-form-label');
        //    // label.classList.add('card-title');
        //    label.htmlFor = this.id;
        //    label.innerHTML = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
        //    wrapper.appendChild(label);
        //    this.labelElement = label;
        //}
        if (!skipLabel) {
            var labelValue = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
            if (labelValue.length > 0) {
                var label = document.createElement('label');
                label.classList.add('col-form-label');
                // label.classList.add('card-title');
                label.innerHTML = labelValue;
                label.id = this.id + "_label";
                label.setAttribute('for', this.componentObj.id);
                //this.component.setAttribute('aria-describedby', label.id);
                wrapper.appendChild(label);
                this.labelElement = label;
                if (!isInputElement) {
                    this.component.setAttribute("aria-labelledby", label.id);
                }
                else {
                    label.htmlFor = this.id;
                }
            }
            else {
                this.processAriaLabel();
            }
        }
        else {
            this.processAriaLabel();
        }
        if (isInputElement) {
            var inputGroup = document.createElement('div');
            inputGroup.classList.add('input-group');
            wrapper.appendChild(inputGroup);
            inputGroup.appendChild(wrappedComponent);
            this.inputGroupElement = inputGroup;
        }
        else {
            wrapper.appendChild(wrappedComponent);
        }
        if (this.inlineStyle) {
            this.component.setAttribute('style', this.inlineStyle);
        }
        if (this.wrapperStyle) {
            var existingStyleClasses = wrapper.getAttribute('style') || '';
            wrapper.setAttribute('style', existingStyleClasses + this.wrapperStyle);
        }
        if (this.push && this.push == true) {
            wrapper.classList.add('mr-auto');
        }
        this.processHtmlAccessibilityRole();
        this.htmlElement = wrapper;
        this.contentWrapper = this.component;
    };
    HTMLFormComponent.prototype.innerWrap = function () {
        return this.component;
    };
    HTMLFormComponent.prototype.showToolbox = function () {
        // this.toolbox.classList.remove('d-none');
    };
    HTMLFormComponent.prototype.hideToolbox = function () {
        // this.toolbox.classList.add('d-none');
    };
    HTMLFormComponent.prototype.focusCurrentComponent = function (deferred, options) {
        if (this.designMode) {
            var form = document.getElementById(this.formId);
            var activeComponents = form.querySelectorAll('.designerFocusedElement');
            var isUserAgentIE = this.fh.isIE();
            if (activeComponents.length) {
                if (isUserAgentIE && !NodeList.prototype.forEach) {
                    NodeList.prototype.forEach = function (callback, thisArg) {
                        thisArg = thisArg || window;
                        for (var i = 0; i < this.length; i++) {
                            callback.call(thisArg, this[i], i, this);
                        }
                    };
                }
                activeComponents.forEach(function (element) {
                    element.classList.remove('designerFocusedElement');
                    element.classList.remove('colorBorder');
                });
            }
            if (!this.htmlElement.classList.contains('colorBorder') && options.isLast) {
                if (this.componentObj.type === 'DropdownItem' || this.componentObj.type === 'ThreeDotsMenuItem') {
                    var dropdown = this.component.closest('.fc.dropdown').parentElement;
                    dropdown.classList.add('colorBorder');
                    dropdown.classList.add('designerFocusedElement');
                }
                else {
                    this.htmlElement.classList.add('colorBorder');
                }
                this.animateScroll(options);
            }
            if (!this.htmlElement.classList.contains('designerFocusedElement') || !this.htmlElement.classList.contains('colorBorder')) {
                this.htmlElement.classList.add('designerFocusedElement');
                this.htmlElement.classList.add('colorBorder');
            }
            this.highlightDesignerElementTree();
        }
        else {
            if (this.focusableComponent != null && this.focusableComponent.focus) {
                this.focusableComponent.focus();
            }
            else {
                if (options.isLast) {
                    this.animateScroll(options);
                }
            }
        }
        deferred.resolve(options);
        return deferred.promise();
    };
    HTMLFormComponent.prototype.setRequiredField = function (isRequired) {
        if (isRequired) {
            if (this.requiredElement !== null) {
                return;
            }
            if (this.componentObj.type === 'RadioOption' || this.componentObj.type === 'RadioOptionsGroup' || this.componentObj.type === 'CheckBox') {
                var divRequired = document.createElement('div');
                divRequired.classList.add('requiredFieldWrapper');
                var spanRequired = document.createElement('span');
                spanRequired.classList.add('requiredField');
                var iconRequired = document.createElement('i');
                iconRequired.classList.add('fas');
                iconRequired.classList.add('fa-star-of-life');
                spanRequired.appendChild(iconRequired);
                divRequired.appendChild(spanRequired);
                this.requiredElement = divRequired;
                var label = this.htmlElement.firstChild;
                var controlLabelWithText = label.innerText.length;
                if (controlLabelWithText) {
                    label.appendChild(this.requiredElement);
                }
                else {
                    this.htmlElement.appendChild(this.requiredElement);
                }
            }
            else {
                var iconRequired = document.createElement('i');
                iconRequired.classList.add('fas');
                iconRequired.classList.add('fa-star-of-life');
                iconRequired.style.fontSize = '0.5em';
                var spanRequired = document.createElement('span');
                spanRequired.classList.add('input-group-text');
                spanRequired.classList.add('input-required');
                spanRequired.appendChild(iconRequired);
                var divRequired = document.createElement('div');
                divRequired.classList.add('input-group-append');
                divRequired.appendChild(spanRequired);
                this.requiredElement = divRequired;
                if (this.inputGroupElement != null) {
                    this.inputGroupElement.appendChild(this.requiredElement);
                }
                else if (this.component.classList.contains('field-required')) {
                    this.component.appendChild(this.requiredElement);
                }
                else {
                    this.htmlElement.appendChild(this.requiredElement);
                }
            }
        }
        else {
            if (this.requiredElement === null) {
                return;
            }
            if (this.component.classList.contains('field-required')) {
                if (this.component.contains(this.requiredElement)) {
                    this.component.removeChild(this.requiredElement);
                }
                if (this.inputGroupElement.contains(this.requiredElement)) {
                    this.inputGroupElement.removeChild(this.requiredElement);
                }
            }
            else if (this.htmlElement.requiredElement) {
                this.htmlElement.removeChild(this.requiredElement);
            }
            this.requiredElement = null;
        }
    };
    HTMLFormComponent.prototype.extractChangedAttributes = function () {
        if (this.changesQueue) {
            return this.changesQueue.extractChangedAttributes();
        }
    };
    HTMLFormComponent.prototype.__ = function (string, node, args) {
        if (node === void 0) { node = undefined; }
        if (args === void 0) { args = undefined; }
        // in case when node is arg is ommited
        if ($.isArray(node)) {
            args = node;
            node = null;
        }
        if (!node) {
            node = document.createElement('span');
            node.classList.add('translation');
        }
        $(node).text(this.i18n.__(string, args, this.language));
        this.translationItems.push({
            element: node,
            string: string,
            args: args
        });
        return node;
    };
    HTMLFormComponent.prototype.changeLanguage = function (code) {
        for (var i = 0; i < this.translationItems.length; i++) {
            var item = this.translationItems[i];
            $(item.element).text(this.i18n.__(item.string, item.args, code));
        }
    };
    HTMLFormComponent.prototype.getDefaultWidth = function () {
        return 'md-12';
    };
    HTMLFormComponent.prototype.getAdditionalButtons = function () {
        return [];
    };
    HTMLFormComponent.prototype.animateScroll = function (options) {
        options.scrollableElement = options.scrollableElement || 'html, body';
        if (this.component.localName === 'th') {
            var parentTable = this.component.closest('table').id;
            $(options.scrollableElement).animate({
                scrollTop: $('#' + parentTable).offset().top - 160
            });
        }
        else if (this.type === 'IMapLite') {
            var row = this.component.closest('.row');
            $(options.scrollableElement).animate({
                scrollTop: $(row).offset().top - 160
            });
        }
        else if (this.componentObj.type === 'DropdownItem' || this.componentObj.type === 'ThreeDotsMenuItem') {
            var dropdown = this.component.closest('.fc.dropdown');
            $(options.scrollableElement).animate({
                scrollTop: $(dropdown).offset().top - 160
            });
        }
        else {
            if ($('#' + this.id).length > 0) {
                $(options.scrollableElement).animate({
                    scrollTop: $('#' + this.id).offset().top - 160
                });
            }
        }
    };
    HTMLFormComponent.prototype.highlightDesignerElementTree = function () {
        // check if elementTree already contains highlighted elements
        // if yes, clear highlights -> we only want one at a time
        var designerElementTree = document.getElementById('designerElementTree');
        var highlightedElements = designerElementTree.querySelectorAll('.toolboxElementHighlight');
        if (highlightedElements.length) {
            highlightedElements.forEach(function (element) {
                element.classList.remove('toolboxElementHighlight');
            });
        }
        // check if formToolboxTools accordion is expanded and if so, collapse its elements first
        var toolboxElements = document.getElementById('toolboxFormTools');
        var toolboxBtns = toolboxElements.querySelectorAll('.collapseBtn');
        if (toolboxBtns) {
            toolboxBtns.forEach(function (button) {
                if (!button.classList.contains('collapsed')) {
                    button.setAttribute('aria-expanded', 'false');
                    button.classList.add('collapsed');
                }
            });
        }
        // check if designerElementTree is collapsed and if so, expand it first
        var collapseBtn = designerElementTree.querySelector('.btn');
        if (collapseBtn && collapseBtn.classList.contains('collapsed')) {
            collapseBtn.setAttribute('aria-expanded', 'true');
            collapseBtn.classList.remove('collapsed');
            designerElementTree.querySelector('.collapse').classList.add('show');
        }
        // verify event source and set elementTreeEquivalent accordingly
        var focusEventData = this.formsManager.firedEventData;
        var sourceElement = focusEventData.eventSourceId;
        var elementTreeEquivalent;
        if (focusEventData.containerId === 'formDesignerToolbox') {
            elementTreeEquivalent = document.getElementById(sourceElement);
        }
        else {
            elementTreeEquivalent = designerElementTree.querySelector('li[data-designer_element_equivalent=' + sourceElement + ']');
        }
        if (elementTreeEquivalent !== null) {
            var treeNode = elementTreeEquivalent.querySelector('.treeNodeBody');
            if (treeNode !== null) {
                treeNode.classList.add('toolboxElementHighlight');
                this.updateDesignerElementTree(focusEventData, elementTreeEquivalent);
            }
        }
    };
    HTMLFormComponent.prototype.updateDesignerElementTree = function (focusEventData, elementTreeEquivalent) {
        var treeElementsList = Array.from(document.getElementById('designerElementTree').querySelector('.treeElementList').children);
        var topLevelNode;
        var hiddenNodes;
        var nodeCarets;
        treeElementsList.forEach(function (node) {
            if (node.contains(elementTreeEquivalent)) {
                topLevelNode = node;
            }
        });
        if (topLevelNode) {
            hiddenNodes = topLevelNode.querySelectorAll('ul.d-none');
            nodeCarets = topLevelNode.querySelectorAll('.treeNodeBody');
            // expand only nodes that contain elementTreeEquivalent
            hiddenNodes.forEach(function (node) {
                if (node.children.length && node.contains(elementTreeEquivalent)) {
                    node.classList.remove('d-none');
                }
            });
            // update nodes carets that contain elementTreeEquivalent
            nodeCarets.forEach(function (caret) {
                if (caret.firstChild.classList.contains('fa-caret-right')) {
                    var nodeList = caret.parentElement;
                    if (nodeList && !nodeList.nextElementSibling.classList.contains('d-none')) {
                        caret.firstChild.classList.remove('fa-caret-right');
                        caret.firstChild.classList.add('fa-caret-down');
                    }
                }
            });
        }
    };
    /**
     * Function that handle adding margin and paddings to component styles.
     * @return string
     */
    HTMLFormComponent.prototype.handlemarginAndPAddingStyles = function () {
        if (this.componentObj.marginLeft) {
            this.htmlElement.style.marginLeft = this.componentObj.marginLeft;
        }
        if (this.componentObj.marginRight) {
            this.htmlElement.style.marginRight = this.componentObj.marginRight;
        }
        if (this.componentObj.marginTop) {
            this.htmlElement.style.marginTop = this.componentObj.marginTop;
        }
        if (this.componentObj.marginBottom) {
            this.htmlElement.style.marginBottom = this.componentObj.marginBottom;
        }
        if (this.componentObj.paddingLeft) {
            this.htmlElement.style.paddingLeft = this.componentObj.paddingLeft;
        }
        if (this.componentObj.paddingRight) {
            this.htmlElement.style.paddingRight = this.componentObj.paddingRight;
        }
        if (this.componentObj.paddingTop) {
            this.htmlElement.style.paddingTop = this.componentObj.paddingTop;
        }
        if (this.componentObj.paddingBottom) {
            this.htmlElement.style.paddingBottom = this.componentObj.paddingBottom;
        }
    };
    /**
     * Function process width string from backend serwer and creates proper bootstrap classes string array so they can be added to component.
     * @param width
     */
    HTMLFormComponent.prototype.handleWidth = function (width) {
        if (width === void 0) { width = this.componentObj.width; }
        if (!width) {
            width = this.getDefaultWidth();
        }
        if (width) {
            // Delete unwanted spaces
            width = width.trim();
            //Replace un wanted chars
            width = width.replace(HTMLFormComponent.bootstrapColSeparateCahrsRegexp, ' ');
            //Replace multi spaces with one
            width = width.replace(/\s\s+/g, ' ');
            this.width = width.split(' ');
        }
    };
    /**
     * Logic moved to function so it can be overrided by children classes.
     */
    HTMLFormComponent.prototype.buildDesingerToolbox = function () {
        FhContainer_1.FhContainer.get('Designer').buildToolbox(this.getAdditionalButtons(), this);
    };
    HTMLFormComponent.prototype.handleDeleteBtnEvent = function (event) {
        event.stopImmediatePropagation();
        var keyCode = event.keyCode;
        if (keyCode === 46) { // keyborad delete keycode
            var deleteBtn = document.getElementById('designerDeleteFormElement');
            var focusedElement = document.querySelector('.designerFocusedElement');
            if (!focusedElement) {
                console.error('%cElement to be removed was not found.', 'background: #cc0000; color: #FFF');
                return;
            }
            else {
                /**
                 * We need to check if delete button was pressed on for deleting focusedelement.
                 * To do this we get activeElement (element that has current gloabl focus) and check if element is html input/select.
                 * If it is select or input or textarea and it is not inside current edited form we should prevent delete.
                 */
                var activeElement = document.activeElement;
                var activeElementTagName = activeElement.tagName.toLowerCase();
                if (focusedElement.contains(activeElement) || (!focusedElement.contains(activeElement) &&
                    (activeElementTagName !== 'input' &&
                        activeElementTagName !== 'select' &&
                        activeElementTagName !== 'textarea' &&
                        !activeElement.classList.contains('form-control')))) {
                    deleteBtn.click();
                }
            }
        }
    };
    /**
     * Ads autocomplete attribiute to input element i both exists on element.
     */
    HTMLFormComponent.prototype.processAutocomplete = function (value) {
        if (value === void 0) { value = this.autocomplete; }
        if (this.autocomplete && this.input) {
            this.input.setAttribute('autocomplete', value);
        }
    };
    /**
     * Ads aria-label attribiute to element.
     */
    HTMLFormComponent.prototype.processAriaLabel = function (value) {
        if (value === void 0) { value = this.ariaLabel; }
        //Add attribute only when there is no label on component.
        if (this.ariaLabel && !this.labelElement) {
            if (this.input) {
                this.input.setAttribute('aria-label', value);
            }
            else {
                this.component.setAttribute('aria-label', value);
            }
        }
    };
    /**
     * Ads role attribiute to element.
     */
    HTMLFormComponent.prototype.processHtmlAccessibilityRole = function (value) {
        if (value === void 0) { value = this.htmlAccessibilityRole; }
        if (this.htmlAccessibilityRole) {
            this.component.setAttribute('role', value);
        }
    };
    HTMLFormComponent.bootstrapColRegexp = /^(xs|sm|md|lg|xl)-([1-9]|1[0-2])$/i;
    HTMLFormComponent.bootstrapColWidthRegexp = /^\d+(px|%)$/i;
    HTMLFormComponent.bootstrapColSeparateCahrsRegexp = /(,|;|\|\/|\|)/g;
    __decorate([
        lazyInject('FHML'),
        __metadata("design:type", FHML_1.FHML)
    ], HTMLFormComponent.prototype, "fhml", void 0);
    __decorate([
        lazyInject('I18n'),
        __metadata("design:type", I18n_1.I18n)
    ], HTMLFormComponent.prototype, "i18n", void 0);
    return HTMLFormComponent;
}(FormComponent_1.FormComponent));
exports.HTMLFormComponent = HTMLFormComponent;
//# sourceMappingURL=HTMLFormComponent.js.map