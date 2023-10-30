import {default as Cookies} from "js-cookie"
import {SocketOutputCommands} from "./SocketOutputCommands";
import * as $ from 'jquery'; //TODO remove Jquery

import {ApplicationLockService} from "../service/application-lock.service";
import {I18nService} from "../service/i18n.service";
import {Injectable, Injector, inject} from "@angular/core";
import {Util} from "../service/Util";
import {SocketHandler} from "./SocketHandler";
import {Subject} from "rxjs";
import {NotificationService} from "../service/Notification";
import {EventsManager} from "../service/events-manager.service";
import {BaseEvent} from "../events/BaseEvent";
import {FormComponent} from "../controls/form/form.component";

// declare const ENV_IS_DEVELOPMENT: boolean;


//TODO Designer logic should be moved to Designer Module.
@Injectable({
    providedIn: 'root',
})
class FormsManager {
    // @lazyInject('ApplicationLock')
    private applicationLock: ApplicationLockService = inject(ApplicationLockService);
    // @lazyInject('Util')
    private util: Util = inject(Util);
    // @lazyInject('I18n')
    private i18n: I18nService = inject(I18nService);
    // @lazyInject('SocketHandler')
    private socketHandler: SocketHandler = inject(SocketHandler);
    // @lazyInject('LayoutHandler')
    // private layoutHandler: LayoutHandler;

  private notificationService: NotificationService = inject(NotificationService);
  private eventsManager: EventsManager = inject(EventsManager);

    public duringShutdown: boolean = false;
  private openedForms: FormComponent[] = [];
    public openedFormsSubject: Subject<any> = new Subject<any>();
  public changesSubject: Subject<FhChanges> = new Subject<any>();
    private usedContainers: any = {};
    public eventQueue: any[] = [];
    private pendingRequestsCount: number = 0;
    private currentMouseDownElement: HTMLElement = null;
    private formObj: any;
    public firedEventData: any;

    // identyfikator ostatnio aktywnego elementu przy zamykaniu formatki
    private lastActiveElementId: string = null;
    // identyfikator ostatnio aktywnego formularza
    private lastClosedFormId: string;

    private initialized: boolean = false;

    constructor(public injector: Injector) {

        // this.registerBodyMouseEvents();
    }

    public getActiveForm() {
        return this.openedForms.length > 0 ? this.openedForms[this.openedForms.length - 1] : null;
    }

    public openForm(formObj) {
        // let form = (<any>FhContainer.get('Form'))(formObj);
        //Na razie zostajemy na obiekcie JSON - może przeniesiemy też klasę Form z 4.x gdy będzie potrzebna do dalszej pracy.
        this.openedFormsSubject.next(formObj);
      // this.openedForms.push(formObj);
        this.usedContainers[formObj.containerId] = formObj;
        this.formObj = formObj;

        // form.create();


    }

    public openForms(formsList) {
        if (!formsList) {
            return;
        }
        formsList.forEach((formObj) => {
            // if (this.usedContainers[formObj.container] && ENV_IS_DEVELOPMENT) {
            if (this.usedContainers[formObj.container]) {
                console.error('Framework Error: Container "' + formObj.container + '" is used by form "'
                    + this.usedContainers[formObj.container].id + '". It will be overwritten');
            }
            this.openForm(formObj);
            if (this.lastActiveElementId && this.lastClosedFormId === formObj.id) {
                // let element = document.getElementById(this.lastActiveElementId);
                // if (element) {
                //     // skip menu's focus
                //     if (document.getElementById('menuForm').contains(element)) {
                //         return;
                //     }
                //     element.focus();
                // }
            }
        });
    }

    public clear() {
        this.eventQueue = [];
        this.pendingRequestsCount = 0;
    }

    public setInitialized() {
        this.initialized = true;
    }

    public isInitialized() {
        return this.initialized;
    }

