export interface ClientDataHandler {
    getServiceId() : string;
    handleData(data :any) : void;
}