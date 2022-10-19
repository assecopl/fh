import {injectable,} from "inversify";
import {HTMLFormComponent} from "./HTMLFormComponent";
import * as $ from 'jquery';
import {FhContainer} from "../FhContainer";
import {LayoutHandler} from "../LayoutHandler";
import getDecorators from "inversify-inject-decorators";
import * as focusTrap  from 'focus-trap';

let {lazyInject} = getDecorators(FhContainer);

@injectable()
class Form extends HTMLFormComponent {

    @lazyInject('LayoutHandler')
    private layoutHandler: LayoutHandler;

    public formType: string;
    private drag: any;
    private dragging: boolean;
    private resizing: boolean;
    private position: { top: number; left: number };
    private dimensions: { width: number; height: number };
    private onManualModalClose: any;
    private keyupEvent: any;
    private resourcesUrlPrefix: string;
    private focusFirstElement: boolean;
    public state: string;
    public viewMode: string;
    private windowListenerMouseMove: any;
    private windowListenerMouseUp: any;
    private modalDeferred;
    protected headingTypeValue: "h1" | "h2" | "h3" | "h4" | "h5" | "h6" = null;
    protected blockFocusForModal:boolean = false;

    protected afterInitActions:Array<any> = [];

    protected focusTrap:focusTrap.FocusTrap = null;

