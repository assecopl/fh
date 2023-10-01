import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {TablePagedComponent} from './table-paged.component';

describe('TablePagedComponent', () => {
    let component: TablePagedComponent;
    let fixture: ComponentFixture<TablePagedComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [TablePagedComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(TablePagedComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