  closeForm(form: FormComponent) {
    if (form) {
      this.usedContainers[form.container.id] = null;
      let indexToDelte = -1;
      this.openedForms.forEach((value, index) => {
        if (value.id == form.id) {
          indexToDelte = index;
        }
      })
      this.openedForms.splice(indexToDelte, 1);

        if (document.activeElement) {
            this.lastActiveElementId = document.activeElement.id || null;
        }
        this.lastClosedFormId = form.id;
        // form.destroy();
        form = null;
    }
    }

    closeForms(formsList) {
        if (!formsList) {
            return;
        }
        formsList.forEach((formId) => {
            let form = this.findForm(formId);
            if (typeof form === 'object') {

                // if (form.container.id === 'designedFormContainer' && !localStorage.getItem('formContainerHeight')) {
                //     localStorage.setItem('formContainerHeight', form.container.clientHeight);
                // }

                this.closeForm(form);
            }
        });
    }

    findForm(formId) {
        for (let i = 0, len = this.openedForms.length; i < len; i++) {
            let form = this.openedForms[i];
            if (form.id === formId) {
                return form;
            }
        }
      return null;
    }

    handleExternalEvents(eventList) {
        if (!eventList) {
            return;
        }
        eventList.forEach((eventObj) => {
            let type = eventObj.type;
          // let event = this.injector.get('Events.' + type, null); // sprawdzić czy po stringu znajdzie, jeżeli nie - zdefiniować InejctionToken dla każdego eventu.

          let event: BaseEvent = this.eventsManager.getEvent(type);

            if (event && typeof event.fire === 'function') {
                event.fire(eventObj);
            }
        });
    }

    findFormByContainer(containerId) {
        let form = null;
        for (let i = 0; i < this.openedForms.length; i++) {
          if (this.openedForms[i].container.id === containerId) {
                form = this.openedForms[i];
                break;
            }
        }
        return form;
    }


    /**
     * TODO Rewrite to Angular later
     * @param componentId
     * @param containerId
     */
    focusComponent(componentId, containerId) {
        let path = false;
        let form = null;
        if (containerId) {
            form = this.findFormByContainer(containerId);
            if (form) {
                // path = form.findComponent(componentId, false, true);
            }
        } else {
            for (let i = 0, len = this.openedForms.length; i < len; i++) {
                form = this.openedForms[i];
                // path = form.findComponent(componentId, false, true);
                if (path) {
                    break;
                }
            }
        }
        if (form && path) {
            // if (form.modalDeferred) {
            //     form.modalDeferred.promise().then(() => form.focusComponent(path, 0));
            // } else {
            //     form.focusComponent(path, 0);
            // }
        }
    }

    /**
     * TODO Zdecydować czy potem będzie nam potrzebna apliakcja samych zmian. Przepisać na Angulara
     * @param changesList
     */
    applyChanges(changesList) {
      if (changesList) {
        //Grupujemy zmiany po id formularza.
        let groupedCHanges = {};
        changesList.forEach((change: any) => {
          if (!groupedCHanges[change.formId]) {
            groupedCHanges[change.formId] = [];
          }
          groupedCHanges[change.formId].push(change);

        });

        this.changesSubject.next(groupedCHanges)
      }

      // if (!changesList) {
      //     return;
      // }
      // changesList.forEach((change) => {
      //     let form = this.findForm(change.formId);
      //     if (form) {
      //         // form.applyChange(change);
      //     }
      // });
    }

    fireEvent(eventType, actionName, formId, componentId, deferredEvent, doLock, params = undefined) {
        let serviceType = eventType === null && formId == null;

        if (this.pendingRequestsCount > 0) {
          // let event = new NotificationEvent();
          // event.fire({
          //     level: 'warning',
          //     message: this.i18n.__("request pending")
          // });
          this.notificationService.showWarning(this.i18n.__("request pending"))
            return false;
        }

        let form = this.findForm(formId);
        // if (!serviceType && form === false && ENV_IS_DEVELOPMENT) {
      if (!serviceType && !form) {
            console.warn('%cForm not found. EventType: %s, formId: %s, componentId: %s', 'background: #cc0000; color: #FFF', eventType, formId, componentId);
            // alert('Form not found. See console for more information.');
            return false;
        }
        // if (!serviceType && form.container === undefined && ENV_IS_DEVELOPMENT) {
        if (!serviceType && form.container === undefined) {
            console.warn('%cForm container not found. EventType: %s, formId: %s, componentId: %s, form: %o', 'background: #cc0000; color: #FFF', eventType, formId, componentId, form);
            // alert('Form container not found. See console for more information.');
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
            //TODO Rozwiązać pobieranie zmian z komponentów.
          let changes = form.collectAllChanges();
          eventData.changedFields = eventData.changedFields.concat(changes);
        });

