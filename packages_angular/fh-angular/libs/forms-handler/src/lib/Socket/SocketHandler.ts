import {Injectable} from "@angular/core";
import {Connector} from "./Connector";

@Injectable({
    providedIn: 'root',
})
class SocketHandler {
    public connectionId: string;
    private connectors: Connector[] = [];
    public activeConnector: Connector;


    constructor() {
    }

    public addConnector(connector) {
        this.connectors.push(connector);
    };

    public selectConnector(index) {
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

export {SocketHandler};
