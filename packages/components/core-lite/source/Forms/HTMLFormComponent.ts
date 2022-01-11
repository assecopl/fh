import 'bootstrap/js/dist/tooltip';
import 'bootstrap/js/dist/popover';
import {FormComponent} from './FormComponent';
import {FhContainer} from '../FhContainer';
import getDecorators from 'inversify-inject-decorators';
import {I18n} from '../I18n/I18n';
import {FHML} from '../FHML';
import {AdditionalButton} from './AdditionalButton';
import * as $ from 'jquery';
import {Placement, PopoverOption, TooltipOption, Trigger} from "bootstrap";
import {ComponentExtender} from './ComponentExtender';

let {lazyInject} = getDecorators(FhContainer);

abstract class HTMLFormComponent extends FormComponent {
    @lazyInject('FHML')
    protected fhml: FHML;

    @lazyInject('I18n')
    protected i18n: I18n;

    protected container: HTMLElement;
    protected hintElement: HTMLElement;
    public htmlElement: any;
    public accessibility: string;
    public invisible: string;
    protected presentationStyle: string;
    protected requiredField: string;
    public component: any;
    public toolbox: HTMLElement;
    protected inputGroupElement: HTMLElement;
    protected labelElement: HTMLElement;
    protected requiredElement: HTMLElement;
    protected requiredHidden: boolean;
    private readonly translationItems: any;
    private inputSize: any;
    public width: string[];
    protected styleClasses: string[] = [];
    protected readonly inlineStyle: string;
    protected readonly wrapperStyle: string;
    private readonly language: any;
    protected hint: any;
    public hintTitle: string = "";
    public hintAriaLabel: string = null;
    protected hintIcon: string = null;
    protected hintPlacement: Placement;
    protected hintTrigger: Trigger;
    public hintType: 'STANDARD' | 'STANDARD_POPOVER' | 'STATIC' | 'STATIC_POPOVER' | 'STATIC_POPOVER_LEFT' | 'STATIC_LEFT' = 'STANDARD';
    public hintInputGroup: boolean = false;
    protected hintInitialized: boolean = false;
    public rawValue: any;
    private areSubcomponentsRendered: boolean;
    protected oldValue: any;
    protected components: HTMLFormComponent[];
    private readonly push: boolean;
    private static bootstrapColRegexp: RegExp = /^(xs|sm|md|lg|xl)-([1-9]|1[0-2])$/i;
    private static bootstrapColWidthRegexp: RegExp = /^\d+(px|%)$/i;
    private static bootstrapColSeparateCahrsRegexp: RegExp = /(,|;|\|\/|\|)/g;
    protected focusableComponent: HTMLElement;
    public type: string;
    public input: any;

    public autocomplete: string = null;
    public ariaLabel: string = null;
    public htmlAccessibilityRole: string = null;


    protected constructor(componentObj: any, parent: FormComponent) {
        super(componentObj, parent);

        if (this.parent != null) {
            this.container = this.parent.contentWrapper;
        } else { // FORM
            this.container = document.getElementById(this.parentId);
            if (this.container == null && this.parentId != 'MODAL_VIRTUAL_CONTAINER') {
                throw 'Container \'' + this.parentId + '\' not found';
            }
        }
        this.combinedId = this.parentId + '_' + this.id;

        this.hintType = this.componentObj.hintType;
        this.hintInputGroup = this.componentObj.hintInputGroup ? this.componentObj.hintInputGroup : false;
        this.accessibility = this.componentObj.accessibility;
        this.invisible = this.componentObj.invisible;
        this.presentationStyle = this.componentObj.presentationStyle;
        this.requiredField = this.componentObj.required;
        this.autocomplete = this.componentObj.autocomplete ? this.componentObj.autocomplete : null;
        this.ariaLabel = this.componentObj.ariaLabel ? this.componentObj.ariaLabel : null;
        this.htmlAccessibilityRole = this.componentObj.htmlAccessibilityRole ? this.componentObj.htmlAccessibilityRole : null;
        this.designMode = this.componentObj.designMode || (this.parent != null && this.parent.designMode);
        if (this.designMode) {
            this.buildDesingerToolbox();
        }

        this.handleWidth();

        if (typeof this.oldValue === 'undefined') {
            if (this.componentObj.rawValue != null) {
                this.oldValue = this.componentObj.rawValue;
            } else {
                this.oldValue = this.componentObj.value;
            }
        }
        this.inlineStyle = this.componentObj.inlineStyle;
        this.wrapperStyle = this.componentObj.wrapperStyle;
        this.push = this.componentObj.push;
        this.rawValue = this.oldValue;
        this.htmlElement = null;
        this.component = this.htmlElement;
        this.contentWrapper = this.component;
        this.styleClasses = (this.componentObj.styleClasses || '').split(/[, ]/);

        this.hint = this.componentObj.hint || null;
        this.hintPlacement = this.componentObj.hintPlacement || 'auto';
        this.hintIcon = this.componentObj.hintIcon || null;
        this.hintAriaLabel = this.componentObj.hintAriaLabel || null;
        if (this.componentObj.hintTrigger) {
            switch (this.componentObj.hintTrigger) {
                case 'HOVER_FOCUS':
                    this.hintTrigger = 'hover focus';
                    break;
                case 'HOVER':
                    this.hintTrigger = 'hover';
                    break;
                case 'CLICK': 
                    this.hintTrigger = 'click';
                    break;
            }
        } else {
            this.hintTrigger = 'hover focus';
        }
        this.hintElement = null;

        if (this.formId === 'designerComponents' || this.formId === 'designerToolbox') {
            let a: any = this;
            while ((a = a.parent) != null) {
                if (a.component != null && a.component.classList.contains('designerToolbox')) {
                    (<any>FhContainer.get('Designer')).onDesignerToolboxComponentCreated(a);
                    break;
                }
            }
        }

        this.requiredElement = null;
        this.areSubcomponentsRendered = false;

        /* Languages */
        this.language = this.componentObj.language || null;
        this.translationItems = [];

        this.labelElement = null;


        this.hintTitle = this.componentObj.hintTitle ? this.fhml.parse(this.componentObj.hintTitle) : this.fhml.parse(this.componentObj.label)
    }