        // if (ENV_IS_DEVELOPMENT) {
        console.log('%c eventData ', 'background: #F0F; color: #FFF', eventData);
        // }

        deferredEvent.deferred.promise().then(() => {
            let currentForm = this.findForm(formId);
            // if (!serviceType && (currentForm === false || (!deferredEvent.component.designMode && deferredEvent.component.destroyed) ||
            //     (document.getElementById(componentId) == null && currentForm.findComponent(componentId, true, false, true) === false))) {
          if (!serviceType && (!currentForm || (!deferredEvent.component.designMode && deferredEvent.component.destroyed))) { // some components are not available in HTML (RuleDiagram) and some in findComponent (Table row components)
                console.error('Component ' + componentId + ' on form ' + formId + ' is not available any more. Not sending event from this component to server.');
                this.triggerQueue();
            } else {
                let requestId = this.socketHandler.activeConnector.run(
                    SocketOutputCommands.HANDLE_EVENT, eventData,
                    (requestId, data) => {
                        this.handleEvent(requestId, data);
                    });
                if (doLock) {
                    // this.applicationLock.enable(requestId);
                    this.applicationLock.showUC();
                }
            }
        });
        return true;
    }

    fireHttpMultiPartEvent(eventType, actionName, formId, componentId, url, data: FormData) {
      let eventData = {
        formId: formId,
        eventSourceId: componentId,
        eventType: eventType,
        changedFields: []
      };

        // //Cycle through all the boxes to gather all changes
      this.openedForms.forEach(function (form) {
        let changes = form.collectAllChanges();
        eventData.changedFields = eventData.changedFields.concat(changes);
      });

        // data.append('eventData', JSON.stringify(eventData));
        let token = Cookies.get('XSRF-TOKEN');
        let deferred = $.Deferred();
        let progress = $('#' + componentId).find('.progress-bar');
        progress.parent().get(0).classList.remove('d-none');
        progress.width(0).show(0);
        let requestHandle = $.ajax(<any>{
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
            success: (data) => {
                this.pendingRequestsCount--;
                deferred.resolve(data);
            },
            error: (request, statusTxt) => {
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
            },
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
                layout: ""
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

        // TODO Errors - Wprowadzić obsługę błędów na komponentah Angular
        // if (mergedResult.errors && mergedResult.errors.length) {
        //     this.applicationLock.createErrorDialog(mergedResult.errors);
        // }
        // this.layoutHandler.startLayoutProcessing(mergedResult.layout);
        // this.layoutHandler.finishLayoutProcessing();
        this.closeForms(mergedResult.closeForm);
        this.openForms(mergedResult.openForm);
        this.applyChanges(mergedResult.changes);

        this.handleExternalEvents(mergedResult.events);


        if (requestId) {
            // this.applicationLock.disable(requestId);
            this.applicationLock.hideUC()
        }

        // TODO Obsługa Formów typu modal
        if (this.openedForms.length > 0 && this.openedForms[this.openedForms.length - 1].formType === 'MODAL') {
            // $('body').addClass('modal-open');
        }

        // clear awaiting to be clicked element after application lock is really disabled
        // if (!this.applicationLock.isActive()) {
        this.currentMouseDownElement = null;
        // }

        // if (ENV_IS_DEVELOPMENT) {
        console.log("Finished request processing");
        // }

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

    // TODO Ten problem chyba już nas nie będzie dotyczył.
    // There is a problem when clicking on a button (with onClick) while standing on an input (with onChange).
    // The input fires onChange on blur, application lock is shown and the button doesn't get the onClick event at all.
    // We remeber last mouse downed element and fire click event when mouse button is released while showing application lock.
    // registerBodyMouseEvents() {
    //     $('body').on('mouseup', function (event) {
    //         this.onBodyMouseUp(event);
    //     }.bind(this));
    //     $('body').on('mousedown', function (event) {
    //         this.onBodyMouseDown(event);
    //     }.bind(this));
    // };
    //
    // informElementMouseDown(element) {
    //     this.currentMouseDownElement = element;
    // }

    // onBodyMouseUp(event) {
    //     if (this.applicationLock.isActive() && this.currentMouseDownElement != null) {
    //         if (ENV_IS_DEVELOPMENT) {
    //             console.log('%cFiring \'click\' event on ' + this.currentMouseDownElement + ' as mouse button was released on application lock.', 'background: #FAA');
    //         }
    //
    //         let evt = document.createEvent('Events');
    //         evt.initEvent('click', true, true);
    //         this.currentMouseDownElement.dispatchEvent(evt);
    //     }
    //     this.currentMouseDownElement = null;
    // }
    //
    // onBodyMouseDown(event) {
    //     if (!this.applicationLock.isActive()) {
    //         this.informElementMouseDown(event.target);
    //     }
    // }

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

    //TODO toggleMenu() prawdopodobnie nie używane już.

    // toggleMenu() {
    //     let menuForm = this.layoutHandler.getLayoutContainer("menuForm");
    //     // document.getElementById('menuForm');
    //     let toolboxContainer = document.getElementById('formDesignerToolbox');
    //     // document.getElementById('formDesignerToolbox');
    //     let toolbox = this.layoutHandler.getLayoutContainer("designerToolbox");
    //     // document.getElementById('designerToolbox');
    //     let menuTogglerIcon = document.getElementById('menuTogglerIcon');
    //     // document.getElementById('menuTogglerIcon');
    //     let menuFormInner = document.getElementById('menuFormInner');
    //     // document.getElementById('menuFormInner');
    //
    //     if (!toolboxContainer.contains(menuFormInner)) {
    //         menuForm.removeChild(menuFormInner);
    //
    //         if (toolbox !== null) {
    //             toolbox.classList.add('d-none');
    //         }
    //         menuFormInner.classList.add('hiddenMenuForm');
    //         toolboxContainer.appendChild(menuFormInner);
    //
    //         menuTogglerIcon.classList.remove('fa-caret-right');
    //         menuTogglerIcon.classList.remove('menuExpandRight');
    //         menuTogglerIcon.classList.add('fa-caret-left');
    //         menuTogglerIcon.classList.add('menuCollapseLeft');
    //     } else {
    //         menuFormInner.classList.remove('hiddenMenuForm');
    //         toolboxContainer.removeChild(menuFormInner);
    //         menuForm.appendChild(menuFormInner);
    //
    //         if (toolbox !== null) {
    //             toolbox.classList.remove('d-none');
    //         }
    //
    //         menuTogglerIcon.classList.remove('fa-caret-left');
    //         menuTogglerIcon.classList.remove('menuCollapseLeft');
    //         menuTogglerIcon.classList.add('fa-caret-right');
    //         menuTogglerIcon.classList.add('menuExpandRight');
    //     }
    // }

  public registerForm(form: FormComponent) {
    //rejestrjemy jezeli nie istnieje w tablicy
    if (this.openedForms.indexOf(form) == -1) {
      this.openedForms.push(form);
    }
  }

  public unregisterForm(form: FormComponent) {
    if (this.openedForms.indexOf(form) > -1) {
      this.openedForms.splice(this.openedForms.indexOf(form), 1);
    }
  }



}

export {FormsManager, FhChanges};


interface FhChanges {
  [key: string]: {
    formId: string,
    formElementId: string,
    changedAttributes: { [key: string]: string }
  }[]
}
