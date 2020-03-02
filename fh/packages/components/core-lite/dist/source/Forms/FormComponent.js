"use strict";
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
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var FormComponentChangesQueue_1 = require("./FormComponentChangesQueue");
var FormsManager_1 = require("../Socket/FormsManager");
var FH_1 = require("../Socket/FH");
var Util_1 = require("../Util");
var FhContainer_1 = require("../FhContainer");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
var FormComponent = /** @class */ (function () {
    function FormComponent(componentObj, parent) {
        this._parent = null;
        this.designDeletable = true;
        this._componentObj = componentObj;
        this._id = this.componentObj.id;
        this._parent = parent ? parent : null;
        this.designDeletable = componentObj.designDeletable;
        if (this._parent) {
            this._formId = this._parent.formId;
        }
        else { // FORM
            this._formId = '';
        }
        this.components = [];
        this.nonVisualComponents = [];
        this.destroyed = false;
        this.designMode = this.componentObj.designMode || (this.parent != null && this.parent.designMode);
        if (this.designMode) {
            // in design mode only changesQueue should be used
            this.changesQueue = FhContainer_1.FhContainer.get('FormComponentChangesQueue');
        }
        this.onDesignerToolboxDrop = this.componentObj.onDesignerToolboxDrop;
    }
    /* Create component */
    FormComponent.prototype.create = function () {
        this.createComponents();
    };
    FormComponent.prototype.createComponents = function () {
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
        if (this.componentObj.nonVisualSubcomponents) {
            this.componentObj.nonVisualSubcomponents.forEach(function (componentObj) {
                this.addNonVisualComponent(componentObj);
            }.bind(this));
        }
    };
    ;
    FormComponent.prototype.destroy = function (removeFromParent) {
        this._parent = null;
        this.contentWrapper = null;
        this.destroyed = true;
        this.components.forEach(function (component) {
            if (component != null) {
                component.destroy(removeFromParent);
            }
        });
        this.nonVisualComponents.forEach(function (component) {
            if (component != null) {
                component.destroy(removeFromParent);
            }
        });
    };
    ;
    /* Collect changes component's children before sending event to backend */
    FormComponent.prototype.collectAllChanges = function () {
        var allChanges = [];
        this.components.forEach(function (component) {
            var changes = component.collectAllChanges();
            allChanges = allChanges.concat(changes);
        }.bind(this));
        this.nonVisualComponents.forEach(function (component) {
            var changes = component.collectAllChanges();
            allChanges = allChanges.concat(changes);
        }.bind(this));
        return this.collectChanges(allChanges);
    };
    ;
    /* Collect changes from component */
    FormComponent.prototype.collectChanges = function (allChanges) {
        var pendingChangedAttributes;
        if (this.designMode) {
            // in design mode only changesQueue should be used
            pendingChangedAttributes = this.changesQueue.extractChangedAttributes();
        }
        else {
            pendingChangedAttributes = this.extractChangedAttributes();
        }
        if (this.util.countProperties(pendingChangedAttributes) > 0) {
            allChanges.push({
                fieldId: this.id,
                changedAttributes: pendingChangedAttributes,
                formId: this.formId
            });
        }
        return allChanges;
    };
    ;
    /* Get pending attributes' changes and forget them. */
    FormComponent.prototype.extractChangedAttributes = function () {
        return {};
    };
    ;
    /* Fire event to backend */
    FormComponent.prototype.fireEvent = function (eventType, actionName) {
        this.fireEventImpl(eventType, actionName, false);
    };
    ;
    /* Fire event to backend and lock application */
    FormComponent.prototype.fireEventWithLock = function (eventType, actionName) {
        this.fireEventImpl(eventType, actionName, true);
    };
    ;
    /* Fire event to backend */
    FormComponent.prototype.fireEventImpl = function (eventType, actionName, doLock) {
        if (this.destroyed) {
            return;
        }
        if (this.designMode && !eventType.startsWith('onformedit_')
            && ['onDropComponent', 'onResizeComponent'].indexOf(actionName) === -1)
            return;
        var deferedEvent = {
            component: this,
            deferred: $.Deferred()
        };
        this.formsManager.eventQueue.push(deferedEvent);
        if (this.formsManager.eventQueue.length == 1) {
            deferedEvent.deferred.resolve();
        }
        var success = this.formsManager.fireEvent(eventType, actionName, this.formId, this.id, deferedEvent, doLock);
        if (!success) {
            this.formsManager.eventQueue.pop();
        }
    };
    ;
    FormComponent.prototype.fireHttpMultiPartEvent = function (eventType, actionName, url, data) {
        return this.formsManager.fireHttpMultiPartEvent(eventType, actionName, this.formId, this.id, url, data);
    };
    ;
    /* Apply changes from backend */
    FormComponent.prototype.applyChange = function (change) {
        if (this.id === change.formElementId) {
            this.update(change);
        }
        else {
            this.components.forEach(function (component) {
                component.applyChange(change);
            });
            this.nonVisualComponents.forEach(function (component) {
                component.applyChange(change);
            });
        }
    };
    ;
    FormComponent.prototype.update = function (change) {
        if (change.removedComponents) {
            change.removedComponents.forEach(function (componentId) {
                var component = this.findComponent(componentId);
                if (component) {
                    this.removeComponent(componentId);
                    component.destroy(true);
                }
            }.bind(this));
        }
        if (change.addedComponents) {
            Object.keys(change.addedComponents).forEach(function (afterId) {
                var afterIndex = this.findComponent(afterId, true);
                var atIndex;
                if (afterIndex === false) {
                    atIndex = 0;
                }
                else {
                    atIndex = afterIndex + 1;
                }
                var componentsAfterId = change.addedComponents[afterId] || [];
                for (var i = 0; i < componentsAfterId.length; i++) {
                    var componentObj = componentsAfterId[i];
                    var component = this.fh.createComponent(componentObj, this);
                    this.components.splice(atIndex + i, 0, component);
                    component.create();
                }
            }.bind(this));
        }
    };
    ;
    FormComponent.prototype.addComponent = function (componentObj) {
        var component = this.fh.createComponent(componentObj, this);
        this.components.push(component);
        component.create();
        return component;
    };
    ;
    FormComponent.prototype.addNonVisualComponent = function (componentObj) {
        var component = this.fh.createComponent(componentObj, this);
        this.nonVisualComponents.push(component);
        component.create();
    };
    ;
    FormComponent.prototype.addComponents = function (componentsList) {
        (componentsList || []).forEach(function (componentObj) {
            this.addComponent(componentObj);
        }.bind(this));
    };
    ;
    FormComponent.prototype.removeComponent = function (componentId) {
        var componentIndex = this.findComponent(componentId, true);
        if (componentIndex !== false) {
            this.components.splice(componentIndex, 1);
        }
    };
    ;
    FormComponent.prototype.findComponent = function (componentId, onlyId, getPath, deepSearch) {
        if (getPath === void 0) { getPath = false; }
        if (deepSearch === void 0) { deepSearch = false; }
        var result = this.findComponentImpl(this.getAllComponents(), componentId, onlyId, getPath, deepSearch);
        if (result === false) {
            result = this.findComponentImpl(this.nonVisualComponents, componentId, onlyId, getPath, deepSearch);
        }
        return result;
    };
    ;
    FormComponent.prototype.findComponentImpl = function (componentList, componentId, onlyId, getPath, deepSearch) {
        if (getPath === void 0) { getPath = false; }
        if (deepSearch === void 0) { deepSearch = false; }
        var path = [];
        for (var i = componentList.length - 1; i >= 0; i--) {
            var component = componentList[i];
            if (component.id === componentId) {
                if (getPath) {
                    path.unshift(component.id);
                }
                else {
                    if (onlyId) {
                        return i;
                    }
                    return component;
                }
            }
            else {
                if (getPath) {
                    var inChild = component.findComponent(componentId, onlyId, getPath, deepSearch);
                    if (inChild) {
                        path = inChild;
                        path.unshift(component.id);
                    }
                }
                else if (deepSearch) {
                    var childComponent = component.findComponent(componentId, onlyId, getPath, deepSearch);
                    if (childComponent !== false) {
                        return childComponent;
                    }
                }
            }
        }
        if (getPath && path.length) {
            return path;
        }
        return false;
    };
    ;
    FormComponent.prototype.focusComponent = function (path, index, options) {
        options = options || {};
        var allComponents = this.getAllComponents();
        var componentId = path[index];
        for (var i = allComponents.length - 1; i >= 0; i--) {
            var component = allComponents[i];
            if (component.id === componentId) {
                var deferred = $.Deferred();
                options.isLast = index === path.length - 1;
                return component.focusCurrentComponent(deferred, options).done(function (options) {
                    if (index != path.length - 1) {
                        component.focusComponent(path, index + 1, options);
                    }
                });
            }
        }
    };
    ;
    FormComponent.prototype.getAllComponents = function () {
        return this.components;
    };
    FormComponent.prototype.focusCurrentComponent = function (deferred, options) {
        if (options.isLast) {
            deferred.resolve(options);
        }
        return deferred.promise();
    };
    ;
    Object.defineProperty(FormComponent.prototype, "id", {
        get: function () {
            return this._id;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FormComponent.prototype, "formId", {
        get: function () {
            return this._formId;
        },
        set: function (_formId) {
            this._formId = _formId;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FormComponent.prototype, "parent", {
        get: function () {
            return this._parent;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FormComponent.prototype, "componentObj", {
        get: function () {
            return this._componentObj;
        },
        enumerable: true,
        configurable: true
    });
    Object.defineProperty(FormComponent.prototype, "parentId", {
        get: function () {
            if (this._parent != null) {
                return this._parent.id || this._parent.combinedId;
            }
            else {
                // Form
                return this._componentObj.container;
            }
        },
        enumerable: true,
        configurable: true
    });
    FormComponent.prototype.processURL = function (url) {
        // process only relative urls
        if (url != null && url != '' && /^(?:[a-z]+:)?\/\//i.test(url) == false) {
            // get form object
            var form = this.parent;
            while (form.parent) {
                form = form.parent;
            }
            // add URL prefix if present
            if (form.resourcesUrlPrefix != null) {
                var urlStartsWithSlash = /^\//.test(url);
                var prefixEndsWithSlash = /\/$/.test(form.resourcesUrlPrefix);
                // avoid producing double slash and no slash at all
                if (prefixEndsWithSlash && urlStartsWithSlash) {
                    url = url.substring(1);
                }
                else if (!prefixEndsWithSlash && !urlStartsWithSlash) {
                    url = '/' + url;
                }
                return form.resourcesUrlPrefix + url;
            }
            else {
                return this.util.getPath(url);
            }
        }
        return url;
    };
    /**
     * Returns form that component is display on
     * @returns {any}
     */
    FormComponent.prototype.getForm = function () {
        var form = this;
        while (form.parent != null) {
            form = form.parent;
        }
        return form;
    };
    FormComponent.prototype.getViewMode = function () {
        return this.getForm().viewMode;
    };
    FormComponent.prototype.getFormType = function () {
        return this.getForm().formType;
    };
    FormComponent.prototype.isFormActive = function () {
        return this.getForm().state == 'ACTIVE';
    };
    FormComponent.VALUE_ATTRIBUTE_NAME = 'value';
    __decorate([
        lazyInject("FormsManager"),
        __metadata("design:type", FormsManager_1.FormsManager)
    ], FormComponent.prototype, "formsManager", void 0);
    __decorate([
        lazyInject("FH"),
        __metadata("design:type", FH_1.FH)
    ], FormComponent.prototype, "fh", void 0);
    __decorate([
        lazyInject("Util"),
        __metadata("design:type", Util_1.Util)
    ], FormComponent.prototype, "util", void 0);
    __decorate([
        lazyInject("FormComponentChangesQueue"),
        __metadata("design:type", FormComponentChangesQueue_1.FormComponentChangesQueue)
    ], FormComponent.prototype, "changesQueue", void 0);
    return FormComponent;
}());
exports.FormComponent = FormComponent;
//# sourceMappingURL=FormComponent.js.map