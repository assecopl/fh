import {
    Component,
    Host,
    Injector,
    Input, OnChanges,
    OnInit,
    Optional, SimpleChanges,
    SkipSelf
} from '@angular/core';
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {EwopBackdropService} from "@ewop/ng-core";

@Component({
    selector: 'ewop-backdrop',
    templateUrl: './backdrop.component.html',
    styleUrls: ['./backdrop.component.scss']
})
export class BackdropComponent implements OnInit, OnChanges {
    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                private backdrop: EwopBackdropService) {
    }

    @Input()
    public show: boolean = false;

    public transparent: boolean = true;

    ngOnInit(): void {
        this.backdrop.backdropSubject.subscribe(c => {
            if (c != this.show) {
                this.show = c;
            }
        });
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['show'] && changes['show'].currentValue == true) {
            setTimeout(() => {
                this.transparent = false;
            }, 300)
        }

    }


}
