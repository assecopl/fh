import {
    AfterContentInit,
    AfterViewInit,
    Component, EventEmitter,
    forwardRef,
    Host,
    Injector,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Optional, Output,
    SimpleChanges,
    SkipSelf
} from '@angular/core';
import {EwopAvailabilityDirective} from "@ewop/ng-availability";
import {EwopComponent} from '../../models/componentClasses/EwopComponent';
import {TableComponent} from '../table/table.component';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {Page} from '@ewop/ng-core';
import {TableComponentRef} from "../table/table.ref";

@Component({
    selector: 'ewop-table-paged',
    templateUrl: './table-paged.component.html',
    styleUrls: ['./table-paged.component.scss'],
    providers: [
        /**
         * Inicjalizujemy dyrektywę dostępności aby zbudoać hierarchię elementów i dać możliwość zarządzania dostępnością
         */
        EwopAvailabilityDirective,
        /**
         * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
         * Dzięki temu budujemy hierarchię kontrolek Ewop.
         */
        {provide: EwopComponent, useExisting: forwardRef(() => TablePagedComponent)},
        {provide: TableComponentRef, useExisting: forwardRef(() => TablePagedComponent)}]
})
export class TablePagedComponent extends TableComponent implements OnInit, AfterViewInit, OnChanges, OnDestroy, AfterContentInit {

    /**
     * @override
     */
    @Input()
    public collection: Page<any> = null;

    /**
     * pageable variables and eventEmiters
     */
    @Input()
    public pageable: boolean = true;
    @Output()
    public pageChange = new EventEmitter<any>();
    @Output()
    public sortChange = new EventEmitter<any>();
    @Output()
    public pageSizeChange = new EventEmitter<any>();

    @Input()
    public pageSize = 0;

    public pageNumber: number = 1;

    constructor(public injector: Injector, @Optional() @Host() @SkipSelf() parentEwopComponent: EwopComponent) {
        super(injector, parentEwopComponent);

        this.width = BootstrapWidthEnum.MD12;
        this.hostClass = `col-${this.width}`;
    }

    ngOnInit() {
        super.ngOnInit();
        this.restorePageNumber();
    }

    ngAfterViewInit(): void {
        super.ngAfterViewInit()
    }

    ngOnChanges(changes: SimpleChanges): void {
        super.ngOnChanges(changes);
    }

    ngOnDestroy(): void {
    }

    ngAfterContentInit(): void {
        super.ngAfterContentInit();
    }

    public restorePageNumber() {
        if (this.collection && this.collection.pageNumber > 0) {
            this.pageNumber = this.collection.pageNumber + 1;
            this.pageChange.emit(this.collection);
        }
    }

    public onPageChange() {
        if ((this.pageNumber - 1) != this.collection.pageNumber) {
            this.collection.pageNumber = this.pageNumber - 1;
            this.pageChange.emit(this.collection);
        }
    }
}
