import {Injectable} from "@angular/core";


@Injectable({
  providedIn: 'root',
})
export class CustomActionsManager {

  public _callbacks: { [key: string]: (data: string) => void; } = {};

  constructor() {
  }


  public callbacks(): { [key: string]: (data: string) => void } {
    return this._callbacks;
  }

  public registerCallback(name: string, callback: (...args) => void) {
    this._callbacks[name] = callback;
  }
}
