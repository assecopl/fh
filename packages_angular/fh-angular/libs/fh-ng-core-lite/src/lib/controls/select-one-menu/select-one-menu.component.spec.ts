import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {SelectOneMenuComponent} from './select-one-menu.component';

describe('SelectOneMenuComponent', () => {
  let component: SelectOneMenuComponent;
  let fixture: ComponentFixture<SelectOneMenuComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SelectOneMenuComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectOneMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
