import {
    Component,
    EventEmitter,
    Host,
    HostListener,
    Injector,
    Input,
    OnInit,
    Optional,
    Output,
    SkipSelf
} from '@angular/core';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {EwopContainer, EwopNotificationService} from "@ewop/ng-core";

@Component({
    selector: 'ewop-notifications',
    templateUrl: './notifications.component.html',
    styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit {
    constructor(public injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent,
                public notification: EwopNotificationService,) {
    }

    public downloadToasts: Map<string, any> = new Map<string, any>();

    @Output()
    public onToastDispose: EventEmitter<any> = new EventEmitter<any>();

    @Input()
    public toasts: any[] = [];

    onDispose(toast: any) {
        this.onToastDispose.emit(toast);
    }

    ngOnInit(): void {
        this.notification.filesObserable.subscribe(c => {
            this.downloadToasts = c;
        });
    }
}
