import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';

@Injectable({providedIn: 'root'})
export class ApplicationLockService {
    backdropSubject: Subject<any> = new Subject<boolean>();
    currentUCValue: boolean = false;
    currentFormValue: boolean = false;

    showUC() {
        this.currentUCValue = true;
        setTimeout(() => {
        if (this.currentUCValue) {
            this.backdropSubject.next(true);
        }
        }, 500)
        return true;
    }

    showForm() {
        this.currentFormValue = true;
        setTimeout(() => {
        if (this.currentFormValue) {
            this.backdropSubject.next(true);
        }
        }, 500)
        return true;
    }


    hideForm() {
        this.currentFormValue = false;
        setTimeout(() => {
        if (!this.currentFormValue && !this.currentUCValue) {
            this.backdropSubject.next(false)
        }
        }, 501)
        return true;

    }

    hideUC() {
        this.currentUCValue = false;
        setTimeout(() => {
        if (!this.currentFormValue && !this.currentUCValue) {
            this.backdropSubject.next(false)
        }
        }, 501)
        return true;

    }

  public isActive(): boolean {
    return this.currentUCValue || this.currentFormValue;
  }


}
