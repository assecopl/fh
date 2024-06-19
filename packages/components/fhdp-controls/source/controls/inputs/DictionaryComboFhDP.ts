import {createElement} from 'react';
import * as ReactDOM from 'react-dom';
import {HTMLFormComponent, LanguageChangeObserver} from 'fh-forms-handler';
import {LanguageResiterer} from './../helpers/LanguageResigerer';
import {ComboFhDP} from './ComboFhDP';
import { DictionaryComboFhDPHelper } from '../helpers/DictionaryComboFhDPHelper';
import { DictionaryComboFhDPPopperTable } from './DictionaryComboFhDPPopperTable';

class DictionaryComboFhDP extends ComboFhDP implements LanguageChangeObserver {
    private instance : any;
    private divTooltipId: any;
    private divTooltip: any;

    private title: string;
    private columns: any[];
    private rows: any[];
    private popupOpen: boolean;
    private searchRequested: boolean;
    private page: number;
    private pagesCount: number;
    private isSearch: boolean = true;
    private popupColor?: string;
    private dirty: boolean = false;
    private languageWrapped: any;
    private valueFromChangedBinding: any;
    private displayOnlyCode: boolean;

    private _writingDebaunceTimer: any;
    private pageChangeClicked: boolean = false;
    private clickInPopup: boolean = false;

