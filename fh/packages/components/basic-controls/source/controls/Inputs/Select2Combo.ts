import {InputText} from "./InputText";
import {HTMLFormComponent} from 'fh-forms-handler';
import "select2";
import {DataFormat, GroupedDataFormat} from "select2";

class Select2Combo extends InputText {
    protected emptyLabel: boolean = false;
    protected emptyLabelText: string = "";
    protected selectedIndex: number;
    protected removedIndex: number;
    protected cleared: boolean;
    protected multiselect: boolean;
    protected selectedIndexGroup: any;
    protected removedIndexGroup: any;
    protected multiselectRawValue: any;
    protected values: any;
    protected lastElementSelected: any;
    protected lastElementDeselected: any;

    constructor(componentObj: any, parent: HTMLFormComponent) {
        super(componentObj, parent);

        this.values = this.componentObj.filteredValues || [];
        this.emptyLabel = this.componentObj.emptyLabel || false;
        this.emptyLabelText = this.componentObj.emptyLabelText || "";
        this.selectedIndexGroup = componentObj.selectedGroupIndex || "";
        this.removedIndexGroup = componentObj.removedGroupIndex || "";
        this.multiselect = this.componentObj.multiselect || false;
        this.selectedIndex = componentObj.selectedItemIndex || null;
        this.removedIndex = null;
        this.multiselectRawValue = this.componentObj.multiselectRawValue || [];
        this.lastElementSelected = null;
        this.lastElementDeselected = null;
    }

    create() {
        let input = document.createElement('select');
        if (!this.multiselect) {
            let option = document.createElement('option');
            input.appendChild(option);
        }

        this.input = input;
        input.id = this.id;
        input.style.width = "100%";

        if (this.emptyLabel && (this.rawValue === "" || this.rawValue === null)) {
            input.value = this.emptyLabelText;
            this.rawValue = this.emptyLabelText;
            this.oldValue = this.emptyLabelText;
        } else {
            input.value = this.rawValue;
            this.oldValue = this.rawValue;
        }

        input.autocomplete = 'off';

        this.component = this.input;
        this.focusableComponent = input;

        this.wrap(false, true);
        this.hintElement = this.inputGroupElement;
        this.inputGroupElement.appendChild(this.input);

        this.addStyles();
        this.display();


        $(this.input).select2({
            width: 'resolve',
            multiple: this.multiselect,
            dropdownAutoWidth: true,
            theme: "bootstrap",
            data: this.prepareValues(),
            placeholder: this.emptyLabelText,
            allowClear: true,
            templateResult: function (state) {
                //Ukrywamy pustą opcje jezeli nie jest oczekiwana.
                if (!this.emptyLabel && state.id == -1) {
                    return null;
                } else {
                    return state.text;
                }
            }.bind(this),
            debug: false,
        });

        if (this.multiselect) {
            $(this.input).attr('multiple', 'multiple');
            this.selectMultiValues();
        }

        $(this.input).on('change.select2', this.onValueChange.bind(this));
        $(this.input).on('select2:selecting', function (e: any) {
            this.lastElementSelected = e.params.args.data;
        }.bind(this));
        $(this.input).on('select2:unselecting', function (e: any) {
            this.lastElementDeselected = e.params.args.data;
        }.bind(this));
        $(this.input).on('select2:clear', this.onClear.bind(this));
    }

    public onValueChange(e) {
        this.updateModel();
        this.getCurrentSelections();
        if (this.onChange && this.accessibility === 'EDIT') {
            this.fireEventWithLock('onChange', this.onChange);
        }

    }

    setAccessibility(accessibility) {
        if (accessibility !== 'HIDDEN') {
            this.htmlElement.classList.remove('d-none');
            this.htmlElement.classList.remove('invisible');
        }
        if (accessibility !== 'DEFECTED' || accessibility !== 'VIEW') {
            this.component.classList.remove('fc-disabled', 'disabled');
        }
        if (accessibility !== 'EDIT') {
            this.component.classList.remove('fc-editable');
        }

        switch (accessibility) {
            case 'EDIT':
                this.accessibilityResolve(this.component, 'EDIT');
                // this.autocompleter.classList.add('fc-editable');
                if (this.multiselect) {
                    // let tags = this.htmlElement.querySelectorAll(".tag-remove");
                    // if (tags.length > 0) {
                    //     tags.forEach((node) => this.accessibilityResolve(node, 'EDIT'));
                    // }
                }
                break;
            case 'VIEW':
                this.accessibilityResolve(this.component, 'VIEW');
                if (this.multiselect) {
                    // let tags = this.htmlElement.querySelectorAll(".tag-remove");
                    // if (tags.length > 0) {
                    //     tags.forEach((node) => this.accessibilityResolve(node, 'VIEW'));
                    // }
                }
                break;
            case 'HIDDEN':
                if (this.invisible) {
                    this.htmlElement.classList.add('invisible');
                } else {
                    this.hideHint();
                    this.htmlElement.classList.add('d-none');
                }
                break;
            case 'DEFECTED':
                this.accessibilityResolve(this.component, 'DEFECTED');
                this.component.title = 'Broken control';
                break;
        }

        this.accessibility = accessibility;
    }

