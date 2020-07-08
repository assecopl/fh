import 'bootstrap/js/dist/tooltip';
import { FormComponent } from './FormComponent';
import { I18n } from '../I18n/I18n';
import { FHML } from '../FHML';
import { AdditionalButton } from './AdditionalButton';
declare abstract class HTMLFormComponent extends FormComponent {
    protected fhml: FHML;
    protected i18n: I18n;
    protected container: HTMLElement;
    protected hintElement: HTMLElement;
    htmlElement: any;
    accessibility: string;
    invisible: string;
    protected presentationStyle: string;
    protected requiredField: string;
    component: any;
    toolbox: HTMLElement;
    protected inputGroupElement: HTMLElement;
    protected labelElement: HTMLElement;
    protected requiredElement: HTMLElement;
    protected requiredHidden: boolean;
    private readonly translationItems;
    private inputSize;
    width: string[];
    protected styleClasses: string[];
    protected readonly inlineStyle: string;
    protected readonly wrapperStyle: string;
    private readonly language;
    protected hint: any;
    protected hintPlacement: string;
    protected hintTrigger: string;
    hintType: 'STANDARD' | 'STATIC';
    hintInputGroup: boolean;
    protected hintInitialized: boolean;
    rawValue: any;
    private areSubcomponentsRendered;
    protected oldValue: any;
    protected components: HTMLFormComponent[];
    private readonly push;
    private static bootstrapColRegexp;
    private static bootstrapColWidthRegexp;
    private static bootstrapColSeparateCahrsRegexp;
    protected focusableComponent: HTMLElement;
    type: string;
    input: any;
    autocomplete: string;
    ariaLabel: string;
    htmlAccessibilityRole: string;
    protected constructor(componentObj: any, parent: FormComponent);
    create(): void;
    display(): void;
    render(): void;
    renderSubcomponents(): void;
    initHint(): void;
    /**
     *
     * @param ttip
     */
    processStaticHintElement(ttip: any): HTMLElement;
    destroyHint(): void;
    destroy(removeFromParent: any): void;
    update(change: any): void;
    updateLabelClass(newLabel: any): void;
    processAddedComponents(change: any): void;
    updateModel(): void;
    accessibilityResolve(node: HTMLElement, access: string): void;
    setAccessibility(accessibility: any): void;
    setPresentationStyle(presentationStyle: any): void;
    protected getMainComponent(): any;
    addStyles(): void;
    hasHeight(): boolean;
    handleHeight(): void;
    addAlignStyles(): void;
    resolveLabelPosition(): void;
    setInputAndLabelPosition(property: string, labelElement: HTMLElement, inputElement: HTMLElement): void;
    resolveInputSize(): void;
    getQueryForInputSize(): any;
    enableStyleClasses(): void;
    setWrapperWidth(wrapper: HTMLDivElement, oldWidth: string[], newWidth: string[]): void;
    protected wrap(skipLabel?: boolean, isInputElement?: boolean): void;
    protected wrapInner(wrapper: any, wrappedComponent: any, skipLabel?: boolean, isInputElement?: boolean): void;
    protected innerWrap(): any;
    showToolbox(): void;
    hideToolbox(): void;
    focusCurrentComponent(deferred: any, options: any): any;
    setRequiredField(isRequired: any): void;
    extractChangedAttributes(): {};
    __(string: any, node?: any, args?: any): any;
    changeLanguage(code: any): void;
    getDefaultWidth(): string;
    getAdditionalButtons(): AdditionalButton[];
    animateScroll(options: any): void;
    highlightDesignerElementTree(): void;
    updateDesignerElementTree(focusEventData: any, elementTreeEquivalent: any): void;
    /**
     * Function that handle adding margin and paddings to component styles.
     * @return string
     */
    handlemarginAndPAddingStyles(): void;
    /**
     * Function process width string from backend serwer and creates proper bootstrap classes string array so they can be added to component.
     * @param width
     */
    private handleWidth;
    /**
     * Logic moved to function so it can be overrided by children classes.
     */
    protected buildDesingerToolbox(): void;
    private handleDeleteBtnEvent;
    /**
     * Ads autocomplete attribiute to input element i both exists on element.
     */
    protected processAutocomplete(value?: string): void;
    /**
     * Ads aria-label attribiute to element.
     */
    protected processAriaLabel(value?: string): void;
    /**
     * Ads role attribiute to element.
     */
    protected processHtmlAccessibilityRole(value?: string): void;
}
export { HTMLFormComponent };
