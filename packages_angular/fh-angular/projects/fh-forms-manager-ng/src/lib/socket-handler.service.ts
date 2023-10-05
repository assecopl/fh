import {Injectable} from '@angular/core';
import {ConnectorService} from "./connector.service";
import {Util} from "./service/Util";

@Injectable({
    providedIn: 'root'
})
export class SocketHandlerService {
    public connectionId: string | null = null;
    private connectors: ConnectorService[] = [];
    public activeConnector: ConnectorService;
    public context: string = 'socketForms'

    constructor(private util: Util) {
      this.activeConnector = new ConnectorService("ws://localhost:8090/fhdp-demo-app/socketForms")
      this.connectors.push(this.activeConnector);
    }


    public addConnector(connector: ConnectorService) {
        this.connectors.push(connector);
    };

    public selectConnector(index: any) {
        var connector = this.connectors[index];
        if (connector) {
            this.activeConnector = this.connectors[index];
        } else {
            console.error('There is no connector at selected index');
        }
    };

    public selectBestConnector() {
        // TODO: We should do better logic to choose best connector.
        this.activeConnector = this.connectors[0];
    };
}