    public getCurrentSelections() {
        const selections = $(this.input).select2('data');
        const selectionsIdx = [];

        selections.forEach(function (optionData) {
            selectionsIdx.push(optionData);
        });

        if (this.multiselect) {
            if (this.lastElementDeselected) {
                this.removedIndex = this.lastElementDeselected.targetId;
            } else if (this.lastElementSelected) {
                this.selectedIndex = this.lastElementSelected.targetId;
                this.selectedIndexGroup = this.lastElementSelected.groupId;
            }

            this.lastElementDeselected = null;
            this.lastElementSelected = null;
        } else { //Sprawdzamy czy wartosc jest pusta/wyczyszczona
            if (selections.length == 0) {
                //Wartosci ustawiona podczas czyszczenia this.onClear() , nie zmieniamy ich.
            } else {
                this.selectedIndex = selectionsIdx[0].targetId;
                //selectedIndexGroup must be send as String.
                this.selectedIndexGroup = selectionsIdx[0].groupId;

                this.lastElementSelected = null;
                this.lastElementDeselected = null;
            }
        }
    }

    public onClear(e) {
        this.selectedIndexGroup = "";
        this.selectedIndex = -1;
        this.cleared = true;
        this.lastElementSelected = -1;

        $(this.input).trigger("change");
    }

    findIndexByValue(targetValue: string) {
        const groups = Object.keys(this.values);

        if (groups.length == 1) {
            const group = 0;

            let result = -1;

            this.values[groups[group]].forEach(function (value, index) {
                if (value.targetValue == targetValue) {
                    result = index;
                }
            });

            return result;
        } else {
            //TODO Obsługa grup
        }
    }

    selectMultiValues() {
        const indexes: string[] = [];
        const groups = Object.keys(this.values);
        const values = this.multiselectRawValue && (typeof this.multiselectRawValue === "string") ? JSON.parse(this.multiselectRawValue) : [];

        if (groups.length == 1) {
            const group = 0;

            this.values[groups[group]].forEach(function (value, index) {
                if (value.targetValue == this.rawValue) {
                    this.selectedIndex = index;
                }
            }.bind(this));

            values.forEach(function (val) {
                this.values[groups[group]].forEach((value, index) => {
                    if (val === value.targetValue) {
                        indexes.push(value.targetId);
                    }
                });
            }.bind(this));

            $(this.input).val(indexes);
            $(this.input).trigger('change');
        } else {
            //TODO Obsługa grup
        }
    }

    prepareValues(): DataFormat | GroupedDataFormat | any {
        const data: DataFormat[] | any = []
        const groups = Object.keys(this.values);

        if (groups.length == 1) {
            const group = 0;

            this.values[groups[group]].forEach(function (value, index) {
                if (value.targetValue == this.rawValue) {
                    this.selectedIndex = index;
                }
            }.bind(this));

            this.values[groups[group]].forEach((value, index) => {
                data.push({
                    id: value.targetId, // ID zmienia swój typ na string.
                    groupId: groups[group],
                    targetId: value.targetId, // Przepisujemy ID aby zachować typ Number/Integer.
                    text: value.displayAsTarget ? value.targetValue : value.displayedValue,
                    selected: index === this.selectedIndex
                });
            });
        } else {
            //TODO Obsługa grup
        }

        return data;
    }

    update(change) {
        super.update(change);

        if (change.changedAttributes) {
            $.each(change.changedAttributes, function (name, newValue) {
                switch (name) {
                    case 'selectedIndex':
                        //TODO nie uwzgledniam grupy tutaj , sprawdzic z grupami
                        this.selectedIndex = newValue
                        this.selectedIndexGroup = change.changedAttributes["selectedIndexGroup"] || "";
                        $(this.input).val(newValue);
                        $(this.input).trigger("change");
                        break;
                    case 'rawValue':
                        this.rawValue = newValue;

                        let index = this.findIndexByValue(newValue);
                        $(this.input).val(index);
                        $(this.input).trigger("change");
                        break;
                    case 'multiselectRawValue':
                        this.multiselectRawValue = newValue;
                        this.selectMultiValues();
                        break;
                }
            }.bind(this));
        }
    }

    updateModel() {
        this.rawValue = $(this.input).select2('data');
    }

    protected innerWrap() {
        return this.input;
    }

    extractChangedAttributes() {
        if (this.designMode === true) {
            return this.changesQueue.extractChangedAttributes();
        }
        if (this.accessibility !== 'EDIT') {
            return {};
        }
        let attrs = {
            blur: undefined,
            cleared: undefined,
            text: undefined,
            addedTag: undefined,
            selectedIndexGroup: undefined,
            removedIndexGroup: undefined,
            selectedIndex: undefined,
            cursor: undefined,
            removedIndex: undefined
        };

        if (this.cleared) {
            attrs.cleared = this.cleared;
            this.cleared = false;
        }
        if (this.removedIndex != null) {
            attrs.removedIndexGroup = this.removedIndexGroup ? this.removedIndexGroup.toString() : ""
            attrs.removedIndex = this.removedIndex;
        } else if (this.selectedIndex != null) {
            //selectedIndexGroup must be send as string
            attrs.selectedIndexGroup = this.selectedIndexGroup ? this.selectedIndexGroup.toString() : ""
            attrs.selectedIndex = this.selectedIndex;
        }

        this.removedIndex = null;
        this.selectedIndex = null;
        this.removedIndexGroup = null;
        this.selectedIndexGroup = null;

        return attrs;
    }
}

export {
    Select2Combo
}
