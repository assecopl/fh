import {
    Component,
    forwardRef,
    Host,
    HostBinding,
    Injector, Input,
    OnInit,
    Optional, Renderer2,
    SimpleChanges,
    SkipSelf
} from '@angular/core';
import {EwopHTMLElementC} from '../../models/componentClasses/EwopHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {EwopComponent} from "../../models/componentClasses/EwopComponent";
import {RepeaterComponent} from "../repeater/repeater.component";

@Component({
    selector: 'ewop-group',
    templateUrl: './group.component.html',
    styleUrls: ['./group.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
      // EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => GroupComponent)}
    ]
})
export class GroupComponent extends EwopHTMLElementC implements OnInit {

  constructor(public override injector: Injector,
                @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);

        this.width = BootstrapWidthEnum.MD12;
        if (parentEwopComponent instanceof RepeaterComponent) {
            this.mb3 = false;
        }
    }

    @Input() class: string = ''; // override the standard class attr with a new one.

    @HostBinding('class')
    get hostClasses(): any { // Override host classes. Prevent adding it to Host

        const classArray = this.class.split(" ")
        const classMap = {};
        classArray.forEach(value => {
            if (value) {
                classMap[value.trim()] = false;
            }
        })
        return classMap; //Clear Host classes.
    }

  override ngOnInit() {
        super.ngOnInit();
        //Pass external ID to availability processor
        // this.ewopAvailabilityDirective.ngOnInit();
        // this.ewopAvailabilityDirective.id = this.id;
    }

  override ngOnChanges(changes: SimpleChanges) {
        super.ngOnChanges(changes);
    }
}