    /* Create component and assign it's HTML to this.htmlElement */
    create() {
        this.component = document.createElement('div');
        this.htmlElement = this.component;
        this.contentWrapper = this.htmlElement;
        this.hintElement = this.component;

        this.display();

        /* Add subcomponents */
        super.create();
    }

    /* Append component to container */
    display() {
        this.processAutocomplete(this.autocomplete);
        this.setAccessibility(this.accessibility);
        this.setPresentationStyle(this.presentationStyle);
        this.enableStyleClasses();

        if (this.designMode) {
            (<any>FhContainer.get('Designer')).addToolboxListeners(this);

            if (this.toolbox) {
                this.htmlElement.appendChild(this.toolbox);
            }

            if (this.component.classList.contains('tabContainer')) {
                this.component.addEventListener('click', function (e) {
                    e.stopImmediatePropagation();
                    e.preventDefault();

                    // get the tab's id and click on the tab to edit its properties
                    const clickedTabId = e.target.getAttribute('data-tab-id');
                    const clickedTab = document.getElementById(clickedTabId);
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
                    let columnId = this.component.dataset.columnId;
                    let column = this.component.closest('.fc.table').querySelector('.' + columnId);
                    column.click();
                }.bind(this));
            }
            if (this.component.classList.contains('outputLabel')) {
                this.component.addEventListener('click', function (e) {
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    this.component.focus();
                    this.fireEvent('onformedit_elementedit', 'elementedit');
                    document.addEventListener('keyup', event => {
                        this.handleDeleteBtnEvent(event);
                    });
                }.bind(this));
            }
            this.htmlElement.addEventListener('click', function (e) {
                e.stopImmediatePropagation();
                e.preventDefault();
                this.component.focus();
                this.fireEvent('onformedit_elementedit', 'elementedit');
                document.addEventListener('keyup', event => {
                    this.handleDeleteBtnEvent(event);
                });
            }.bind(this));
            if (this.contentWrapper && this.contentWrapper.classList.contains('button')) {
                this.component.addEventListener('click', function (e) {
                    this.component.focus();
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    this.fireEvent('onformedit_elementedit', 'elementedit');
                    document.addEventListener('keyup', event => {
                        this.handleDeleteBtnEvent(event);
                    });
                }.bind(this));
            }
            if (this.contentWrapper && this.contentWrapper.classList.contains('selectOneMenu')) {
                this.component.addEventListener('click', function (e) {
                    this.component.focus();
                    e.stopImmediatePropagation();
                    this.fireEvent('onformedit_elementedit', 'elementedit');
                    document.addEventListener('keyup', event => {
                        this.handleDeleteBtnEvent(event);
                    });
                }.bind(this));
            }
            if (this.contentWrapper && this.contentWrapper.classList.contains('fileUpload')) {
                this.component.addEventListener('click', function (e) {
                    this.component.focus();
                    e.stopImmediatePropagation();
                    e.preventDefault();
                    this.fireEvent('onformedit_elementedit', 'elementedit');
                    document.addEventListener('keyup', event => {
                        this.handleDeleteBtnEvent(event);
                    });
                }.bind(this));
            }
        }

        this.container.appendChild(this.htmlElement);

        this.setRequiredField(this.requiredField);
        if (this.hint !== null) {
            this.initHint();
        }

        const createChain = ComponentExtender.getInstance().getStaticExtenders(this.constructor.name, 'create');
        createChain.forEach((ext) => {
            if (ext) {
                ext(this.componentObj, this);
            }
        })
    }

    render() {
        if (this.htmlElement) {
            this.display();
        }
        if (!this.areSubcomponentsRendered) {
            this.renderSubcomponents();
            this.areSubcomponentsRendered = true;
        }
    }

    renderSubcomponents() {
        this.components.forEach(function (component) {
            component.render();
        });
    }

