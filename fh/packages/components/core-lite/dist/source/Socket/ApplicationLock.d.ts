import 'bootstrap/js/dist/modal';
import 'bootstrap/js/dist/tooltip';
import 'jquery-ui/ui/widgets/dialog';
declare class ApplicationLock {
    private i18n;
    rids: string[];
    lockElement: HTMLElement;
    reconnectInfo: HTMLElement;
    opacityTimer: number;
    constructor();
    enable(requestId: string): void;
    disable(requestId: string): void;
    isActive(): number;
    createErrorDialog(data: Array<any>, callback?: any, withClose?: boolean): any;
    private createElement;
    static closeErrorDialog(dialog: any): void;
    createInfoDialog(info: any, button1Name?: any, button1OnClick?: any, button2Name?: any, button2OnClick?: any, withClose?: boolean): void;
    closeInfoDialog(): void;
    private static appendDialogElements;
}
export { ApplicationLock };
