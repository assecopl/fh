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
var inversify_1 = require("inversify");
var inversify_inject_decorators_1 = require("inversify-inject-decorators");
var Cookies = require("js.cookie");
var ShutdownEvent_pl_1 = require("../Events/i18n/ShutdownEvent.pl");
var ShutdownEvent_en_1 = require("../Events/i18n/ShutdownEvent.en");
var FormsManager_pl_1 = require("../I18n/FormsManager.pl");
var FormsManager_en_1 = require("../I18n/FormsManager.en");
var I18n_1 = require("../I18n/I18n");
var ApplicationLock_1 = require("./ApplicationLock");
var SocketHandler_1 = require("./SocketHandler");
var Util_1 = require("../Util");
var NotificationEvent_1 = require("../Events/NotificationEvent");
var SocketOutputCommands_1 = require("./SocketOutputCommands");
var $ = require("jquery");
var FhContainer_1 = require("../FhContainer");
var LayoutHandler_1 = require("../LayoutHandler");
var lazyInject = inversify_inject_decorators_1.default(FhContainer_1.FhContainer).lazyInject;
//TODO Designer logic should be moved to Designer Module.
var FormsManager = /** @class */ (function () {
    function FormsManager() {
        this.duringShutdown = false;
        this.openedForms = [];
        this.usedContainers = {};
        this.eventQueue = [];
        this.pendingRequestsCount = 0;
        this.currentMouseDownElement = null;
        // identyfikator ostatnio aktywnego elementu przy zamykaniu formatki
        this.lastActiveElementId = null;
        this.i18n.registerStrings('pl', FormsManager_pl_1.FormsManagerPL);
        this.i18n.registerStrings('en', FormsManager_en_1.FormsManagerEN);
        this.i18n.registerStrings('pl', ShutdownEvent_pl_1.ShutdownEventPL);
        this.i18n.registerStrings('en', ShutdownEvent_en_1.ShutdownEventEN);
        this.registerBodyMouseEvents();
    }
    FormsManager.prototype.openForm = function (formObj) {
        var form = FhContainer_1.FhContainer.get('Form')(formObj);
        this.openedForms.push(form);
        this.usedContainers[form.containerId] = form;
        this.formObj = formObj;
        if (form !== null && form.id === 'designerToolbox' || form.id === 'designerComponents' || form.id === 'FormPreview') {
            this.buildDesignerContainers();
            this.collapseMenuForm();
        }
        if (form.container !== null && form.container.id === 'formDesignerModelProperties') {
            this.showDesignerModelProperties();
            var mainForm = this.layoutHandler.getLayoutContainer("mainForm");
            // document.getElementById('mainForm');
            var formDesignerContainers = this.layoutHandler.getLayoutContainer("formDesigner");
            // document.getElementById('formDesigner');
            if (!mainForm.contains(formDesignerContainers)) {
                this.buildDesignerContainers();
                this.collapseMenuForm();
            }
        }
        form.create();
        /**
         * Set Designer Behaviour.
         *
         */
        if (form.container.id && form.container.id == "formDesignerComponents") {
            FhContainer_1.FhContainer.get('Designer').setBehaviour(form.componentObj.behaviour);
        }
        if (form.id === 'designerComponents' || (form.container.id == 'designedFormContainer' && form.designMode)) {
            if (formObj.designContainerType && !formObj.modal) {
                this.handleDesignerContainerWidth(formObj.designContainerType, formObj.designContainerWidth);
            }
            else if (formObj.width && formObj.formType != 'MODAL' && formObj.formType != 'MODAL_OVERFLOW') {
                this.handleDesignerContainerWidthPx(formObj.width);
            }
            else {
                this.handleDesignerContainerWidthDefault();
            }
        }
    };
    FormsManager.prototype.openForms = function (formsList) {
        if (!formsList) {
            return;
        }
        formsList.forEach(function (formObj) {
            if (this.usedContainers[formObj.container] && ENV_IS_DEVELOPMENT) {
                console.error('Framework Error: Container "' + formObj.container + '" is used by form "'
                    + this.usedContainers[formObj.container].id + '". It will be overwritten');
            }
            this.openForm(formObj);
            if (this.lastActiveElementId && this.lastClosedFormId === formObj.id) {
                var element = document.getElementById(this.lastActiveElementId);
                if (element) {
                    // skip menu's focus
                    if (document.getElementById('menuForm').contains(element)) {
                        return;
                    }
                    element.focus();
                }
            }
        }.bind(this));
    };
    FormsManager.prototype.closeForm = function (form) {
        this.usedContainers[form.containerId] = null;
        this.openedForms.splice(this.openedForms.indexOf(form), 1);
        if (form !== null && form.id === 'designerToolbox' || form.id === 'designerComponents' || form.id === 'FormPreview') {
            this.removeDesignerContainers();
            this.uncollapseMenuForm();
        }
        if (form.container !== null && form.container.id === 'formDesignerModelProperties') {
            this.hideDesignerModelProperties();
            if (this.openedForms.find(function (form) { return form.id == 'designerComponents'; }) == undefined) {
                this.removeDesignerContainers();
                this.uncollapseMenuForm();
            }
        }
        if (document.activeElement) {
            this.lastActiveElementId = document.activeElement.id || null;
        }
        this.lastClosedFormId = form.id;
        form.destroy();
        form = null;
    };
    FormsManager.prototype.closeForms = function (formsList) {
        if (!formsList) {
            return;
        }
        formsList.forEach(function (formId) {
            var form = this.findForm(formId);
            if (typeof form === 'object') {
                if (form.container.id === 'designedFormContainer' && !localStorage.getItem('formContainerHeight')) {
                    localStorage.setItem('formContainerHeight', form.container.clientHeight);
                }
                this.closeForm(form);
            }
        }.bind(this));
    };
    FormsManager.prototype.findForm = function (formId) {
        for (var i = 0, len = this.openedForms.length; i < len; i++) {
            var form = this.openedForms[i];
            if (form.id === formId) {
                return form;
            }
        }
        return false;
    };
    FormsManager.prototype.handleExternalEvents = function (eventList) {
        if (!eventList) {
            return;
        }
        eventList.forEach(function (eventObj) {
            var type = eventObj.type;
            var event = FhContainer_1.FhContainer.get('Events.' + type);
            if (event && typeof event.fire === 'function') {
                event.fire(eventObj);
            }
        }.bind(this));
    };
    FormsManager.prototype.findFormByContainer = function (containerId) {
        var form = null;
        for (var i = 0; i < this.openedForms.length; i++) {
            if (this.openedForms[i].parentId === containerId) {
                form = this.openedForms[i];
                break;
            }
        }
        return form;
    };
    FormsManager.prototype.focusComponent = function (componentId, containerId) {
        var path = false;
        var form = null;
        if (containerId) {
            form = this.findFormByContainer(containerId);
            if (form) {
                path = form.findComponent(componentId, false, true);
            }
        }
        else {
            for (var i = 0, len = this.openedForms.length; i < len; i++) {
                form = this.openedForms[i];
                path = form.findComponent(componentId, false, true);
                if (path) {
                    break;
                }
            }
        }
        if (form && path) {
            if (form.modalDeferred) {
                form.modalDeferred.promise().then(function () { return form.focusComponent(path, 0); });
            }
            else {
                form.focusComponent(path, 0);
            }
        }
    };
    FormsManager.prototype.applyChanges = function (changesList) {
        if (!changesList) {
            return;
        }
        changesList.forEach(function (change) {
            var form = this.findForm(change.formId);
            if (form) {
                form.applyChange(change);
            }
        }.bind(this));
    };
    FormsManager.prototype.fireEvent = function (eventType, actionName, formId, componentId, deferredEvent, doLock, params) {
        if (params === void 0) { params = undefined; }
        var serviceType = eventType === null && formId == null;
        if (this.pendingRequestsCount > 0) {
            var event_1 = new NotificationEvent_1.NotificationEvent();
            event_1.fire({
                level: 'warning',
                message: this.i18n.__("request pending")
            });
            return false;
        }
        var form = this.findForm(formId);
        if (!serviceType && form === false && ENV_IS_DEVELOPMENT) {
            console.error('%cForm not found. EventType: %s, formId: %s, componentId: %s', 'background: #cc0000; color: #FFF', eventType, formId, componentId);
            alert('Form not found. See console for more information.');
            return false;
        }
        if (!serviceType && form.container === undefined && ENV_IS_DEVELOPMENT) {
            console.error('%cForm container not found. EventType: %s, formId: %s, componentId: %s, form: %o', 'background: #cc0000; color: #FFF', eventType, formId, componentId, form);
            alert('Form container not found. See console for more information.');
            return false;
        }
        var containerId = null;
        if (!serviceType && form.container) {
            containerId = form.container.id;
        }
        var eventData = {
            containerId: containerId,
            formId: formId,
            eventSourceId: componentId,
            eventType: eventType,
            actionName: actionName,
            changedFields: [],
            params: params === undefined ? [] : params
        };
        this.firedEventData = eventData;
        //Cycle through all the boxes to gather all changes
        this.openedForms.forEach(function (form) {
            var changes = form.collectAllChanges();
            eventData.changedFields = eventData.changedFields.concat(changes);
        });
        if (ENV_IS_DEVELOPMENT) {
            console.log('%c eventData ', 'background: #F0F; color: #FFF', eventData);
        }
        deferredEvent.deferred.promise().then(function () {
            var currentForm = this.findForm(formId);
            if (!serviceType && (currentForm === false || (!deferredEvent.component.designMode && deferredEvent.component.destroyed) ||
                (document.getElementById(componentId) == null && currentForm.findComponent(componentId, true, false, true) === false))) { // some components are not available in HTML (RuleDiagram) and some in findComponent (Table row components)
                console.error('Component ' + componentId + ' on form ' + formId + ' is not available any more. Not sending event from this component to server.');
                this.triggerQueue();
            }
            else {
                var requestId = this.socketHandler.activeConnector.run(SocketOutputCommands_1.SocketOutputCommands.HANDLE_EVENT, eventData, function (requestId, data) {
                    this.handleEvent(requestId, data);
                }.bind(this));
                if (doLock) {
                    this.applicationLock.enable(requestId);
                }
            }
        }.bind(this));
        return true;
    };
    FormsManager.prototype.fireHttpMultiPartEvent = function (eventType, actionName, formId, componentId, url, data) {
        // let eventData = {
        //     formId: formId,
        //     eventSourceId: componentId,
        //     eventType: eventType,
        //     changedFields: []
        // };
        //
        // //Cycle through all the boxes to gather all changes
        // this.openedForms.forEach(function(form) {
        //     let changes = form.collectAllChanges();
        //     eventData.changedFields = eventData.changedFields.concat(changes);
        // });
        //
        // data.append('eventData', JSON.stringify(eventData));
        var token = Cookies.get('XSRF-TOKEN');
        var deferred = $.Deferred();
        var progress = $('#' + componentId).find('.progress-bar');
        progress.parent().get(0).classList.remove('d-none');
        progress.width(0).show(0);
        var requestHandle = $.ajax({
            url: url,
            data: data,
            cache: false,
            contentType: false,
            processData: false,
            type: 'POST',
            crossOrigin: true,
            crossDomain: true,
            xhrFields: {
                withCredentials: true
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader('X-CSRF-TOKEN', token);
            },
            success: function (data) {
                this.pendingRequestsCount--;
                deferred.resolve(data);
            }.bind(this),
            error: function (request, statusTxt) {
                this.pendingRequestsCount--;
                console.error('%c Error during sending request, status is: ', 'background: #F00; color: #FFF', request.status);
                progress.parent().get(0).classList.add('d-none');
                progress.hide(0).width(0);
                var status = request.status;
                if (status == 0 && statusTxt == 'abort') {
                    status = -1;
                }
                deferred.rejectWith(this, [status]);
            }.bind(this),
            xhr: function () {
                var xhr = $.ajaxSettings.xhr();
                xhr.upload.onprogress = function (evt) {
                    progress.width(evt.loaded / evt.total * 100 + '%');
                };
                xhr.upload.onload = function () {
                };
                return xhr;
            }
        });
        var handle = {
            abortRequest: function () {
                requestHandle.abort();
            },
            promise: deferred.promise()
        };
        this.pendingRequestsCount++;
        return handle;
    };
    FormsManager.prototype.handleEvent = function (requestId, resultOrArray) {
        // converts either an array or a single object to an array
        var resultArray = [].concat(resultOrArray);
        // merge result
        var mergedResult;
        if (resultArray.length == 1) {
            // no merge needed
            mergedResult = resultArray[0];
        }
        else {
            // merge all responses into one
            //FIXME This part is probably unused. I didn't find code on server side that would make array response.
            mergedResult = {
                closeForm: [],
                openForm: [],
                changes: [],
                errors: [],
                events: [],
                layout: ""
            };
            for (var _i = 0, resultArray_1 = resultArray; _i < resultArray_1.length; _i++) {
                var singleResult = resultArray_1[_i];
                if (singleResult.closeForm)
                    mergedResult.closeForm = mergedResult.closeForm.concat(singleResult.closeForm);
                if (singleResult.openForm)
                    mergedResult.openForm = mergedResult.openForm.concat(singleResult.openForm);
                if (singleResult.changes)
                    mergedResult.changes = mergedResult.changes.concat(singleResult.changes);
                if (singleResult.errors)
                    mergedResult.errors = mergedResult.errors.concat(singleResult.errors);
                if (singleResult.events)
                    mergedResult.events = mergedResult.events.concat(singleResult.events);
                /**
                 * TODO It can couse problems on pages with multi response, do not know where i can check it. Layout may get wrong type on such pages.
                 */
                if (singleResult.layout)
                    mergedResult.layout = singleResult.layout;
            }
        }
        if (mergedResult.errors && mergedResult.errors.length) {
            this.applicationLock.createErrorDialog(mergedResult.errors);
        }
        this.layoutHandler.startLayoutProcessing(mergedResult.layout);
        this.layoutHandler.finishLayoutProcessing();
        this.closeForms(mergedResult.closeForm);
        this.openForms(mergedResult.openForm);
        this.applyChanges(mergedResult.changes);
        this.handleExternalEvents(mergedResult.events);
        if (requestId) {
            this.applicationLock.disable(requestId);
        }
        if (this.openedForms.length > 0 && this.openedForms[this.openedForms.length - 1].formType === 'MODAL') {
            $('body').addClass('modal-open');
        }
        // clear awaiting to be clicked element after application lock is really disabled
        if (!this.applicationLock.isActive()) {
            this.currentMouseDownElement = null;
        }
        if (ENV_IS_DEVELOPMENT) {
            console.log("Finished request processing");
        }
        this.triggerQueue();
    };
    FormsManager.prototype.triggerQueue = function () {
        this.eventQueue.splice(0, 1);
        if (this.eventQueue.length) {
            this.eventQueue[0].deferred.resolve();
        }
    };
    FormsManager.prototype.getLocationHash = function () {
        if (location.hash == '#') { // IE workaround
            return '';
        }
        else {
            return location.hash;
        }
    };
    // There is a problem when clicking on a button (with onClick) while standing on an input (with onChange).
    // The input fires onChange on blur, application lock is shown and the button doesn't get the onClick event at all.
    // We remeber last mouse downed element and fire click event when mouse button is released while showing application lock.
    FormsManager.prototype.registerBodyMouseEvents = function () {
        $('body').on('mouseup', function (event) {
            this.onBodyMouseUp(event);
        }.bind(this));
        $('body').on('mousedown', function (event) {
            this.onBodyMouseDown(event);
        }.bind(this));
    };
    ;
    FormsManager.prototype.informElementMouseDown = function (element) {
        this.currentMouseDownElement = element;
    };
    FormsManager.prototype.onBodyMouseUp = function (event) {
        if (this.applicationLock.isActive() && this.currentMouseDownElement != null) {
            if (ENV_IS_DEVELOPMENT) {
                console.log('%cFiring \'click\' event on ' + this.currentMouseDownElement + ' as mouse button was released on application lock.', 'background: #FAA');
            }
            var evt = document.createEvent('Events');
            evt.initEvent('click', true, true);
            this.currentMouseDownElement.dispatchEvent(evt);
        }
        this.currentMouseDownElement = null;
    };
    FormsManager.prototype.onBodyMouseDown = function (event) {
        if (!this.applicationLock.isActive()) {
            this.informElementMouseDown(event.target);
        }
    };
    FormsManager.prototype.ensureFunctionalityUnavailableDuringShutdown = function () {
        if (this.duringShutdown) {
            this.util.showDialog(this.i18n.__('functionality.title'), this.i18n.__('functionality.message'), this.i18n.__('functionality.button'), 'btn-secondary', null);
        }
        return !this.duringShutdown;
    };
    FormsManager.prototype.buildDesignerContainers = function () {
        var mainForm = this.layoutHandler.getLayoutContainer("mainForm");
        var formDesignerContainers = this.layoutHandler.getLayoutContainer("formDesigner");
        var formsContainer = mainForm.parentElement;
        if (mainForm !== undefined && mainForm.children.length > 0) {
            if (mainForm.children[0].id !== 'formDesigner') {
                mainForm.children[0].classList.add('d-none');
            }
        }
        mainForm.classList.remove('col-sm-12');
        mainForm.classList.remove('col-md-9');
        mainForm.classList.remove('col-lg-9');
        mainForm.classList.remove('col-xl-10');
        if (mainForm.contains(formDesignerContainers)) {
            return;
        }
        else {
            formsContainer.removeChild(formDesignerContainers);
            mainForm.appendChild(formDesignerContainers);
        }
        if (formDesignerContainers.classList.contains('d-none')) {
            formDesignerContainers.classList.remove('d-none');
        }
    };
    FormsManager.prototype.removeDesignerContainers = function () {
        var mainForm = this.layoutHandler.getLayoutContainer("mainForm");
        var menuForm = this.layoutHandler.getLayoutContainer("menuForm");
        var menuFormInner = document.getElementById('menuFormInner');
        var toolboxForm = this.layoutHandler.getLayoutContainer("formDesignerToolbox");
        var formDesignerContainers = this.layoutHandler.getLayoutContainer("formDesigner");
        var formDesignerComponents = this.layoutHandler.getLayoutContainer("formDesignerComponents");
        var formsContainer = mainForm.parentElement;
        if (!mainForm.contains(formDesignerContainers)) {
            return;
        }
        else {
            if (toolboxForm.contains(menuFormInner)) {
                toolboxForm.removeChild(menuFormInner);
                menuForm.appendChild(menuFormInner);
            }
            if (mainForm.children.length > 0) {
                mainForm.children[0].classList.remove('d-none');
                mainForm.removeChild(formDesignerContainers);
            }
            //FIXME Generuje porblemy przy przłączaniu layout-u.
            mainForm.classList.add('col-sm-12');
            mainForm.classList.add('col-md-9');
            mainForm.classList.add('col-lg-9');
            mainForm.classList.add('col-xl-10');
            formDesignerContainers.classList.add('d-none');
            formsContainer.appendChild(formDesignerContainers);
        }
    };
    FormsManager.prototype.handleDesignerContainerWidth = function (dContainerType, width) {
        if (width === void 0) { width = null; }
        //Set parametr designer cintainer type for designer object
        FhContainer_1.FhContainer.get('Designer').setDesignContainerType(dContainerType);
        var formDesignerComponents = this.layoutHandler.getLayoutContainer("designedFormContainer", true);
        if (formDesignerComponents.length > 0) {
            formDesignerComponents.removeClass(function (index, className) {
                return (className.match(/(^|\s)fh-dc-\S+/g) || []).join(' ');
            });
            if (width) {
                formDesignerComponents.css('width', width);
            }
            else {
                formDesignerComponents.css('width', "");
                formDesignerComponents.addClass('fh-dc-' + dContainerType.toLocaleLowerCase());
            }
        }
    };
    FormsManager.prototype.handleDesignerContainerWidthPx = function (width) {
        var formDesignerComponents = this.layoutHandler.getLayoutContainer("designedFormContainer", true);
        if (formDesignerComponents.length > 0) {
            formDesignerComponents.removeClass(function (index, className) {
                return (className.match(/(^|\s)fh-dc-\S+/g) || []).join(' ');
            });
            formDesignerComponents.css('width', width);
        }
    };
    FormsManager.prototype.handleDesignerContainerWidthDefault = function () {
        var currentDesigner = FhContainer_1.FhContainer.get('Designer').designContainerType;
        var formDesignerComponents = this.layoutHandler.getLayoutContainer("designedFormContainer", true);
        this.handleDesignerContainerWidth(currentDesigner);
    };
    FormsManager.prototype.showDesignerModelProperties = function () {
        var formDesignerModelProperties = this.layoutHandler.getLayoutContainer("formDesignerModelProperties");
        var formDesignerComponents = this.layoutHandler.getLayoutContainer("formDesignerComponents");
        var toolbox = this.layoutHandler.getLayoutContainer("formDesignerToolbox");
        var properties = this.layoutHandler.getLayoutContainer("formDesignerProperties");
        if (toolbox !== null && properties !== null) {
            [toolbox, properties, formDesignerComponents].forEach(function (element) {
                element.classList.add('d-none');
            });
        }
        formDesignerModelProperties.classList.remove('d-none');
    };
    FormsManager.prototype.hideDesignerModelProperties = function () {
        var formDesignerModelProperties = this.layoutHandler.getLayoutContainer("formDesignerModelProperties");
        var formDesignerComponents = this.layoutHandler.getLayoutContainer("formDesignerComponents");
        var toolbox = this.layoutHandler.getLayoutContainer("formDesignerToolbox");
        var properties = this.layoutHandler.getLayoutContainer("formDesignerProperties");
        formDesignerModelProperties.classList.add('d-none');
        if (toolbox !== null && properties !== null) {
            [toolbox, properties, formDesignerComponents].forEach(function (element) {
                element.classList.remove('d-none');
            });
        }
    };
    FormsManager.prototype.collapseMenuForm = function () {
        var mainForm = this.layoutHandler.getLayoutContainer("mainForm");
        var menuForm = this.layoutHandler.getLayoutContainer("menuForm");
        var formDesigner = this.layoutHandler.getLayoutContainer("formDesigner").querySelector('.row');
        var formsContainer = mainForm.parentElement;
        var menuIcon = document.getElementById('menuToggler');
        var formDesignerToolbox = document.getElementById('formDesignerToolbox');
        if (formsContainer.contains(menuIcon) || menuForm.parentElement.contains(menuIcon)) {
            return;
        }
        else {
            menuForm.classList.add('d-none');
            var menuToggler = document.createElement('div');
            menuToggler.id = 'menuToggler';
            var menuTogglerIcon_1 = document.createElement('span');
            ['fas', 'fa-caret-right', 'menuExpandRight'].forEach(function (cssStyle) {
                menuTogglerIcon_1.classList.add(cssStyle);
            });
            menuTogglerIcon_1.id = 'menuTogglerIcon';
            menuToggler.appendChild(menuTogglerIcon_1);
            if (mainForm.parentElement === menuForm.parentElement) {
                formDesigner.insertBefore(menuToggler, formDesignerToolbox);
            }
            else {
                menuForm.parentElement.insertBefore(menuToggler, menuForm);
            }
            menuToggler.addEventListener('click', this.toggleMenu.bind(this));
        }
    };
    FormsManager.prototype.uncollapseMenuForm = function () {
        var menuForm = this.layoutHandler.getLayoutContainer("menuForm");
        // document.getElementById('menuForm');
        var menuToggler = this.layoutHandler.getLayoutContainer("menuToggler");
        // document.getElementById('menuToggler');
        var formDesigner = this.layoutHandler.getLayoutContainer("formDesigner").querySelector('.row');
        if (!menuForm.classList.contains('d-none')) {
            return;
        }
        else {
            menuToggler.removeEventListener('click', this.toggleMenu.bind(this));
            if (formDesigner.contains(menuToggler)) {
                formDesigner.removeChild(menuToggler);
            }
            else {
                menuForm.parentElement.removeChild(menuToggler);
            }
            menuForm.classList.remove('d-none');
        }
    };
    FormsManager.prototype.toggleMenu = function () {
        var menuForm = this.layoutHandler.getLayoutContainer("menuForm");
        // document.getElementById('menuForm');
        var toolboxContainer = document.getElementById('formDesignerToolbox');
        // document.getElementById('formDesignerToolbox');
        var toolbox = this.layoutHandler.getLayoutContainer("designerToolbox");
        // document.getElementById('designerToolbox');
        var menuTogglerIcon = document.getElementById('menuTogglerIcon');
        // document.getElementById('menuTogglerIcon');
        var menuFormInner = document.getElementById('menuFormInner');
        // document.getElementById('menuFormInner');
        if (!toolboxContainer.contains(menuFormInner)) {
            menuForm.removeChild(menuFormInner);
            if (toolbox !== null) {
                toolbox.classList.add('d-none');
            }
            menuFormInner.classList.add('hiddenMenuForm');
            toolboxContainer.appendChild(menuFormInner);
            menuTogglerIcon.classList.remove('fa-caret-right');
            menuTogglerIcon.classList.remove('menuExpandRight');
            menuTogglerIcon.classList.add('fa-caret-left');
            menuTogglerIcon.classList.add('menuCollapseLeft');
        }
        else {
            menuFormInner.classList.remove('hiddenMenuForm');
            toolboxContainer.removeChild(menuFormInner);
            menuForm.appendChild(menuFormInner);
            if (toolbox !== null) {
                toolbox.classList.remove('d-none');
            }
            menuTogglerIcon.classList.remove('fa-caret-left');
            menuTogglerIcon.classList.remove('menuCollapseLeft');
            menuTogglerIcon.classList.add('fa-caret-right');
            menuTogglerIcon.classList.add('menuExpandRight');
        }
    };
    __decorate([
        lazyInject('ApplicationLock'),
        __metadata("design:type", ApplicationLock_1.ApplicationLock)
    ], FormsManager.prototype, "applicationLock", void 0);
    __decorate([
        lazyInject('SocketHandler'),
        __metadata("design:type", SocketHandler_1.SocketHandler)
    ], FormsManager.prototype, "socketHandler", void 0);
    __decorate([
        lazyInject('I18n'),
        __metadata("design:type", I18n_1.I18n)
    ], FormsManager.prototype, "i18n", void 0);
    __decorate([
        lazyInject('Util'),
        __metadata("design:type", Util_1.Util)
    ], FormsManager.prototype, "util", void 0);
    __decorate([
        lazyInject('LayoutHandler'),
        __metadata("design:type", LayoutHandler_1.LayoutHandler)
    ], FormsManager.prototype, "layoutHandler", void 0);
    FormsManager = __decorate([
        inversify_1.injectable(),
        __metadata("design:paramtypes", [])
    ], FormsManager);
    return FormsManager;
}());
exports.FormsManager = FormsManager;
//# sourceMappingURL=FormsManager.js.map