    hideHint() {
        if (this.hintElement) {
            if (this.hintType == 'STANDARD_POPOVER') {
                $(this.hintElement).popover('hide');
            } else {
                $(this.hintElement).tooltip('hide');
            }

            if (this.hintType === 'STATIC' || this.hintType == 'STATIC_POPOVER' || this.hintType == 'STATIC_POPOVER_LEFT') {
                let tip = $(this.hintElement).attr('aria-describedby');
                let tipElement = $('#' + tip);
                if (tipElement) {
                    if(this.hintType == 'STATIC_POPOVER' || this.hintType == 'STATIC_POPOVER_LEFT') {
                        tipElement.popover('hide');
                    } else {
                        tipElement.tooltip('hide');
                    }
                }
            }

        }
    }

    initHint() {
        const fhml = FhContainer.get<FHML>('FHML');
        let hintParsed = this.hint;
        if (fhml.needParse(hintParsed)) {
            hintParsed = fhml.parse(hintParsed);
        }
        if (this.hintElement && !this.hintInitialized && this.hint) {
            let tooltipOptions: TooltipOption = {
                placement: this.hintPlacement,
                title: hintParsed,
                trigger: this.hintTrigger,
                html: true,
                boundary: 'window'
                // selector: '#'+this.id
            };

            if (this.hintType == 'STATIC' || this.hintType == 'STATIC_POPOVER' || this.hintType == 'STATIC_LEFT' || this.hintType == 'STATIC_POPOVER_LEFT') {

                if (tooltipOptions.placement == 'auto') {
                    tooltipOptions.placement = 'top';
                }

                //Change tooltip trigger to click
                tooltipOptions.trigger = this.hintTrigger || 'click hover';
                //Create tooltip element
                const ttip = document.createElement('button');
                ttip.className = 'btn hint-ico-help fa';

                if(this.hintAriaLabel) {
                    ttip.setAttribute('aria-label', this.hintAriaLabel);
                } else {
                    ttip.setAttribute('aria-haspopup', 'true');
                }
                this.hintElement = ttip;
                if (this.inputGroupElement && this.hintInputGroup) {
                    let ttipButton = document.createElement('div');
                    ttipButton.onmouseenter = (ev: MouseEvent) => {
                        (ev.target as any).click();
                    };
                    ttipButton.onmouseleave =(ev: MouseEvent) => {
                        (ev.target as any).click();
                    };
                    this.inputGroupElement.classList.add('hint-static');

                    if(this.hintIcon){
                        this.hintIcon.toString().split(" ").forEach(value => {
                            const c = value.trim();
                             ttip.classList.add(c);
                        })
                    } else {
                        ttip.classList.add('fa-question-circle');
                    }
                    ttip.classList.add("input-group-text");

                    this.htmlElement.classList.add('hasInputHelpIcon');

                    this.hintElement = ttip;
                    ttip.classList.add('input-group-append');
                    this.inputGroupElement.appendChild(ttip);
                } else {
                    // p-0 px-1 border-0
                    ttip.classList.add("border-0");
                    ttip.classList.add("px-0");
                    ttip.classList.add("p-0");
                    /**
                     * Change typical behaviour by overwrite this function in component.
                     */
                    if(this.hintIcon){
                        this.hintIcon.toString().split(" ").forEach(value => {
                            const c = value.trim();
                            ttip.classList.add(c);
                        })
                    } else {
                        ttip.classList.add('fa-question-circle');
                    }

                    const element = this.processStaticHintElement(ttip);
                    element ? element.classList.add('hint-static') : null;
                }


            }

            if (this.hintType == 'STANDARD' || this.hintType == 'STATIC' || this.hintType == 'STATIC_LEFT') {
                $(this.hintElement).tooltip(tooltipOptions);
                this.hintElement.classList.add("fh-tooltip");
                this.hintInitialized = true;
            }
            if (this.hintType == 'STANDARD_POPOVER' || this.hintType == 'STATIC_POPOVER' || this.hintType == 'STATIC_POPOVER_LEFT') {
                const popOptions: PopoverOption = tooltipOptions;
                popOptions.content = tooltipOptions.title;
                popOptions.title = this.hintTitle ? this.hintTitle : "";

                //Add close buuton to static popovers
                if (this.hintType != "STANDARD_POPOVER") {
                    popOptions.title += '<i class="close popoverer-close fa fa-times pull-right" />'
                }

                $(this.hintElement).popover(popOptions);
                this.hintElement.classList.add("fh-popover");

                //Add close buuton logic to static popovers
                if (this.hintType != "STANDARD_POPOVER") {
                    $(this.hintElement).on('shown.bs.popover', function () {
                        // console.log("id", this.id );
                        // console.log("poper", $(this.hintElement).data('bs.popover') );
                        const popover = $(this.hintElement).data('bs.popover');
                        if (typeof popover !== "undefined") {
                            const tip = popover.tip;
                            const close = $(tip).find('.close')
                            close.css("cursor", "pointer");
                            close.bind('click', function () {
                                popover.hide();
                            });

                        }
                    }.bind(this));
                }
                this.hintInitialized = true;
            }


        }
    }