    constructor(formObj: any, parent: any = null) {
        super(formObj, parent);

        this.combinedId = this.id;
        this.formId = this.id;
        this.formType = formObj.formType;
        this.viewMode = this.componentObj.viewMode;
        this.focusFirstElement = this.componentObj.focusFirstElement;
        this.state = this.componentObj.state;
        this.headingTypeValue = this.componentObj.headingTypeValue? this.componentObj.headingTypeValue : null;
        this.blockFocusForModal = this.componentObj.blockFocusForModal? this.componentObj.blockFocusForModal : false;
        // console.log("this.componentObj.blockFocusForModal", this.componentObj.blockFocusForModal)

        /* TODO: remove after changing Java */
        if (this.componentObj.modal) {
            this.formType = 'MODAL';
        }

        if (this.viewMode != 'NORMAL') {
            var editorClasses = $('#designedFormContainer')[0].classList;
            editorClasses.remove('editing-modal-sm');
            editorClasses.remove('editing-modal-md');
            editorClasses.remove('editing-modal-lg');
            editorClasses.remove('editing-modal-xlg');
            editorClasses.remove('editing-modal-xxlg');
            editorClasses.remove('editing-modal-full');
            if (this.formType == 'MODAL' || this.formType == 'MODAL_OVERFLOW') {
                switch (this.componentObj.modalSize) {
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
            this.formType = 'STANDARD';
        }

        if (this.formType === 'FLOATING') {
            this.dragging = false;
            this.resizing = false;
            this.position = {top: 0, left: 0};
            this.dimensions = {width: 0, height: 0};
            this.drag = {startX: 0, startY: 0, posX: 0, posY: 0, width: 0, height: 0};
        } else if (this.formType === 'HEADER') {

        }

        this.resourcesUrlPrefix = this.componentObj.resourcesUrlPrefix;
        this.onManualModalClose = this.componentObj.onManualModalClose;
        this.components = [];

        if (this.id && this.id == "formDesignerComponents") {
            //FIXME ever executed ?? Looks like never. There is no form witdh with this id.
            (<any>FhContainer.get('Designer')).beforeDesignerFormCreated();
        }

        if (this.formType == 'MODAL') {
            this.layoutHandler.blockLayoutChangeForModal();
        }
        if (this.designMode) {
            this.layoutHandler.blockLayoutChangeForDesigner();
        }


        if (this.parent != null) {
            this.container = this.parent.contentWrapper;
        } else { // FORM
            this.container = this.layoutHandler.getLayoutContainer(this.parentId);
            // this.mainLayout = this.componentObj.layout;
            if (this.container == null && this.parentId != 'MODAL_VIRTUAL_CONTAINER') {
                throw "Container '" + this.parentId + "' not found";
            }
        }

    }

    create() {
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
            $(this.htmlElement).modal({show: true, focus: true});

            $(this.htmlElement).one('shown.bs.modal', function () {
                while (this.contentWrapper != null && this.contentWrapper.firstChild) this.contentWrapper.removeChild(this.contentWrapper.firstChild);
                this.renderSubcomponents();
                this.modalDeferred.resolve();
                this.focusFirstActiveInputElement(true);

                /**
                 * WCAG(Srean Reader) Block focus on elements that are outside/under opened modal.
                 * Used FocusTrap library : https://www.npmjs.com/package/focus-trap
                 * We can't block key modal functionality so errors will be only log to console.
                 */
                if (this.blockFocusForModal) {
                    try {
                        if (this.htmlElement) {
                            // @ts-ignore
                            this.focusTrap = focusTrap(this.htmlElement, {});
                            this.focusTrap.activate();
                        }
                    } catch (e) {
                        console.log(focusTrap);
                        console.info(e);
                    }
                }
            }.bind(this));

            /**
             * WCAG(Srean Reader) Deactivate focus trpa on modal close.
             */
            if(this.blockFocusForModal) {
                $(this.htmlElement).on('hidden.bs.modal', function (e) {
                    try {
                        if (this.focusTrap) {
                            this.focusTrap.deactivate();
                        }
                    } catch (e) {
                        console.info(e);
                    }
                }.bind(this))
            }
        } else {
            this.renderSubcomponents();
            this.focusFirstActiveInputElement();
        }

        if (this.viewMode == 'DESIGN') {
            this.component.dataset.designable = 'true';
            (<any>FhContainer.get('Designer')).onEditedFormCreated(this);
        }

        if (this.component.clientHeight > this.container.clientHeight) {
            localStorage.setItem('updatedFormHeight', this.component.clientHeight);
            let updatedFormHeight = localStorage.getItem('updatedFormHeight');
            let parsedFormHeightWithBorder = parseInt(updatedFormHeight, 10) + 2;
            this.container.style.height = parsedFormHeightWithBorder + 'px';
            localStorage.removeItem('updatedFormHeight');
        } else if (this.container.clientHeight === 0) {
            this.container.style.height = 'auto';
        }

        this.fireAfterInitActions();
    };

    destroy(removeFromParent) {
        this.destroyed = true;
        if (this.windowListenerMouseUp != null) {
            window.removeEventListener('mouseup', this.windowListenerMouseUp);
        }
        if (this.windowListenerMouseMove != null) {
            window.removeEventListener('mousemove', this.windowListenerMouseMove);
        }
        if (this.formType === 'STANDARD') {
            if (this.container.id === 'designedFormContainer') {
                let containerHeight = localStorage.getItem('formContainerHeight');
                this.container.style.height = parseInt(containerHeight, 10) + 'px';
                localStorage.removeItem('formContainerHeight');
                (<any>FhContainer.get('Designer')).destroy();
            }
            this.container.innerHTML = '';
        } else if (this.formType === 'MODAL' || this.formType === 'MODAL_OVERFLOW') {
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
        } else if (this.formType === 'FLOATING') {
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

    update(change) {
        super.update(change);

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

    buildForm() {
        var form = null;
        switch (this.formType) {
            case 'STANDARD':
                form = this.buildStandardForm();
                break;
            case 'MINIMAL':
                form = this.buildMinimalForm();
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

    buildModalForm() {
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
            this.labelElement = document.createElement(this.headingTypeValue?this.headingTypeValue:"strong");
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

    focusFirstActiveInputElement(focusModalButton = false) {
        if (this.designMode || !this.focusFirstElement) {
            return;
        }

        const input = $(this.component).find(':input:enabled:not([readonly]):not(button):first')
        if(input.length > 0) {
            input.trigger('focus');
        } else {
            if(focusModalButton) {
                $(this.component).find('button.button:first').trigger('focus');
            }
        }
    }

    buildFloatingForm() {
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
        this.labelElement = document.createElement(this.headingTypeValue?this.headingTypeValue:"span");
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

    buildStandardForm() {
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
            this.labelElement = document.createElement(this.headingTypeValue?this.headingTypeValue:"span");
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

    buildMinimalForm() {
        var form = document.createElement('div');

        form.id = this.id;

        ['fc', 'form'].forEach(function (cssClass) {
            form.classList.add(cssClass);
        });

        var row = document.createElement('div');
        row.classList.add('row');
        row.classList.add('eq-row');
        this.contentWrapper = row;

        form.appendChild(row);

        return form;
    };

    buildHeaderForm() {
        var fluid = document.createElement('div');
        fluid.id = this.id;

        var collapse = document.createElement('div');
        collapse.id = 'navbar-main';

        this.contentWrapper = collapse;
        fluid.appendChild(collapse);

        return fluid;
    };

    buildCloseButton() {
        var button = document.createElement('button');
        button.type = 'button';
        button.classList.add('close');
        button.innerHTML = '&times;';
        button.addEventListener('click', function (event) {
            this.fireEventWithLock('onManualModalClose', this.onManualModalClose);
        }.bind(this));

        return button;
    };

    mouseDown(event) {
        var styles = window.getComputedStyle(this.htmlElement);
        this.drag.posX = this.position.left = parseFloat(styles.getPropertyValue('left'));
        this.drag.posY = this.position.top = parseFloat(styles.getPropertyValue('top'));
        this.drag.width = this.dimensions.width = parseFloat(styles.getPropertyValue('width'));
        this.drag.height = this.dimensions.height = parseFloat(styles.getPropertyValue('height'));
        this.drag.startX = event.clientX;
        this.drag.startY = event.clientY;
    };

    addCloudIcon(titleElement) {
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

    setPresentationStyle(presentationStyle) {
        return;
    }

    public addAfterInitActions(action: () => void){
        this.afterInitActions.push(action);
    }

    private fireAfterInitActions(){
        this.afterInitActions.forEach(function (action) {
            if(action && {}.toString.call(action) === '[object Function]'){
                action();
            }
        });
        this.afterInitActions = [];
    }

}

export {Form};
