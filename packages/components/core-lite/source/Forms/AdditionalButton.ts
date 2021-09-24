class AdditionalButton {
    private action: string;
    private icon: string;
    private title: string;

    constructor(action: string, icon: string, title: string) {
        this.action = action;
        this.icon = icon;
        this.title = title;
    }

    getAction() {
        return this.action;
    }

    getIcon() {
        return this.icon;
    }

    getTitle() {
        return this.title;
    }
}

export {AdditionalButton};