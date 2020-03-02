export declare class ServiceManagerUtil {
    private formsManager;
    private socketHandler;
    callAction(serviceId: string, actionName: string, params?: any[], doLock?: boolean): void;
    cancelServiceEvents(serviceId: string): void;
    sendData(serviceId: string, data: any): void;
}
