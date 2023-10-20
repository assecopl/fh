import {Injectable} from '@angular/core';
import {__connectorService} from './__connector.service';
import {Util} from './service/Util';

/**
 * @deprecated The method should not be used
 */
@Injectable({
  providedIn: 'root',
})
export class __socketHandlerService {
  public connectionId: string | null = null;
    private connectors: __connectorService[] = [];
    public activeConnector: __connectorService;
  public context: string = 'socketForms';

  constructor(private util: Util) {
      this.activeConnector = new __connectorService(
      'ws://localhost:8090/fhdp-demo-app/socketForms'
    );
    this.connectors.push(this.activeConnector);
  }

    public addConnector(connector: __connectorService) {
    this.connectors.push(connector);
  }

  public selectConnector(index: any) {
    var connector = this.connectors[index];
    if (connector) {
      this.activeConnector = this.connectors[index];
    } else {
      console.error('There is no connector at selected index');
    }
  }

  public selectBestConnector() {
    // TODO: We should do better logic to choose best connector.
    this.activeConnector = this.connectors[0];
  }
}
