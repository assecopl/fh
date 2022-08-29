import getDecorators from "inversify-inject-decorators";
import {FormComponentChangesQueue} from "./FormComponentChangesQueue";
import {FormsManager} from "../Socket/FormsManager";
import {FH} from "../Socket/FH";
import {Util} from "../Util";
import {Form} from "./Form";
import {FhContainer} from "../FhContainer";

let { lazyInject } = getDecorators(FhContainer);

abstract class FormComponent {
    @lazyInject("FormsManager")
    protected formsManager: FormsManager;

    @lazyInject("FH")
    protected fh: FH;

    @lazyInject("Util")
    protected util: Util;

    @lazyInject("FormComponentChangesQueue")
    protected changesQueue: FormComponentChangesQueue;

    protected static readonly VALUE_ATTRIBUTE_NAME: string = 'value';

    private _id: string;
    protected _formId: string;
    private _parent: FormComponent = null;
    private _componentObj: any;

    protected components: FormComponent[];
    protected nonVisualComponents: FormComponent[];
    protected destroyed: boolean;
    private onDesignerToolboxDrop: any;
    public combinedId: string;
    public contentWrapper: HTMLElement;
    public designMode: boolean;
    public designDeletable: boolean = true;

    constructor(componentObj: any, parent: FormComponent) {
        this._componentObj = componentObj;

        this._id = this.componentObj.id;
        this._parent = parent?parent:null;
        this.designDeletable = componentObj.designDeletable;

        if (this._parent) {
            this._formId = this._parent.formId;
        } else { // FORM
            this._formId = '';
        }

        this.components = [];
        this.nonVisualComponents = [];

        this.destroyed = false;
        this.designMode = this.componentObj.designMode || (this.parent != null && this.parent.designMode);
        if (this.designMode) {
            // in design mode only changesQueue should be used
            this.changesQueue = FhContainer.get('FormComponentChangesQueue');
        }
        this.onDesignerToolboxDrop = this.componentObj.onDesignerToolboxDrop;

    }

    /* Create component */
    protected create() {
        this.createComponents();
    }

    protected createComponents() {
        if (this.componentObj.subelements) {
            this.addComponents(this.componentObj.subelements);
        }
        if (this.componentObj.nonVisualSubcomponents) {
            this.componentObj.nonVisualSubcomponents.forEach(function (componentObj) {
                this.addNonVisualComponent(componentObj);
            }.bind(this));
        }
    };

