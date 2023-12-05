import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {DictionaryLookupComponent} from './dictionary-lookup.component';

describe('ComboComponent', () => {
    let component: DictionaryLookupComponent;
    let fixture: ComponentFixture<DictionaryLookupComponent>;

    beforeEach(waitForAsync(() => {
        TestBed.configureTestingModule({
            declarations: [DictionaryLookupComponent],
        }).compileComponents();
    }));

    beforeEach(() => {
        fixture = TestBed.createComponent(DictionaryLookupComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
