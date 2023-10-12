import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {DictionaryViewComponent} from './dictionary.component';

describe('DictionaryComponent', () => {
  let component: DictionaryViewComponent;
  let fixture: ComponentFixture<DictionaryViewComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [DictionaryViewComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DictionaryViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