    public guuid: string;
    public popperId: string;


    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);
        this.instance = null;
        this.divTooltipId = null;
        this.divTooltip = null;
        this.title = this.fhml.resolveValueTextOrEmpty(this.componentObj.title);
        this.columns = this.componentObj.columns || [];
        this.rows = this.componentObj.rows || [];
        this.popupOpen = false;
        this.page = this.componentObj.page;
        this.searchRequested = this.componentObj.searchRequested;
        this.pagesCount = this.componentObj.pagesCount;
        this.displayOnlyCode = this.componentObj.displayOnlyCode;
        // this.dirty = this.componentObj.dirty;
        // console.log('dirty on create', this.componentObj.dirty)
        this.dirty = false;
        this.languageWrapped = this.componentObj.language || null;
        if (this.componentObj.valueFromChangedBinding) {
            this.valueFromChangedBinding = this.componentObj.valueFromChangedBinding;
        }
        if (this.componentObj.background) {
            this.popupColor = this.componentObj.popupColor;
        }

        LanguageResiterer.getInstance(this.i18n).registerLanguags(this);

        this.popperId = "dictionary-combo-popper-" + Date.now();

    }

    create() {
        let input = document.createElement('input');
        this.input = input;
        input.id = this.id;
        input.type = 'text';
        input.value = this.rawValue;
        input.autocomplete = 'off';
        ['fc', 'form-control'].forEach(function (cssClass) {
            input.classList.add(cssClass);
        });
        if (this.placeholder) {
            input.placeholder = this.placeholder;
        }

        this.keySupport.addKeyEventListeners(input);

        input.addEventListener('input', function () {
            this.selectedIndexGroup = null;
            this.selectedIndex = null;
            this.removedIndex = null;
            this.updateModel();
        }.bind(this));
        $(input).on('paste', function (event) {

            event.stopPropagation();
            if (this.accessibility !== 'EDIT') {
                return;
            }
            this.updateModel();
            if (this.onChange && (this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue)) {
                this.fireEventWithLock('onChange', this.onChange);
                this.changeToFired = false
            }
        }.bind(this));
        // $(input).on('keydown', function (event) {
        //
        //     if (this.accessibility !== 'EDIT') {
        //         return;
        //     }
        //     let keyCode = event.which;
        //     let options = this.autocompleter.querySelectorAll('li:not(.dropdown-header)');
        //     if (keyCode === 9 || keyCode === 13) {
        //         let shouldBlur = false;
        //         if (this.highlighted != null) {
        //             shouldBlur = true;
        //             let element = options[this.highlighted].firstChild;
        //             this.selectedIndexGroup = element.dataset.group;
        //             this.selectedIndex = parseInt(element.dataset.index);
        //             this.forceSendSelectedIndex = true;
        //             const val = this.values[this.selectedIndexGroup][this.selectedIndex];
        //             if (val) {
        //                 this.input.value = val.displayAsTarget ? val.targetValue : val.displayedValue;
        //             }
        //             if (element.dataset.targetCursorPosition !== undefined) {
        //                 this.setCursorPositionToInput(parseInt(element.dataset.targetCursorPosition));
        //                 shouldBlur = false;
        //             }
        //         }
        //         this.updateModel();
        //         if (this.onChange && (this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue || this.changeToFired)) {
        //             this.fireEventWithLock('onChange', this.onChange);
        //             this.changeToFired = false;
        //         }
        //         if (shouldBlur) {
        //             this.blurEventWithoutChange = true;
        //             this.input.blur(); // must be after onChange
        //             this.input.focus();
        //         }
        //
        //
        //     } else {
        //         let move = 0;
        //         switch (keyCode) {
        //             case 40: // down arrow
        //                 move = 1;
        //                 break;
        //             case 38: // up arrow
        //                 move = -1;
        //                 break;
        //         }
        //         if ((keyCode === 40 || keyCode === 38) && !this.autocompleter.classList.contains('isEmpty')) {
        //             let current = options[this.highlighted];
        //             if (current) {
        //                 current.classList.remove('selected');
        //             }
        //             if (this.highlighted === null) {
        //                 if (move === 1) {
        //                     this.highlighted = 0;
        //                 } else {
        //                     this.highlighted = options.length - 1;
        //                 }
        //             } else {
        //                 this.highlighted = this.highlighted + move;
        //             }
        //             if (this.highlighted <= 0) {
        //                 this.highlighted = this.highlighted + options.length;
        //             }
        //             this.highlighted = this.highlighted % options.length;
        //             options[this.highlighted].classList.add('selected');
        //             this.autocompleter.scrollTop = options[this.highlighted].offsetTop;
        //         }
        //         if (this.multiselect && keyCode == 8 && $(this.input).val() === '' && this.tagslist.length > 0) {
        //             event.preventDefault();
        //             var lastTag = this.tagslist[this.tagslist.length - 1];
        //             this.removeTag(encodeURI(lastTag), {});
        //             $(this.input).trigger('focus');
        //             this.openAutocomplete();
        //         }
        //     }
        // }.bind(this));
        if (this.onInput) {
            input.addEventListener('input', function () {

                if (this.accessibility === 'EDIT') {
                    if (!this.openOnFocus) {
                        this.openAutocomplete();
                    }
                    this.onInputEvent();
                }
            }.bind(this));
        }
        // let specialKeyCapture = function (fireEvent, event) {
        //     if (event.ctrlKey && event.which === 32 && this.accessibility === 'EDIT') {
        //         event.stopPropagation();
        //         event.preventDefault();
        //
        //         let doubleSepcialKey = this.onDblSpecialKey && this.input && this.input.value == this.rawValueOnLatSpecialKey && this.input.selectionStart == this.cursorPositionOnLastSpecialKey;
        //         if (fireEvent) {
        //             if (!doubleSepcialKey) {
        //                 this.openAutocomplete();
        //                 this.rawValueOnLatSpecialKey = this.input.value;
        //                 this.cursorPositionOnLastSpecialKey = this.input.selectionStart;
        //                 if (this.onSpecialKey) {
        //                     this.fireEventWithLock('onSpecialKey', this.onSpecialKey);
        //                 }
        //             } else if (doubleSepcialKey) {
        //                 this.rawValueOnLatSpecialKey = null;
        //                 this.cursorPositionOnLastSpecialKey = null;
        //                 if (this.onDblSpecialKey) {
        //                     this.changeToFired = false;
        //                     this.fireEventWithLock('onDblSpecialKey', this.onDblSpecialKey);
        //                 }
        //             }
        //         }
        //     }
        // };
        // if (this.onSpecialKey || this.onDblSpecialKey) {
        //     input.addEventListener('keyup', specialKeyCapture.bind(this, true)); // firing event
        //     input.addEventListener('keydown', specialKeyCapture.bind(this, false)); // not firing event, just stop propagation
        //     input.addEventListener('input', specialKeyCapture.bind(this, false)); // not firing event, just stop propagation
        // }

        input.addEventListener('focus', function () {

            if (this.accessibility === 'EDIT') {
                this.onInputEvent();
                if (this.openOnFocus) {
                    this.openAutocomplete();
                }
                this.autocompleterFocus = false;

            }
        }.bind(this));
        input.addEventListener('blur', function (event) {

            //For IE11. Check if event was fired by action on dropdown element.
            if (!this.autocompleterFocus) {
                this.closeAutocomplete();

                if (this.multiselect) {
                    this.addTag(this.input.value);
                }
            } else {
                //IE11 Put back focus on input.
                // this.input.focus();
                this.autocompleterFocus = false;
                return false;
            }


        }.bind(this));
        if (this.onChange) {

            $(input).on('blur', function (event) {

                if (this.accessibility === 'EDIT' && !this.blurEventWithoutChange && (this.changeToFired || this.rawValue !== this.oldValue || this.multiselectRawValue !== this.multiselectOldValue || this.forceSendSelectedIndex)) {
                    this.blurEvent = true;
                    this.fireEventWithLock('onChange', this.onChange);
                    this.changeToFired = false;
                }
                this.blurEventWithoutChange = false;
            }.bind(this));
        }


        let autocompleter = document.createElement('ul');
        ['autocompleter', 'dropdown-menu', 'fc', 'combo'].forEach(function (cssClass) {
            autocompleter.classList.add(cssClass);
        });
        autocompleter.id = this.id + '_autocompleter';


        this.autocompleter = autocompleter;

        this.component = this.input;
        this.focusableComponent = input;
        this.hintElement = this.component;

        this.wrap(false, true);
        this.createIcon();


        this.createClearButton();
        this.setRequiredField(false);

        this.inputGroupElement.appendChild(autocompleter);
        this.addStyles();
        this.display();
        if (this.isLastValueEnabled !== false) {
            this.createLastValueElement();
        }

        if (this.component.classList.contains('servicesListControl')) {
            this.htmlElement.classList.add('servicesListControlWrapper');
        }


        this.setValues(this.values);


        let self = this;

        let inputGroup = this.getInputGroupElement();
        let iconSearch = this.getIconClass();

        self.divTooltipId = Date.now();
        self.divTooltip = document.createElement('div');
        self.divTooltip.classList.add('dc-element');

        self.divTooltip.classList.add('hidden-popper');
        self.divTooltip.id = self.divTooltipId.toString();

        const id = `FhDP-dictionary-combo-${+new Date()}`;
        this.getInputGroupElement().id = id
        this.guuid = id;

        this.getInputGroupElement().appendChild(self.divTooltip);

        this.i18n.subscribe(this);

        DictionaryComboFhDPHelper.getInstance().registerElement(this);

        this.display();
        document.getElementById(this.input.id).addEventListener('change', this.handleRawDataChange.bind(this));
        document.getElementById(this.input.id).addEventListener('keydown', this.handleTextInputChange.bind(this));
        this.input.addEventListener('blur', this.handleTextInputBlur.bind(this));

        if(iconSearch != null){
            $($("div.search-icon", inputGroup)[0]).addClass('fc-editable');
            $($("div.search-icon", inputGroup)[0]).on("click", function(){
                if(self.accessibility === "VIEW"){//w trybie VIEW nie działa onClickSearchIcon
                    self.isSearch = true;
                    self.crateTooltip($("div.search-icon", self.getInputGroupElement())[0]);
                }
            })
        }
        this.input.addEventListener('keydown', (ev) => {

            let code = ev.keycode || ev.which;
            if (code == 9 && this.popupOpen) { //Tab
                ev.preventDefault();
                ev.stopPropagation();
                const popper = $("#" + this.popperId);
                const focusableElement = popper.find('button:not([disabled]),span[tabindex=0]').first();
                setTimeout(function () {
                    focusableElement.focus();
                }, 1);
            }
            if (code == 40 && !this.popupOpen) { //Arrow down
                ev.preventDefault();
                ev.stopPropagation();
                this.popupOpen = true;
                this.isSearch = true;
                this.fireEvent('onClickSearchIcon', 'search');
                this.renderPopup();

            }

            if (ev.key === "Escape") {
                this.changesQueue.queueAttributeChange('searchRequested', true);
                this.popupOpen = false;
                this.isSearch = true;
                this.fireEvent('onClickSearchIcon', 'search');
                this.renderPopup();
            }
        })
        let clearBtn = $("div.clearButton", self.getInputGroupElement())[0];
        if (this.accessibility !== 'VIEW') {
            if (clearBtn) {
                $(clearBtn).addClass('fc-editable');
                clearBtn.addEventListener('click', (ev) => {
                    this.input.value = "";
                    this.rawValue = null;
                    this.changesQueue.queueAttributeChange('text', null);
                    this.changesQueue.queueAttributeChange('cleared', true);
                    this.fireEventWithLock('cleanupSearch', 'cleanupSearch');
                    // this.fireEventWithLock('recordSelected', null);
                    if(this.popupOpen){
                        this.onClickSearchIconEvent(ev);
                    }
                    this.input.focus();
                });
            }
        } else {
            if (clearBtn) {
                clearBtn.style.display = 'none';
            }
        }

        if(this.htmlElement.querySelector("span.input-old-value") != null) {
            this.htmlElement.querySelector("span.input-old-value").classList.add('fc-editable');
            this.htmlElement.querySelector("span.input-old-value").addEventListener('click', this.onClickEvent.bind(this));
        }
        if(this.htmlElement.querySelector("div.search-icon") != null) {
            this.htmlElement.querySelector("div.search-icon").addEventListener('click', this.onClickSearchIconEvent.bind(this));
        }

        if (this.rawValue === undefined || this.rawValue === 'null' || this.rawValue === null || this.rawValue === '') {
            this.input.value = '';
        }
        if(this.accessibility == 'EDIT') {
            this.fireEvent('setGuuid', JSON.stringify({id: this.guuid}));
        }

    }

    validated(result: boolean) {
        if (result) {
            this.unmarkDirty();
            this.clickInPopup = false;
            this.popupOpen = false;

            this.fireEventWithLock('recordSelected', this.rawValue);
            if(window['handlePopupClose']) {
                window['handlePopupClose'](true);
            }
            this.fireEventWithLock('onChange', this.onChange);
        } else {
            if ((this.clickInPopup && this.popupOpen) || (this.pageChangeClicked && this.popupOpen)) {
                this.clickInPopup = false;
                this.pageChangeClicked = false;
            } else {
                /**
                 * Threre is no need on always open popup when it doeasn't pass validation.
                 */

                // this.changesQueue.queueAttributeChange('searchRequested', true);
                // if (!this.popupOpen) {
                //     this.popupOpen = true;
                // }
                // this.isSearch = true;
                // this.fireEvent('onClickSearchIcon', 'search');
                // this.crateTooltip($("div.search-icon", this.getInputGroupElement())[0]);
            }
            this.getInputGroupElement().style.border = 'solid red 1px';
        }
    }

    markDirty() {
        this.dirty = true;
        this.changesQueue.queueAttributeChange('dirty', this.dirty);
    }

    unmarkDirty() {
        this.dirty = false;
        this.clickInPopup = false;
        this.changesQueue.queueAttributeChange('dirty', this.dirty);
        this.getInputGroupElement().style.border = 'none';
    }

    handleRawDataChange(ev) {
        const content = ev.target.value;
        if (content === '') {
            this.rawValue = null;
        } else {
            this.rawValue = content;
        }
        this.changesQueue.queueAttributeChange('rawValue', this.rawValue);
        this.fireEventWithLock('onChange', this.onChange);
    }

    handleTextInputChange(ev) {

        function isCtrlV(ev: any) {
            let code = ev.keycode || ev.which;
            return code === 86 && ev.ctrlKey;
        }

        function isPrintableKey(ev: any) {
            let code = ev.keycode || ev.which;
            if(code >= 96 && code <= 105) return true;
            else if (code === 13 || code === 8) return true;
            else if(code === 46) return true;
            else if(ev.location > 0) return false;
            else if(code === 8) return true;
            else if(code < 48) return false;
            else if(code > 90) return false;
            else return true;
        }

        if(isPrintableKey(ev) || isCtrlV(ev)) {
            this.markDirty();
            if (this._writingDebaunceTimer) {
                clearTimeout(this._writingDebaunceTimer);
                this._writingDebaunceTimer = undefined;
            }

            const openSearch = () => {
                console.log(this.input, document.activeElement, this.input === document.activeElement)
                if (this.dirty && this.input === document.activeElement && !this.clickInPopup) {
                    if (ev.target.value === '') {
                        this.rawValue = null;
                    }
                    this.changesQueue.queueAttributeChange('searchRequested', true);
                    if (!this.popupOpen) {
                        this.popupOpen = true;
                    }
                    this.isSearch = true;
                    this.fireEvent('onClickSearchIcon', 'search');
                    this.crateTooltip($("div.search-icon", this.getInputGroupElement())[0])
                }
            }

            if (!this.popupOpen) {
                this._writingDebaunceTimer = setTimeout(openSearch, 800);
            }
        }

    }

    private setClickInPopup = (arg: boolean = true) => {
        this.clickInPopup = arg;
    }

    handleTextInputBlur(ev) {
        if (this.dirty && !this.clickInPopup) {
            if (ev.target.value === '' && this.rawValue) {
                this.fireEventWithLock('recordSelected', null);
                this.unmarkDirty();
            } else {
                this.fireEventWithLock('dictionaryComboValidate', JSON.stringify({id: this.guuid, code: ev.target.value}));
                ev.preventDefault();
            }
        } else if (this.clickInPopup) {
            this.clickInPopup = false;
            this.defferFunction(() => {
                document.getElementById(this.input.id).focus();
            });
        } else {
            this.getInputGroupElement().style.border = 'none';
        }
        ev.preventDefault();
    }

    async renderPopup() {
        const handlePopupClose = (force?: boolean) => {
            if (this.popupOpen || force) {
                if ((!this.dirty && !this.clickInPopup) || force) {
                    this.popupOpen = false;
                    this.renderPopup();
                    this.input.focus();
                }
            }
        }
        window['handlePopupClose'] = handlePopupClose;
        const formContainer = this.getFormFocusTrapElement();
        ReactDOM.render(createElement(DictionaryComboFhDPPopperTable, {
            title: this.title,
            columns: this.columns,
            rows: this.rows,
            readOnly: this.accessibility === "VIEW",
            hookElementId: this.getInputGroupElement().id,
            isOpen: this.popupOpen,
            currentPage: this.page,
            pagesCount: this.pagesCount,
            parent: this.getInputGroupElement(),
            backgroundColor: this.popupColor,
            position: this.isSearch ? 'left' : 'right',
            fireChangePopupEvent: (attr: {name: string, arg: any}[], event?: string) => {
                if (['nextPage', 'prevPage'].indexOf(event) > -1) {
                    this.clickInPopup = true;
                    this.pageChangeClicked = true;
                }
                for (const att of attr) {
                    this.changesQueue.queueAttributeChange(att.name, att.arg);
                    this[att.name] = att.arg;
                }
                this.fireEventWithLock(event || 'onChange', this.onChange);
            },
            handleClose: handlePopupClose,
            recordClick: (record: any) => {
                this.clickInPopup = true;
                if (this.dirty || this.input.value === '' || (this.input.value !== record.value && !this.input.value.startsWith(record.value))) {
                    this.input.value = record.value || '';
                    this.rawValue = record.value;
                    this.fireEventWithLock('recordSelected', record.value);
                    handlePopupClose(true);
                    this.fireEventWithLock('onChange', this.onChange);
                } else if (!this.dirty && (this.input.value === record.value || this.input.value.startsWith(record.value))) {
                    handlePopupClose(true);
                }
                this.input.focus();
                this.unmarkDirty();
            },
            clickInPopup: this.setClickInPopup,
            translate: (string: string, args?: any, code?: string) => this.i18n.translateString(string, args, code || this.languageWrapped),
            popperId: this.popperId,
            parentInput: this.input,
            container: formContainer
        }), this.divTooltip);
        this.clickInPopup = false;
    }

    crateTooltip(element){
        let allTooltips = document.getElementsByClassName("dc-element");

        if(allTooltips.length >0){
            for(var i=0; i< allTooltips.length;i++){
                allTooltips[i]
                if (!allTooltips[i].classList.contains("hidden-popper") && allTooltips[i].id != this.divTooltipId.toString()){
                    allTooltips[i].classList.add("hidden-popper");
                }
            }
        }
        this.popupOpen = true;
        if (element && this.accessibility === "VIEW") {
            const isSearch = $(element).is($($("div.search-icon", this.getInputGroupElement())[0]))
            const isOldValue = $(element).is($($("span.input-old-value", this.getInputGroupElement())[0]))

            if (isSearch) {
                this.isSearch = true;
                this.fireEvent('onClickSearchIcon', "search");
            }
            if (isOldValue) {
                this.isSearch = false;
                this.fireEvent('onClickLastValue', "search");
            }
        }
        this.renderPopup();
    }

    destroy(removeFromParent){
        ReactDOM.unmountComponentAtNode(this.divTooltip);
        if (this.popupOpen) {
            this.popupOpen = false;
            this.renderPopup();
        }
        if (this.instance) {
            this.instance.destroy();
            this.divTooltip.classList.add('hidden-popper');
            this.instance = null;
        }
        
        this.i18n.unsubscribe(this);
        super.destroy(removeFromParent);
    }

    languageChanged(code: string) {
        this.languageWrapped = code;
        if (this.popupOpen) {
            this.fireEvent('onClickSearchIcon', 'search');
            this.crateTooltip($("div.search-icon", this.getInputGroupElement())[0])
        }
        this.renderPopup();
    }

    update(change) {
        super.update(change);
        if (change.changedAttributes) {
            let shouldRender = false;
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'language':
                        this.languageWrapped = newValue;
                        if (this.popupOpen) {
                            shouldRender = true;
                        }
                        break;
                    case 'columns':
                        this.columns = newValue;
                        if (this.popupOpen) {
                            shouldRender = true;
                        }
                        break;
                    case 'valueFromChangedBinding':
                        if (newValue === 'null' || newValue === '') {
                            this.rawValue = null;
                            this.input.value = '';
                            this.unmarkDirty();
                            this.valueFromChangedBinding = newValue;
                        } else {
                            this.rawValue = newValue;
                            this.input.value = newValue || '';
                            this.unmarkDirty();
                            this.valueFromChangedBinding = newValue || '';
                        }
                        break;
                    case 'shouldCloseTooltip':
                        this.shouldCloseTooltip = newValue;
                        if(this.shouldCloseTooltip == true){
                            this.destroy();
                        }
                        break;
                    case 'text':
                        console.log('TEXT!', newValue);
                        break;
                    case 'title':
                        this.title = this.fhml.resolveValueTextOrEmpty(newValue);
                        if (this.popupOpen) {
                            shouldRender = true;
                        }
                        break
                    case 'displayedComponentId':
                        this.displayedComponentId = newValue;
                        break;
                    case 'pagesCount':
                        this.pagesCount = newValue;
                        if (this.popupOpen) {
                            shouldRender = true;
                        }
                        break;
                    case 'page':
                        this.page = newValue;
                        if (this.popupOpen) {
                            shouldRender = true;
                        }
                        break;
                    case 'searchRequested':
                        this.searchRequested = newValue;
                        shouldRender = true;
                        break;
                    case 'rows':
                        this.rows = newValue;
                        if (this.popupOpen) {
                            shouldRender = true;
                        }
                        break;
                    case 'accessibility':
                        if (this.accessibility != newValue && newValue == 'EDIT') {
                            this.fireEvent('setGuuid', JSON.stringify({id: this.guuid}));
                        }
                        break;
                }
            }.bind(this));
            if(shouldRender) {
                this.renderPopup();
            }
        }

    }

    updateModel() {
       super.updateModel();
        let theSameValue = this.rawValue == this.lastValue;
        this.toogleLastValueElement(theSameValue);
    }

    extractChangedAttributes() {

        let comboAttrs = super.extractChangedAttributes();

        return comboAttrs;
    };

    onClickEvent(event) {
        this.popupOpen = true;
        this.isSearch = false;
        this.changesQueue.queueAttributeChange('searchRequested', true);
        if(this.accessibility !== "VIEW") {
            if (this._formId === 'FormPreview') {
                this.fireEvent('onClickLastValue', "preview");
            } else {
                this.fireEventWithLock('onClickLastValue', "search");
            }
        }
        this.crateTooltip($("div.input-group-append > span.input-old-value", this.htmlElement)[0]);
        event.target.blur();
    }

    onClickSearchIconEvent(event) {
        event.stopPropagation();
        this.popupOpen = true;
        this.isSearch = true;
        if(this.accessibility !== "VIEW") {
            if (this._formId === 'FormPreview') {
                this.fireEvent('onClickSearchIcon', "preview");
            } else {
                this.fireEvent('onClickSearchIcon', "search");
            }
        }
        this.crateTooltip($("div.search-icon", this.getInputGroupElement())[0]);
        event.target.blur();
    }

    defferFunction(func) {
        setTimeout(func, 100);
    }
}

export {DictionaryComboFhDP}
