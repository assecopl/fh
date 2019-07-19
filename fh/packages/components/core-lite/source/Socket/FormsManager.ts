import {Container, inject, injectable} from "inversify";
import getDecorators from "inversify-inject-decorators";
import * as Cookies from "js.cookie";
import {ShutdownEventPL} from '../Events/i18n/ShutdownEvent.pl';
import {ShutdownEventEN} from '../Events/i18n/ShutdownEvent.en';
import {FormsManagerPL} from '../I18n/FormsManager.pl';
import {FormsManagerEN} from '../I18n/FormsManager.en';
import {I18n} from "../I18n/I18n";
import {ApplicationLock} from "./ApplicationLock";
import {SocketHandler} from "./SocketHandler";
import {Util} from "../Util";
import {NotificationEvent} from "../Events/NotificationEvent";
import {SocketOutputCommands} from "./SocketOutputCommands";
import {BaseEvent} from "../Events/BaseEvent";
import * as $ from 'jquery';

import {FhContainer} from "../FhContainer";
import {LayoutHandler} from "../LayoutHandler";

declare const ENV_IS_DEVELOPMENT: boolean;

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class FormsManager {
    @lazyInject('ApplicationLock')
    private applicationLock: ApplicationLock;
    @lazyInject('SocketHandler')
    private socketHandler: SocketHandler;
    @lazyInject('I18n')
    private i18n: I18n;
    @lazyInject('Util')
    private util: Util;
    @lazyInject('LayoutHandler')
    private layoutHandler: LayoutHandler;

    public duringShutdown: boolean = false;
    private openedForms: any[] = [];
    private usedContainers: any = {};
    public eventQueue: any[] = [];
    private pendingRequestsCount: number = 0;
    private currentMouseDownElement: HTMLElement = null;
    private formObj: any;
    public firedEventData: any;

    constructor() {
        this.i18n.registerStrings('pl', FormsManagerPL);
        this.i18n.registerStrings('en', FormsManagerEN);
        this.i18n.registerStrings('pl', ShutdownEventPL);
        this.i18n.registerStrings('en', ShutdownEventEN);

        this.registerBodyMouseEvents();
    }

    public openForm(formObj) {


        let form = (<any>FhContainer.get('Form'))(formObj);
        this.openedForms.push(form);
        this.usedContainers[form.containerId] = form;
        this.formObj = formObj;

        if (form !== null && form.id === 'designerToolbox' || form.id === 'designerComponents' || form.id === 'FormPreview') {
            this.buildDesignerContainers();
            this.collapseMenuForm();
        }

        if (form.container !== null && form.container.id === 'formDesignerModelProperties') {
            this.showDesignerModelProperties();

            let mainForm = this.layoutHandler.getLayoutContainer( "mainForm");
            // document.getElementById('mainForm');
            let formDesignerContainers = this.layoutHandler.getLayoutContainer( "formDesigner");
            // document.getElementById('formDesigner');
            if (!mainForm.contains(formDesignerContainers)) {
                this.buildDesignerContainers();
                this.collapseMenuForm();
            }
        }

        form.create();

        if (form.id === 'designerComponents' || (form.container.id == 'designedFormContainer' && form.designMode)) {
            if (formObj.designContainerType && !formObj.modal) {
                this.handleDesignerContainerWidth(formObj.designContainerType, formObj.designContainerWidth)
            } else if (formObj.width && formObj.formType != 'MODAL' && formObj.formType != 'MODAL_OVERFLOW' ) {
                this.handleDesignerContainerWidthPx(formObj.width)
            } else {
                this.handleDesignerContainerWidthDefault();
            }
        }
    }

    public openForms(formsList) {
        if (!formsList) {
            return;
        }
        formsList.forEach(function (formObj) {
            if (this.usedContainers[formObj.container] && ENV_IS_DEVELOPMENT) {
                console.error('Framework Error: Container "' + formObj.container + '" is used by form "'
                    + this.usedContainers[formObj.container].id + '". It will be overwritten');
            }
            this.openForm(formObj);
        }.bind(this));
    }

    closeForm(form) {
        this.usedContainers[form.containerId] = null;
        this.openedForms.splice(this.openedForms.indexOf(form), 1);

        if (form !== null && form.id === 'designerToolbox' || form.id === 'designerComponents' || form.id === 'FormPreview') {
            this.removeDesignerContainers();
            this.uncollapseMenuForm();
        }

        if (form.container !== null && form.container.id === 'formDesignerModelProperties') {
            this.hideDesignerModelProperties();

            if (this.openedForms.find(form => form.id == 'designerComponents') == undefined) {
                this.removeDesignerContainers();
                this.uncollapseMenuForm();
            }
        }

        form.destroy();
        form = null;
    }

    closeForms(formsList) {
        if (!formsList) {
            return;
        }
        formsList.forEach(function (formId) {
            let form = this.findForm(formId);
            if (typeof form === 'object') {

                if (form.container.id === 'designedFormContainer' && !localStorage.getItem('formContainerHeight')) {
                    localStorage.setItem('formContainerHeight', form.container.clientHeight);
                }

                this.closeForm(form);
            }
        }.bind(this));
    }

    findForm(formId) {
        for (let i = 0, len = this.openedForms.length; i < len; i++) {
            let form = this.openedForms[i];
            if (form.id === formId) {
                return form;
            }
        }
        return false;
    }

    handleExternalEvents(eventList) {
        if (!eventList) {
            return;
        }
        eventList.forEach(function (eventObj) {
            let type = eventObj.type;
            let event = FhContainer.get<BaseEvent>('Events.' + type);

            if (event && typeof event.fire === 'function') {
                event.fire(eventObj);
            }
        }.bind(this));
    }

    focusComponent(componentId, containerId) {
        let path = false;
        let form = null;
        if (containerId) {
            for (let i = 0; i < this.openedForms.length; i++) {
                if (this.openedForms[i].parentId === containerId) {
                    form = this.openedForms[i];
                    break;
                }
            }
            if (form) {
                path = form.findComponent(componentId, false, true);
            }
        } else {
            for (let i = 0, len = this.openedForms.length; i < len; i++) {
                form = this.openedForms[i];
                path = form.findComponent(componentId, false, true);
                if (path) {
                    break;
                }
            }
        }
        if (form && path) {
            if (form.modalDeferred) {
                form.modalDeferred.promise().then(() => form.focusComponent(path, 0));
            } else {
                form.focusComponent(path, 0);
            }
        }
    }

    applyChanges(changesList) {
        if (!changesList) {
            return;
        }
        changesList.forEach(function (change) {
            let form = this.findForm(change.formId);
            if (form) {
                form.applyChange(change);
            }
        }.bind(this));
    }

    fireEvent(eventType, actionName, formId, componentId, deferredEvent, doLock, params = undefined) {
        let serviceType = eventType === null && formId == null;

        if (this.pendingRequestsCount > 0) {
            let event = new NotificationEvent();
            event.fire({
                level: 'warning',
                message: this.i18n.__("request pending")
            });
            return false;
        }

        let form = this.findForm(formId);
        if (!serviceType && form === false && ENV_IS_DEVELOPMENT) {
            console.error('%cForm not found. EventType: %s, formId: %s, componentId: %s', 'background: #cc0000; color: #FFF', eventType, formId, componentId);
            alert('Form not found. See console form more information.');
            return false;
        }
        if (!serviceType && form.container === undefined && ENV_IS_DEVELOPMENT) {
            console.error('%cForm container not found. EventType: %s, formId: %s, componentId: %s, form: %o', 'background: #cc0000; color: #FFF', eventType, formId, componentId, form);
            alert('Form container not found. See console form more information.');
            return false;
        }

        let containerId = null;
        if (!serviceType && form.container) {
            containerId = form.container.id;
        }

        let eventData = {
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
            let changes = form.collectAllChanges();
            eventData.changedFields = eventData.changedFields.concat(changes);
        });

        if (ENV_IS_DEVELOPMENT) {
            console.log('%c eventData ', 'background: #F0F; color: #FFF', eventData);
        }

        deferredEvent.deferred.promise().then(function () {
            let currentForm = this.findForm(formId);
            if (!serviceType && (currentForm === false || deferredEvent.component.destroyed ||
                (document.getElementById(componentId) == null && currentForm.findComponent(componentId, true, false, true) === false))) { // some components are not available in HTML (RuleDiagram) and some in findComponent (Table row components)
                console.error('Component ' + componentId + ' on form ' + formId + ' is not available any more. Not sending event from this component to server.');
                this.triggerQueue();
            } else {
                let requestId = this.socketHandler.activeConnector.run(
                    SocketOutputCommands.HANDLE_EVENT, eventData,
                    function (requestId, data) {
                        this.handleEvent(requestId, data);
                    }.bind(this));
                if (doLock) {
                    this.applicationLock.enable(requestId);
                }
            }
        }.bind(this));
        return true;
    }

    fireHttpMultiPartEvent(eventType, actionName, formId, componentId, url, data: FormData) {
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
        let token = Cookies.get('XSRF-TOKEN');
        let deferred = $.Deferred();
        let progress = $('#' + componentId).find('.progress-bar');
        progress.parent().get(0).classList.remove('d-none');
        progress.width(0).show(0);
        let requestHandle = $.ajax({
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
                console.error('%c Error during sending request, status is: ',
                    'background: #F00; color: #FFF', request.status);
                progress.parent().get(0).classList.add('d-none');
                progress.hide(0).width(0);
                let status = request.status;
                if (status == 0 && statusTxt == 'abort') {
                    status = -1;
                }
                deferred.rejectWith(this, [status]);
            }.bind(this),
            xhr: function () {
                let xhr = $.ajaxSettings.xhr();
                xhr.upload.onprogress = function (evt) {
                    progress.width(evt.loaded / evt.total * 100 + '%');
                };
                xhr.upload.onload = function () {
                };
                return xhr;
            }
        });

        let handle = {
            abortRequest: function () {
                requestHandle.abort();
            },
            promise: deferred.promise()
        };
        this.pendingRequestsCount++;
        return handle;
    }

    handleEvent(requestId, resultOrArray) {
        // converts either an array or a single object to an array
        let resultArray = [].concat(resultOrArray);

        // merge result
        let mergedResult;

        if (resultArray.length == 1) {
            // no merge needed
            mergedResult = resultArray[0];
        } else {
            // merge all responses into one
            //FIXME This part is probably unused. I didn't find code on server side that would make array response.
            mergedResult = {
                closeForm: [],
                openForm: [],
                changes: [],
                errors: [],
                events: [],
                layout:""
            };
            for (let singleResult of resultArray) {
                if (singleResult.closeForm) mergedResult.closeForm = mergedResult.closeForm.concat(singleResult.closeForm);
                if (singleResult.openForm) mergedResult.openForm = mergedResult.openForm.concat(singleResult.openForm);
                if (singleResult.changes) mergedResult.changes = mergedResult.changes.concat(singleResult.changes);
                if (singleResult.errors) mergedResult.errors = mergedResult.errors.concat(singleResult.errors);
                if (singleResult.events) mergedResult.events = mergedResult.events.concat(singleResult.events);

                /**
                 * TODO It can couse problems on pages with multi response, do not know where i can check it. Layout may get wrong type on such pages.
                 */
                if (singleResult.layout) mergedResult.layout = singleResult.layout;
            }
        }

        if (mergedResult.errors && mergedResult.errors.length) {
            this.applicationLock.createErrorDialog(mergedResult.errors);
        }
        this.layoutHandler.startLayoutProcessing(mergedResult.layout);
        this.closeForms(mergedResult.closeForm);
        this.openForms(mergedResult.openForm);
        this.applyChanges(mergedResult.changes);
        this.handleExternalEvents(mergedResult.events);
        this.layoutHandler.finishLayoutProcessing();

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

        this.triggerQueue();
    }

    triggerQueue() {
        this.eventQueue.splice(0, 1);

        if (this.eventQueue.length) {
            this.eventQueue[0].deferred.resolve();
        }
    }

    public getLocationHash() {
        if (location.hash == '#') { // IE workaround
            return '';
        } else {
            return location.hash;
        }
    }

    // There is a problem when clicking on a button (with onClick) while standing on an input (with onChange).
    // The input fires onChange on blur, application lock is shown and the button doesn't get the onClick event at all.
    // We remeber last mouse downed element and fire click event when mouse button is released while showing application lock.
    registerBodyMouseEvents() {
        $('body').on('mouseup', function (event) {
            this.onBodyMouseUp(event);
        }.bind(this));
        $('body').on('mousedown', function (event) {
            this.onBodyMouseDown(event);
        }.bind(this));
    };

    informElementMouseDown(element) {
        this.currentMouseDownElement = element;
    }

    onBodyMouseUp(event) {
        if (this.applicationLock.isActive() && this.currentMouseDownElement != null) {
            if (ENV_IS_DEVELOPMENT) {
                console.log('%cFiring \'click\' event on ' + this.currentMouseDownElement + ' as mouse button was released on application lock.', 'background: #FAA');
            }

            let evt = document.createEvent('Events');
            evt.initEvent('click', true, true);
            this.currentMouseDownElement.dispatchEvent(evt);
        }
        this.currentMouseDownElement = null;
    }

    onBodyMouseDown(event) {
        if (!this.applicationLock.isActive()) {
            this.informElementMouseDown(event.target);
        }
    }

    ensureFunctionalityUnavailableDuringShutdown() {
        if (this.duringShutdown) {
            this.util.showDialog(
                this.i18n.__('functionality.title'),
                this.i18n.__('functionality.message'),
                this.i18n.__('functionality.button'),
                'btn-secondary',
                null
            );
        }
        return !this.duringShutdown;
    }

    buildDesignerContainers() {
        let mainForm = this.layoutHandler.getLayoutContainer( "mainForm");
        let formDesignerContainers = this.layoutHandler.getLayoutContainer( "formDesigner");
        let formsContainer = mainForm.parentElement;

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
        } else {
            formsContainer.removeChild(formDesignerContainers);
            mainForm.appendChild(formDesignerContainers);
        }

        if (formDesignerContainers.classList.contains('d-none')) {
            formDesignerContainers.classList.remove('d-none');
        }
    }

    removeDesignerContainers() {
        let mainForm = this.layoutHandler.getLayoutContainer( "mainForm");
        let menuForm = this.layoutHandler.getLayoutContainer( "menuForm");
        let menuFormInner = document.getElementById('menuFormInner');
        let toolboxForm = this.layoutHandler.getLayoutContainer( "formDesignerToolbox");
        let formDesignerContainers = this.layoutHandler.getLayoutContainer( "formDesigner");

        let formDesignerComponents = this.layoutHandler.getLayoutContainer( "formDesignerComponents");
        let formsContainer = mainForm.parentElement;

        if (!mainForm.contains(formDesignerContainers)) {
            return;
        } else {
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
    }

    public handleDesignerContainerWidth(dContainerType:string,width:string = null):void{
        //Set parametr designer cintainer type for designer object
        (<any>FhContainer.get('Designer')).setDesignContainerType(dContainerType);

        let formDesignerComponents = this.layoutHandler.getLayoutContainer( "designedFormContainer", true);

        if(formDesignerComponents.length > 0) {
            formDesignerComponents.removeClass(function (index, className) {
                return (className.match(/(^|\s)fh-dc-\S+/g) || []).join(' ');
            });
            if(width){
                formDesignerComponents.css('width', width);
            } else {
                formDesignerComponents.css('width', "");
                formDesignerComponents.addClass('fh-dc-' + dContainerType.toLocaleLowerCase());
            }
        }
    }

    private handleDesignerContainerWidthPx(width:string){
        let formDesignerComponents = this.layoutHandler.getLayoutContainer( "designedFormContainer", true);

        if(formDesignerComponents.length > 0) {
            formDesignerComponents.removeClass(function (index, className) {
                return (className.match(/(^|\s)fh-dc-\S+/g) || []).join(' ');
            });
                formDesignerComponents.css('width', width);
        }
    }

    private handleDesignerContainerWidthDefault(){
        let currentDesigner = (<any>FhContainer.get('Designer')).designContainerType;
        let formDesignerComponents = this.layoutHandler.getLayoutContainer( "designedFormContainer", true);
       this.handleDesignerContainerWidth(currentDesigner);
    }


    showDesignerModelProperties() {
        let formDesignerModelProperties = this.layoutHandler.getLayoutContainer( "formDesignerModelProperties");
        // document.getElementById('formDesignerModelProperties');
        let formDesignerComponents = this.layoutHandler.getLayoutContainer( "formDesignerComponents");
        // document.getElementById('formDesignerComponents');
        let toolbox = this.layoutHandler.getLayoutContainer( "designerToolbox");
        // document.getElementById('designerToolbox');
        let properties = this.layoutHandler.getLayoutContainer( "designerProperties");
        // document.getElementById('designerProperties');

        if (toolbox !== null && properties !== null) {
            [toolbox, properties].forEach(function (element) {
                element.classList.add('non-visible');
            });
        }

        formDesignerComponents.classList.add('d-none');
        formDesignerModelProperties.classList.remove('d-none');
    }

    hideDesignerModelProperties() {
        let formDesignerModelProperties = this.layoutHandler.getLayoutContainer( "formDesignerModelProperties");
        // document.getElementById('formDesignerModelProperties');
        let formDesignerComponents = this.layoutHandler.getLayoutContainer( "formDesignerComponents");
        // document.getElementById('formDesignerComponents');
        let toolbox = this.layoutHandler.getLayoutContainer( "designerToolbox");
        // document.getElementById('designerToolbox');
        let properties = this.layoutHandler.getLayoutContainer( "designerProperties");
        //document.getElementById('designerProperties');

        formDesignerModelProperties.classList.add('d-none');

        if (toolbox !== null && properties !== null) {
            [toolbox, properties].forEach(function (element) {
                element.classList.remove('non-visible');
            });
        }

        formDesignerComponents.classList.remove('d-none');
    }

    collapseMenuForm() {
        let mainForm = this.layoutHandler.getLayoutContainer( "mainForm");
        // document.getElementById('mainForm');
        let menuForm = this.layoutHandler.getLayoutContainer( "menuForm");
        // document.getElementById('menuForm');
        let formDesigner = this.layoutHandler.getLayoutContainer( "formDesigner").querySelector('.row');
        let formsContainer = mainForm.parentElement;
        let menuIcon = document.getElementById('menuToggler');
        let formDesignerToolbox = document.getElementById('formDesignerToolbox');

        if (formsContainer.contains(menuIcon) || menuForm.parentElement.contains(menuIcon)) {
            return;
        } else {
            menuForm.classList.add('d-none');
            let menuToggler = document.createElement('div');
            menuToggler.id = 'menuToggler';

            let menuTogglerIcon = document.createElement('span');
            ['fas', 'fa-caret-right', 'menuExpandRight'].forEach(function (cssStyle) {
                menuTogglerIcon.classList.add(cssStyle);
            });
            menuTogglerIcon.id = 'menuTogglerIcon';
            menuToggler.appendChild(menuTogglerIcon);

            if (mainForm.parentElement === menuForm.parentElement) {
                formDesigner.insertBefore(menuToggler, formDesignerToolbox);
            } else {
                menuForm.parentElement.insertBefore(menuToggler, menuForm);
            }

            menuToggler.addEventListener('click', this.toggleMenu.bind(this));
        }
    }

    uncollapseMenuForm() {
        let menuForm = this.layoutHandler.getLayoutContainer( "menuForm");
        // document.getElementById('menuForm');
        let menuToggler = this.layoutHandler.getLayoutContainer( "menuToggler");
        // document.getElementById('menuToggler');
        let formDesigner = this.layoutHandler.getLayoutContainer( "formDesigner").querySelector('.row');

        if (!menuForm.classList.contains('d-none')) {
            return;
        } else {
            menuToggler.removeEventListener('click', this.toggleMenu.bind(this));
            if (formDesigner.contains(menuToggler)) {
                formDesigner.removeChild(menuToggler);
            } else {
                menuForm.parentElement.removeChild(menuToggler);
            }
            menuForm.classList.remove('d-none');
        }
    }

    toggleMenu() {
        let menuForm = this.layoutHandler.getLayoutContainer( "menuForm");
        // document.getElementById('menuForm');
        let toolboxContainer = document.getElementById('formDesignerToolbox');
        // document.getElementById('formDesignerToolbox');
        let toolbox = this.layoutHandler.getLayoutContainer( "designerToolbox");
        // document.getElementById('designerToolbox');
        let menuTogglerIcon = document.getElementById('menuTogglerIcon');
        // document.getElementById('menuTogglerIcon');
        let menuFormInner = document.getElementById('menuFormInner');
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
        } else {
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
    }
}

export {FormsManager};
