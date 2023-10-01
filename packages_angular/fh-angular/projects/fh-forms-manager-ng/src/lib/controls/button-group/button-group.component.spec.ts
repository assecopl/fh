import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {ButtonGroupComponent} from './button-group.component';

describe('ButtonGroupComponent', () => {
    let component: ButtonGroupComponent;
    let fixture: ComponentFixture<ButtonGroupComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [ButtonGroupComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(ButtonGroupComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
