import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {InputTextComponent} from './input-text.component';

describe('InputTextComponent', () => {
    let component: InputTextComponent;
    let fixture: ComponentFixture<InputTextComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [InputTextComponent]
        })
            .compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(InputTextComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
