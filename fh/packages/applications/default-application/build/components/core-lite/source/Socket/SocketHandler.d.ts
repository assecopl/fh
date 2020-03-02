import { Connector } from "./Connector";
declare class SocketHandler {
    connectionId: string;
    private connectors;
    activeConnector: Connector;
    addConnector(connector: any): void;
    selectConnector(index: any): void;
    selectBestConnector(): void;
}
export { SocketHandler };
