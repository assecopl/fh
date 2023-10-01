import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {RadioOptionComponent} from './radio-option.component';

describe('RadioOptionComponent', () => {
    let component: RadioOptionComponent;
    let fixture: ComponentFixture<RadioOptionComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [RadioOptionComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(RadioOptionComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
