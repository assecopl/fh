declare class AdditionalButton {
    private action;
    private icon;
    private title;
    constructor(action: string, icon: string, title: string);
    getAction(): string;
    getIcon(): string;
    getTitle(): string;
}
export { AdditionalButton };
