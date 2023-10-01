import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {TableCellComponent} from 'projects/ng-basic-controls/src/components/table-cell/table-cell.component';

describe('TableCellComponent', () => {
    let component: TableCellComponent;
    let fixture: ComponentFixture<TableCellComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [TableCellComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(TableCellComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
