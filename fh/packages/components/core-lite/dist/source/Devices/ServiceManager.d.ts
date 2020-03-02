export interface ServiceManager {
    getServiceId(): string;
    init(deviceConfig: (config: any) => void): void;
}
