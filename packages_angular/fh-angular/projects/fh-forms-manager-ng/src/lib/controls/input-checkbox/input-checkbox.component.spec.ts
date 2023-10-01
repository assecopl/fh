import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {InputCheckboxComponent} from './input-checkbox.component';

describe('InputCheckboxComponent', () => {
    let component: InputCheckboxComponent;
    let fixture: ComponentFixture<InputCheckboxComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [InputCheckboxComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(InputCheckboxComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
