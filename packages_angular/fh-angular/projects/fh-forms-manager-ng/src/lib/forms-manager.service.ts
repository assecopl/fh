import {Injectable} from '@angular/core';
import {SocketOutputCommands} from "../enum/SocketOutputCommands";
import {SocketHandlerService} from "./socket-handler.service";
import {ServiceManager} from "./ServiceManager";
import {Subject} from "rxjs";
import {i18nService} from './service/i18n.service';

@Injectable({
    providedIn: 'root'
})
export class FormsManagerService {

  public response = new Subject<any>();

    protected serviceManagers: ServiceManager[] = [];

  public duringShutdown: boolean = false;
  private openedForms: any[] = [];
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

  private i18n: i18nService = new i18nService();

    constructor(private socketHandler: SocketHandlerService) {

    }


    public init(context: string = 'socketForms') {
        this.socketHandler.selectBestConnector();
        this.socketHandler.activeConnector.connect((connectionIdJson: any) => {
            this.socketHandler.connectionId = connectionIdJson.sessionId;

            let requestId = this.socketHandler.activeConnector.getSubsystemMetadata(
                (requestId: any, data: any) => {
                    // this.formsManager.setInitialized();
                    // this.formsManager.handleEvent(requestId, data);
                  this.response.next(data);
                  data.openForm.forEach((form) => {
                    this.openedForms.push(form);
                  })

                    for (let deviceManager of this.serviceManagers) {
                        deviceManager.init((config: any) =>
                            this.socketHandler.activeConnector.run(SocketOutputCommands.IN_CLIENT_DATA, config));
                    }
                });
        });

        // $(function () {
        //   $(window).on('hashchange', function () {
        //     if (!this.ignoreNextHashChange) {
        //       var json = {'url': location.pathname + this.formsManager.getLocationHash()};
        //       var requestId = this.socketHandler.activeConnector.run(SocketOutputCommands.URL_CHANGE, json);
        //       this.applicationLock.enable(requestId);
        //     } else {
        //       this.ignoreNextHashChange = false;
        //     }
        //   }.bind(this));
        // }.bind(this));
    }

  fireEvent(eventType, actionName, formId, componentId, deferredEvent, doLock, params = undefined) {
    let serviceType = eventType === null && formId == null;

    if (this.pendingRequestsCount > 0) {
      // let event = new NotificationEvent();
      // event.fire({
      //     level: 'warning',
      //     message: this.i18n.__("request pending")
      // });
      return false;
    }

    let form = this.findForm(formId);
    if (!serviceType && form === false) {
      console.warn('%cForm not found. EventType: %s, formId: %s, componentId: %s', 'background: #cc0000; color: #FFF', eventType, formId, componentId);
      // alert('Form not found. See console for more information.');
      return false;
    }
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
      // let changes = form.collectAllChanges();
      eventData.changedFields = eventData.changedFields.concat({});
    });

    if (1 == 1) {
      console.log('%c eventData ', 'background: #F0F; color: #FFF', eventData);
    }

    deferredEvent.deferred.promise().then(() => {
      let currentForm = this.findForm(formId);
      // if (!serviceType && (currentForm === false || (!deferredEvent.component.designMode && deferredEvent.component.destroyed) ||
      //     (document.getElementById(componentId) == null && currentForm.findComponent(componentId, true, false, true) === false))) { // some components are not available in HTML (RuleDiagram) and some in findComponent (Table row components)
      //     console.error('Component ' + componentId + ' on form ' + formId + ' is not available any more. Not sending event from this component to server.');
      //     this.triggerQueue();
      // } else {
      let requestId = this.socketHandler.activeConnector.run(
        SocketOutputCommands.HANDLE_EVENT, eventData,
        (requestId, data) => {
          this.handleEvent(requestId, data);
        });
      if (doLock) {
        // this.applicationLock.enable(requestId);
      }
      // }
    });
    return true;
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

  triggerQueue() {
    this.eventQueue.splice(0, 1);

    if (this.eventQueue.length) {
      this.eventQueue[0].deferred.resolve();
    }
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

    this.response.next(resultOrArray);

    // if (mergedResult.errors && mergedResult.errors.length) {
    //     this.applicationLock.createErrorDialog(mergedResult.errors);
    // }
    // this.layoutHandler.startLayoutProcessing(mergedResult.layout);
    // this.layoutHandler.finishLayoutProcessing();
    // this.closeForms(mergedResult.closeForm);
    // this.openForms(mergedResult.openForm);
    // this.applyChanges(mergedResult.changes);
    // this.handleExternalEvents(mergedResult.events);


    // if (requestId) {
    //     this.applicationLock.disable(requestId);
    // }

    // if (this.openedForms.length > 0 && this.openedForms[this.openedForms.length - 1].formType === 'MODAL') {
    //     $('body').addClass('modal-open');
    // }

    // clear awaiting to be clicked element after application lock is really disabled
    // if (!this.applicationLock.isActive()) {
    //     this.currentMouseDownElement = null;
    // }

    // if (ENV_IS_DEVELOPMENT) {
    console.log("Finished request processing");
    // }

    this.triggerQueue();
  }

  // public openForms(formsList) {
  //     if (!formsList) {
  //         return;
  //     }
  //     formsList.forEach(function (formObj) {
  //         if (this.usedContainers[formObj.container] && ENV_IS_DEVELOPMENT) {
  //             console.error('Framework Error: Container "' + formObj.container + '" is used by form "'
  //                 + this.usedContainers[formObj.container].id + '". It will be overwritten');
  //         }
  //         this.openForm(formObj);
  //         if (this.lastActiveElementId && this.lastClosedFormId === formObj.id) {
  //             let element = document.getElementById(this.lastActiveElementId);
  //             if (element) {
  //                 // skip menu's focus
  //                 if (document.getElementById('menuForm').contains(element)) {
  //                     return;
  //                 }
  //                 element.focus();
  //             }
  //         }
  //     }.bind(this));
  // }

}