    /**
     *
     * @param ttip
     */
    public processStaticHintElement(ttip: any): HTMLElement {
        if (this.labelElement && !this.labelElement.classList.contains('sr-only')) {
            if (this.hintType == "STATIC_LEFT" || this.hintType == 'STATIC_POPOVER_LEFT') {
                ttip.classList.add('mr-1');
                $(this.labelElement).prepend(ttip);
            } else {
                ttip.classList.add('ml-2');
                this.labelElement.appendChild(ttip);
            }
            return this.labelElement;
        } else {
            return null;
        }
    }


    destroyHint() {
        if (this.hintElement && this.hintInitialized) {
            if (this.hintType === 'STATIC' || this.hintType === 'STATIC_LEFT' || this.hintType == 'STATIC_POPOVER' || this.hintType == 'STATIC_POPOVER_LEFT') {
                let tip = $(this.hintElement).attr('aria-describedby');
                let tipElement = $('#' + tip);
                if (tipElement) {
                    if(this.hintType == 'STATIC_POPOVER' || this.hintType == 'STATIC_POPOVER_LEFT') {
                        tipElement.popover('hide');
                        tipElement.popover('disable');
                        tipElement.popover('dispose');
                    } else {
                        tipElement.tooltip('hide');
                        tipElement.tooltip('disable');
                        tipElement.tooltip('dispose');
                    }

                }
            }
            if (this.hintType == 'STANDARD_POPOVER' || this.hintType == 'STATIC_POPOVER' || this.hintType == 'STATIC_POPOVER_LEFT') {
                $(this.hintElement).popover('hide');
                $(this.hintElement).popover('disable');
                $(this.hintElement).popover('dispose');
                $(this.hintElement).remove();
            } else {
                $(this.hintElement).tooltip('hide');
                $(this.hintElement).tooltip('disable');
                $(this.hintElement).tooltip('dispose');
            }
            this.hintInitialized = false;
        }
    }