    destroy(removeFromParent) {
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

    /* Collect changes component's children before sending event to backend */

    protected collectAllChanges() {
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

    /* Collect changes from component */

    protected collectChanges(allChanges) {
        var pendingChangedAttributes;
        if (this.designMode) {
            // in design mode only changesQueue should be used
            pendingChangedAttributes = this.changesQueue.extractChangedAttributes();
        } else {
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

    /* Get pending attributes' changes and forget them. */

    protected extractChangedAttributes() {
        return {};
    };

    /* Fire event to backend */

    public fireEvent(eventType, actionName, params = undefined) {
        this.fireEventImpl(eventType, actionName, false, params);
    };

    /* Fire event to backend and lock application */

    protected fireEventWithLock(eventType, actionName, params = undefined) {
        this.fireEventImpl(eventType, actionName, true, params);
    };

    /* Fire event to backend */

    protected fireEventImpl(eventType, actionName, doLock, params = undefined) {
        if (this.destroyed) {
            return;
        }
        if (this.designMode && !eventType.startsWith('onformedit_')
            && ['onDropComponent', 'onResizeComponent'].indexOf(actionName) === -1) return;

        var deferedEvent = {
            component: this,
            deferred: $.Deferred()
        };

        this.formsManager.eventQueue.push(deferedEvent);
        if (this.formsManager.eventQueue.length == 1) {
            deferedEvent.deferred.resolve();
        }

        var success = this.formsManager.fireEvent(eventType, actionName, this.formId, this.id, deferedEvent, doLock, params);
        if (!success) {
            this.formsManager.eventQueue.pop();
        }
    };

    protected fireHttpMultiPartEvent(eventType, actionName, url, data: FormData) {
        return this.formsManager.fireHttpMultiPartEvent(eventType, actionName, this.formId, this.id, url, data);
    };

    /* Apply changes from backend */

    public applyChange(change) {
        if (this.id === change.formElementId) {
            this.update(change);
        } else {
            this.components.forEach(function (component) {
                component.applyChange(change);
            });
            this.nonVisualComponents.forEach(function (component) {
                component.applyChange(change);
            });
        }
    };

    protected update(change) {
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
                } else {
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

    protected addComponent(componentObj) {
        var component = this.fh.createComponent(componentObj, this);

        this.components.push(component);
        component.create();

        return component;
    };

    protected addNonVisualComponent(componentObj) {
        var component = this.fh.createComponent(componentObj, this);

        this.nonVisualComponents.push(component);
        component.create();
    };

    protected addComponents(componentsList) {
        (componentsList || []).forEach(function (componentObj) {
            this.addComponent(componentObj);
        }.bind(this));
    };

    protected removeComponent(componentId) {
        var componentIndex = this.findComponent(componentId, true);

        if (componentIndex !== false) {
            this.components.splice(componentIndex, 1);
        }
    };

    protected findComponent(componentId, onlyId, getPath: boolean = false, deepSearch: boolean = false) {
        var result = this.findComponentImpl(this.getAllComponents(), componentId, onlyId, getPath, deepSearch);
        if (result === false) {
            result = this.findComponentImpl(this.nonVisualComponents, componentId, onlyId, getPath, deepSearch);
        }
        return result;
    };

    private findComponentImpl(componentList, componentId, onlyId, getPath: boolean = false, deepSearch: boolean = false) {
        var path = [];
        for (var i = componentList.length - 1; i >= 0; i--) {
            var component = componentList[i];

            if (component.id === componentId) {
                if (getPath) {
                    path.unshift(component.id);
                } else {
                    if (onlyId) {
                        return i;
                    }
                    return component;
                }
            } else {
                if (getPath) {
                    var inChild = component.findComponent(componentId, onlyId, getPath, deepSearch);
                    if (inChild) {
                        path = inChild;
                        path.unshift(component.id);
                    }
                } else if (deepSearch) {
                    var childComponent = component.findComponent(componentId, onlyId, getPath,
                        deepSearch);
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

    protected focusComponent(path, index, options) {
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

    protected getAllComponents() {
        return this.components;
    }

    protected focusCurrentComponent(deferred, options) {
        if (options.isLast) {
            deferred.resolve(options);
        }
        return deferred.promise();
    };

    public get id(): string {
        return this._id;
    }

    public get formId(): string {
        return this._formId;
    }

    public set formId(_formId: string) {
        this._formId = _formId;
    }

    public get parent(): FormComponent {
        return this._parent;
    }

    public get componentObj(): any {
        return this._componentObj;
    }

    public get parentId(): string {
        if (this._parent != null) {
            return this._parent.id || this._parent.combinedId;
        } else {
            // Form
            return this._componentObj.container;
        }
    }

    protected processURL(url: string) {
        // process only relative urls
        if (url != null && url != '' && /^(?:[a-z]+:)?\/\//i.test(url) == false) {
            // get form object
            var form: any = this.parent;
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
                } else if (!prefixEndsWithSlash && !urlStartsWithSlash) {
                    url = '/' + url;
                }
                return form.resourcesUrlPrefix + url;
            }
            else {
                return this.util.getPath(url);
            }
        }

        return url;
    }

    /**
     * Returns form that component is display on
     * @returns {any}
     */
    private getForm() : Form {
        let form: any = this;
        while (form.parent != null) {
            form = form.parent;
        }

        return form;
    }

    protected getViewMode(): string {
        return this.getForm().viewMode;
    }

    protected getFormType(): string {
        return this.getForm().formType;
    }

    protected isFormActive(): boolean {
        return this.getForm().state == 'ACTIVE';
    }

    public addAfterInitActions(action: any): void {
        this.getForm().addAfterInitActions(action);
    }

}

export {FormComponent};
