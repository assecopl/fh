import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {SelectComboMenuComponent} from './select-combo-menu.component';

describe('SelectComboMenuComponent', () => {
    let component: SelectComboMenuComponent;
    let fixture: ComponentFixture<SelectComboMenuComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [SelectComboMenuComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(SelectComboMenuComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