    /* Destroy component */
    destroy(removeFromParent) {
        this.destroyHint();

        if (removeFromParent && this.parent != null && this.parent.contentWrapper != null && this.htmlElement != null) {
            try {
                if (this.htmlElement.parentNode === this.parent.contentWrapper) {
                    this.parent.contentWrapper.removeChild(this.htmlElement);
                }
            } catch (e) {
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

        const createChain = ComponentExtender.getInstance().getStaticExtenders(this.constructor.name, 'destroy');
        createChain.forEach((ext) => {
            if (ext) {
                ext(this.componentObj, this);
            }
        })

        super.destroy(removeFromParent);

        document.removeEventListener('keyup', this.handleDeleteBtnEvent);
    }

    /* Update component */
    update(change) {
        super.update(change);

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
                            this.destroyHint();
                            this.labelElement.innerHTML = this.fhml.resolveValueTextOrEmpty(newValue);
                            this.updateLabelClass(newValue);
                            if (this.requiredField) {
                                if (['RadioOption', 'RadioOptionsGroup','CheckBox'].indexOf(this.componentObj.type) === -1) {
                                    this.labelElement.appendChild(this.requiredElement);
                                } else {
                                    this.setRequiredField(this.requiredField);
                                }
                            }
                            if (this.hint) {
                                this.initHint();
                            }
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
            const createChain = ComponentExtender.getInstance().getStaticExtenders(this.constructor.name, 'update');
            createChain.forEach((ext) => {
                if (ext) {
                    ext(change.changedAttributes, this.componentObj, this);
                }
            })
        }
    }

    updateLabelClass(newLabel) {
        if (this.labelElement != null) {
            if (newLabel != null && newLabel != '') {
                this.labelElement.classList.remove('empty-label');
            } else {
                this.labelElement.classList.add('empty-label');
            }
        }
    }

    processAddedComponents(change) {
        if (change.addedComponents) {
            Object.keys(change.addedComponents).forEach(function (afterId) {
                let referenceNode = null;
                if (afterId === '-') {
                    referenceNode = this.contentWrapper.firstChild || null;
                } else if (this.findComponent(afterId)) {
                    let elem = this.findComponent(afterId).htmlElement;
                    referenceNode = typeof elem !== 'undefined' ? elem.nextSibling || null : null;
                }

                change.addedComponents[afterId].forEach(function (componentObj) {
                    let component = this.findComponent(componentObj.id);
                    if (component instanceof HTMLFormComponent) {
                        if (referenceNode) {
                            if (component.htmlElement.id != referenceNode.id) {
                                this.contentWrapper.insertBefore(component.htmlElement,
                                    referenceNode);
                            }
                            let thisNodeComponent = this.findComponent(component.htmlElement.id);
                            if (thisNodeComponent.htmlElement &&
                                thisNodeComponent.htmlElement.nextSibling) {
                                referenceNode = thisNodeComponent.htmlElement.nextSibling;
                            }
                        } else {
                            this.contentWrapper.appendChild(component.htmlElement);
                        }
                    } else if (referenceNode) {
                        component.referenceNode = referenceNode.nextSibling || null;
                    }
                }.bind(this));
            }.bind(this));
        }
    }

    updateModel() {
        this.rawValue = this.component.value;
    }

    public accessibilityResolve(node: HTMLElement, access: string) {
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
                } else {
                    node.removeAttribute('disabled');
                }
                node.classList.add('fc-editable');
                break;
            default:
                if (access !== 'EDIT') {
                    node.classList.add('fc-disabled');
                    node.classList.add('disabled');
                } else {
                    node.classList.add('fc-editable');
                }
        }
    }

    setAccessibility(accessibility) {
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
                    } else {
                        this.htmlElement.classList.add('d-none');
                        this.hideHint();
                    }
                }
                break;
            case 'DEFECTED':
                this.accessibilityResolve(this.component, 'DEFECTED');
                this.component.title = 'Broken control';
                break;
        }

        this.accessibility = accessibility;
    }

    setPresentationStyle(presentationStyle) {
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
    }

    protected getMainComponent() {
        return this.component;
    }


    addStyles() {
        this.handleHeight();
        this.resolveLabelPosition();
        this.addAlignStyles();
        this.handlemarginAndPAddingStyles();
    }

    public hasHeight(): boolean {
        return this.componentObj.height != undefined && this.componentObj.height != '' && this.componentObj.height != null;
    }

    handleHeight() {
        if (this.componentObj.height != undefined) {
            let height = this.componentObj.height;
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
    }

    addAlignStyles() {
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
    }

    resolveLabelPosition() {
        if (this.componentObj.labelPosition != undefined) {
            let labelPosition = this.componentObj.labelPosition.toLowerCase();
            if (labelPosition != 'up') {
                $(this.component).closest('.fc.wrapper').addClass('positioned');
                $(this.component).closest('.fc.wrapper').addClass(labelPosition);
            }
            if (labelPosition === 'left' || labelPosition === 'right') {
                this.resolveInputSize();
            }
        }
    }

    setInputAndLabelPosition(property: string, labelElement: HTMLElement, inputElement: HTMLElement) {
        if (property.toString().indexOf('px', property.length - 2) !== -1) {
            if (labelElement !== null) {
                labelElement.style.width = property;
            }

            let group = this.htmlElement.querySelector('.input-group');
            if (group != null) {
                inputElement = group;
            }

            if (inputElement) {
                inputElement.classList.add('stretched');
                inputElement.style.width = 'calc(100% - ' + property + ')';
                //Prevent oversized input-group elemtns.
                inputElement.style.alignSelf = 'flex-start';
            }
        } else {
            let labelWidth = Math.max(0, Math.min(99, parseInt(property)));
            labelElement.style.width = labelWidth + '%';

            let inputWidth = 100 - labelWidth;
            if (inputElement) {
                inputElement.style.width = inputWidth + '%';
            }
        }
    }

    resolveInputSize() {
        if (this.componentObj.labelSize != undefined) {
            if (this.componentObj.labelPosition == 'LEFT') {
                this.setInputAndLabelPosition(
                    this.componentObj.labelSize,
                    this.htmlElement.querySelector('.col-form-label'),
                    this.getQueryForInputSize()
                );
            } else if (this.componentObj.labelPosition == 'RIGHT') {
                this.setInputAndLabelPosition(
                    this.componentObj.labelSize,
                    this.htmlElement.querySelector('.col-form-label'),
                    this.getQueryForInputSize()
                );
            }
            return; // jesli labelSize jest zdefiniowane, to inputSize nie dziala
        }
        if (this.componentObj.inputSize != undefined) {
            let inputWidth = Math.max(0, Math.min(99, parseInt(this.componentObj.inputSize)));
            let labelWidth = 100 - inputWidth;
            let label = this.htmlElement.querySelector('.col-form-label');
            if (label) {
                label.style.width = labelWidth + '%';
            }
            let input = this.getQueryForInputSize();
            if (input) {
                input.style.width = inputWidth + '%';
            }
        }
    }

    getQueryForInputSize() {
        if (this.inputGroupElement != null) {
            return this.htmlElement.querySelector('.input-group');
        } else {
            return this.component.querySelector('.form-control');
        }
    }

    enableStyleClasses() {
        if (this.styleClasses.length && this.styleClasses[0] != '') {
            this.styleClasses.forEach(function (cssClass) {
                if (cssClass) {
                    this.component.classList.add(cssClass);
                }
            }.bind(this));
        }
    }

    setWrapperWidth(wrapper: HTMLDivElement, oldWidth: string[], newWidth: string[]) {
        if (oldWidth) {
            oldWidth.forEach(function (width) {
                if (HTMLFormComponent.bootstrapColRegexp.test(width)) {
                    //In bootstrap 4 "co-xs-12" was replaced with "col-12" so we need to delete it from string.
                    wrapper.classList.remove('col-' + width.replace('xs-', '-'));
                } else if (HTMLFormComponent.bootstrapColWidthRegexp.test(width)) {
                    wrapper.classList.remove('exactWidth');
                    wrapper.style.width = undefined;
                } else {
                    console.error(`Invalid width '${width}' for component '${this.id}'.`);
                }
            }.bind(this));
        }

        newWidth.forEach(function (width) {
            if (HTMLFormComponent.bootstrapColRegexp.test(width)) {
                //In bootstrap 4 "co-xs-12" was replaced with "col-12" so we need to delete it from string.
                wrapper.classList.add('col-' + width.replace('xs-', '-'));
            } else if (HTMLFormComponent.bootstrapColWidthRegexp.test(width)) {
                wrapper.classList.add('exactWidth');
                wrapper.style.width = width;
            } else {
                console.error(`Invalid width '${width}' for component '${this.id}'.`);
            }
        }.bind(this));
    }

    protected wrap(skipLabel: boolean = false, isInputElement: boolean = false) {
        let wrappedComponent = this.innerWrap();
        let wrapper = document.createElement('div');
        ['fc', 'wrapper'].forEach(function (cssClass) {
            wrapper.classList.add(cssClass);
        });

        this.wrapInner(wrapper, wrappedComponent, skipLabel, isInputElement);
    }

    protected wrapInner(wrapper, wrappedComponent, skipLabel: boolean = false, isInputElement: boolean = false) {
        if (this.width) {
            // @ts-ignore
            this.setWrapperWidth(wrapper, undefined, this.width);
        } else {
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

            const labelValue = this.fhml.resolveValueTextOrEmpty(this.componentObj.label);
            if (labelValue.length > 0) {
                let label = document.createElement('label');
                label.classList.add('col-form-label');
                label.classList.add('align-items-end');
                // label.classList.add('card-title');
                label.innerHTML = labelValue;
                label.id = this.id + "_label";

                label.setAttribute('for', this.componentObj.id);
                //this.component.setAttribute('aria-describedby', label.id);

                wrapper.appendChild(label);
                this.labelElement = label;

                if (!isInputElement) {
                    this.component.setAttribute("aria-labelledby", label.id)
                } else {
                    label.htmlFor = this.id;
                }

            } else {
                this.processAriaLabel();
            }
        } else {
            this.processAriaLabel();
        }

        if (isInputElement) {
            let inputGroup = document.createElement('div');
            inputGroup.classList.add('input-group');
            wrapper.appendChild(inputGroup);
            inputGroup.appendChild(wrappedComponent);

            this.inputGroupElement = inputGroup;
        } else {
            wrapper.appendChild(wrappedComponent);
        }

        if (this.inlineStyle) {
            this.component.setAttribute('style', this.inlineStyle);
        }

        if (this.wrapperStyle) {
            let existingStyleClasses = wrapper.getAttribute('style') || '';
            wrapper.setAttribute('style', existingStyleClasses + this.wrapperStyle);
        }

        if (this.push && this.push == true) {
            wrapper.classList.add('mr-auto');
        }

        this.processHtmlAccessibilityRole();
        this.htmlElement = wrapper;
        this.contentWrapper = this.component;

    }

    protected innerWrap() {
        return this.component;
    }

    showToolbox() {
        // this.toolbox.classList.remove('d-none');
    }

    hideToolbox() {
        // this.toolbox.classList.add('d-none');
    }

    public focusCurrentComponent(deferred, options) {
        if (this.designMode) {
            let form = document.getElementById(this.formId);
            let activeComponents = form.querySelectorAll('.designerFocusedElement');
            let isUserAgentIE = this.fh.isIE();
            if (activeComponents.length) {

                if (isUserAgentIE && !NodeList.prototype.forEach) {
                    NodeList.prototype.forEach = function (callback, thisArg) {
                        thisArg = thisArg || window;
                        for (let i = 0; i < this.length; i++) {
                            callback.call(thisArg, this[i], i, this);
                        }
                    };
                }

                activeComponents.forEach(element => {
                    element.classList.remove('designerFocusedElement');
                    element.classList.remove('colorBorder');
                });
            }

            if (!this.htmlElement.classList.contains('colorBorder') && options.isLast) {
                if (this.componentObj.type === 'DropdownItem' || this.componentObj.type === 'ThreeDotsMenuItem') {
                    let dropdown = this.component.closest('.fc.dropdown').parentElement;
                    dropdown.classList.add('colorBorder');
                    dropdown.classList.add('designerFocusedElement');
                } else {
                    this.htmlElement.classList.add('colorBorder');
                }

                this.animateScroll(options);
            }

            if (!this.htmlElement.classList.contains('designerFocusedElement') || !this.htmlElement.classList.contains('colorBorder')) {
                this.htmlElement.classList.add('designerFocusedElement');
                this.htmlElement.classList.add('colorBorder');
            }

            this.highlightDesignerElementTree();
        } else {
            if (this.focusableComponent != null && this.focusableComponent.focus) {
                this.focusableComponent.focus();
            } else {
                if (options.isLast) {
                    this.animateScroll(options);
                }
            }
        }
        deferred.resolve(options);
        return deferred.promise();
    }

    setRequiredField(isRequired) {
        if (isRequired) {
            if (this.requiredElement !== null) {
                return;
            }

            if (this.componentObj.type === 'RadioOption' || this.componentObj.type === 'RadioOptionsGroup' || this.componentObj.type === 'CheckBox') {
                let divRequired = document.createElement('div');
                divRequired.classList.add('requiredFieldWrapper');
                let spanRequired = document.createElement('span');
                spanRequired.classList.add('requiredField');

                let iconRequired = document.createElement('i');
                iconRequired.classList.add('fas');
                iconRequired.classList.add('fa-star-of-life');

                spanRequired.appendChild(iconRequired);
                divRequired.appendChild(spanRequired);

                this.requiredElement = divRequired;

                let label = this.htmlElement.firstChild;
                let controlLabelWithText = label.innerText.length;

                if (controlLabelWithText) {
                    label.appendChild(this.requiredElement);
                } else {
                    this.htmlElement.appendChild(this.requiredElement);
                }
            } else {
                let iconRequired = document.createElement('i');
                iconRequired.classList.add('fas');
                iconRequired.classList.add('fa-star-of-life');
                iconRequired.style.fontSize = '0.5em';

                let spanRequired = document.createElement('span');
                spanRequired.classList.add('input-group-text');
                spanRequired.classList.add('input-required');
                spanRequired.style.paddingLeft = '0.5rem';
                spanRequired.style.paddingRight = '0';
                spanRequired.style.background = 'transparent';
                spanRequired.style.border = 'transparent';
                spanRequired.appendChild(iconRequired);

                let divRequired = document.createElement('div');
                divRequired.classList.add('input-group-append');
                divRequired.appendChild(spanRequired);

                this.requiredElement = divRequired;
                
                if (this.labelElement != null) {
                    this.labelElement.appendChild(this.requiredElement);
                } else if (this.component.classList.contains('field-required')) {
                    this.component.appendChild(this.requiredElement);
                } else {
                    this.htmlElement.appendChild(this.requiredElement);
                }
            }

        } else {
            if (this.requiredElement === null) {
                return;
            }

            if (this.componentObj.type === 'RadioOption' || this.componentObj.type === 'RadioOptionsGroup' || this.componentObj.type === 'CheckBox') {
                let label = this.htmlElement.firstChild;
                let controlLabelWithText = label.innerText.length;
                try {
                    if (controlLabelWithText) {
                        label.removeChild(this.requiredElement);
                    } else {
                        this.htmlElement.removeChild(this.requiredElement);
                    }
                } catch (e) {
                    this.requiredElement.remove();
                }

            } else {

                try {
                    if (this.labelElement != null) {
                        this.labelElement.removeChild(this.requiredElement);
                    } else if (this.component.classList.contains('field-required')) {
                        this.component.removeChild(this.requiredElement);
                    } else {
                        this.htmlElement.removeChild(this.requiredElement);
                    }
                }catch (e) {
                    this.requiredElement.remove();
                }
            }

            this.requiredElement = null;
        }
    }

    extractChangedAttributes() {
        if (this.changesQueue) {
            return this.changesQueue.extractChangedAttributes();
        }
    }


    __(string, node = undefined, args = undefined) {
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
    }

    changeLanguage(code) {
        for (let i = 0; i < this.translationItems.length; i++) {
            let item = this.translationItems[i];

            $(item.element).text(this.i18n.__(item.string, item.args, code));
        }
    }

    public getDefaultWidth(): string {
        return 'md-12';
    }

    getAdditionalButtons(): AdditionalButton[] {
        return [];
    }

    animateScroll(options) {
        options.scrollableElement = options.scrollableElement || 'html, body';

        if (this.component.localName === 'th') {
            let parentTable = this.component.closest('table').id;
            $(options.scrollableElement).animate({
                scrollTop: $('#' + parentTable).offset().top - 160
            });
        } else if (this.type === 'IMapLite') {
            let row = this.component.closest('.row');
            $(options.scrollableElement).animate({
                scrollTop: $(row).offset().top - 160
            });
        } else if (this.componentObj.type === 'DropdownItem' || this.componentObj.type === 'ThreeDotsMenuItem') {
            let dropdown = this.component.closest('.fc.dropdown');
            $(options.scrollableElement).animate({
                scrollTop: $(dropdown).offset().top - 160
            });
        } else {
            if ($('#' + this.id).length > 0) {
                $(options.scrollableElement).animate({
                    scrollTop: $('#' + this.id).offset().top - 160
                });
            }
        }
    }

    highlightDesignerElementTree() {

        // check if elementTree already contains highlighted elements
        // if yes, clear highlights -> we only want one at a time

        let designerElementTree = document.getElementById('designerElementTree');
        let highlightedElements = designerElementTree.querySelectorAll('.toolboxElementHighlight');

        if (highlightedElements.length) {
            highlightedElements.forEach(element => {
                element.classList.remove('toolboxElementHighlight');
            });
        }

        // check if formToolboxTools accordion is expanded and if so, collapse its elements first
        let toolboxElements = document.getElementById('toolboxFormTools');
        let toolboxBtns = toolboxElements.querySelectorAll('.collapseBtn');

        if (toolboxBtns) {
            toolboxBtns.forEach(button => {
                if (!button.classList.contains('collapsed')) {
                    button.setAttribute('aria-expanded', 'false');
                    button.classList.add('collapsed');
                }
            });
        }

        // check if designerElementTree is collapsed and if so, expand it first
        let collapseBtn = designerElementTree.querySelector('.btn');
        if (collapseBtn && collapseBtn.classList.contains('collapsed')) {
            collapseBtn.setAttribute('aria-expanded', 'true');
            collapseBtn.classList.remove('collapsed');
            designerElementTree.querySelector('.collapse').classList.add('show');
        }

        // verify event source and set elementTreeEquivalent accordingly
        let focusEventData = this.formsManager.firedEventData;
        let sourceElement = focusEventData.eventSourceId;
        let elementTreeEquivalent;

        if (focusEventData.containerId === 'formDesignerToolbox') {
            elementTreeEquivalent = document.getElementById(sourceElement);
        } else {
            elementTreeEquivalent = designerElementTree.querySelector('li[data-designer_element_equivalent=' + sourceElement + ']');
        }

        if (elementTreeEquivalent !== null) {
            let treeNode = elementTreeEquivalent.querySelector('.treeNodeBody');
            if (treeNode !== null) {
                treeNode.classList.add('toolboxElementHighlight');
                this.updateDesignerElementTree(focusEventData, elementTreeEquivalent);
            }
        }
    }

    updateDesignerElementTree(focusEventData, elementTreeEquivalent) {
        let treeElementsList = Array.from(document.getElementById('designerElementTree').querySelector('.treeElementList').children);
        let topLevelNode;
        let hiddenNodes;
        let nodeCarets;

        treeElementsList.forEach(node => {
            if (node.contains(elementTreeEquivalent)) {
                topLevelNode = node;
            }
        });

        if (topLevelNode) {
            hiddenNodes = topLevelNode.querySelectorAll('ul.d-none');
            nodeCarets = topLevelNode.querySelectorAll('.treeNodeBody');

            // expand only nodes that contain elementTreeEquivalent
            hiddenNodes.forEach(node => {
                if (node.children.length && node.contains(elementTreeEquivalent)) {
                    node.classList.remove('d-none');
                }
            });

            // update nodes carets that contain elementTreeEquivalent
            nodeCarets.forEach(caret => {
                if (caret.firstChild.classList.contains('fa-caret-right')) {

                    let nodeList = caret.parentElement;
                    if (nodeList && !nodeList.nextElementSibling.classList.contains('d-none')) {
                        caret.firstChild.classList.remove('fa-caret-right');
                        caret.firstChild.classList.add('fa-caret-down');
                    }
                }
            });
        }

    }

    /**
     * Function that handle adding margin and paddings to component styles.
     * @return string
     */
    public handlemarginAndPAddingStyles(): void {

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
    }

    /**
     * Function process width string from backend serwer and creates proper bootstrap classes string array so they can be added to component.
     * @param width
     */
    private handleWidth(width: string = this.componentObj.width) {
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
    }

    /**
     * Logic moved to function so it can be overrided by children classes.
     */
    protected buildDesingerToolbox() {
        (<any>FhContainer.get('Designer')).buildToolbox(this.getAdditionalButtons(), this);

    }

    private handleDeleteBtnEvent(event) {
        event.stopImmediatePropagation();
        let keyCode = event.keyCode;
        if (keyCode === 46) { // keyborad delete keycode
            let deleteBtn = document.getElementById('designerDeleteFormElement');
            let focusedElement = document.querySelector('.designerFocusedElement');
            if (!focusedElement) {
                console.error('%cElement to be removed was not found.', 'background: #cc0000; color: #FFF');
                return;
            } else {
                /**
                 * We need to check if delete button was pressed on for deleting focusedelement.
                 * To do this we get activeElement (element that has current gloabl focus) and check if element is html input/select.
                 * If it is select or input or textarea and it is not inside current edited form we should prevent delete.
                 */
                const activeElement = document.activeElement;
                const activeElementTagName = activeElement.tagName.toLowerCase();
                if (focusedElement.contains(activeElement) || (!focusedElement.contains(activeElement) &&
                    (activeElementTagName !== 'input' &&
                        activeElementTagName !== 'select' &&
                        activeElementTagName !== 'textarea' &&
                        !activeElement.classList.contains('form-control')))) {
                    deleteBtn.click();
                }
            }
        }
    }

    /**
     * Ads autocomplete attribiute to input element i both exists on element.
     */
    protected processAutocomplete(value: string = this.autocomplete) {
        if (this.autocomplete && this.input) {
            this.input.setAttribute('autocomplete', value);
        }
    }

    /**
     * Ads aria-label attribiute to element.
     */
    protected processAriaLabel(value: string = this.ariaLabel) {
        //Add attribute only when there is no label on component.
        if (this.ariaLabel && !this.labelElement) {
            if (this.input) {
                this.input.setAttribute('aria-label', value);
            } else {
                this.component.setAttribute('aria-label', value);
            }
        }
    }

    /**
     * Ads role attribiute to element.
     */
    protected processHtmlAccessibilityRole(value: string = this.htmlAccessibilityRole) {
        if (this.htmlAccessibilityRole) {
            this.component.setAttribute('role', value);
        }
    }

    /**
     * Function that close all bootstrap hints inside component based on css class
     */
    public hideAllHints(){
        if(this.component) {
            $(this.component).find(".fh-tooltip").each(function( index ) {
                try {
                    $(this).tooltip('hide');
                }catch (e) {}
            });
            $(this.component).find(".fh-popover").each(function( index ) {
                try {
                    $(this).popover('hide');
                }catch (e) {}
            })
        }
    }

}

export {HTMLFormComponent};
