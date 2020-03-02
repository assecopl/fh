import { ServiceManager } from "../Devices/ServiceManager";
declare class FH {
    private applicationLock;
    private formsManager;
    private socketHandler;
    protected serviceManagers: ServiceManager[];
    private applicationLocked;
    private ignoreNextHashChange;
    constructor();
    init(): void;
    initExternal(socketUrl: string): void;
    /**
     * @deprecated since version 2.0
     */
    startUseCase(subsystemId: string, useCaseId: string, callback: any): void;
    createComponent(componentObj: any, parent: any): any;
    changeHash(newHash: string): void;
    isIE(): number | false;
}
export { FH };
