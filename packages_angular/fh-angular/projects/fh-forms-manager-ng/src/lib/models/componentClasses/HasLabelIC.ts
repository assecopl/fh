import {Input, OnInit, Directive} from '@angular/core';

type Constructor<T> = new(...args: any[]) => T;

export function HasLabelM<T extends Constructor<{}>>(Base: T) {
    @Directive()
    class Temporary extends Base {

        @Input()
        public label: string = null;

        constructor(...args: any[]) {
            super(...args);
        }
    }

    return Temporary
}

export interface HasLabelI {
    label: string
}

