import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DynamicComponentComponent} from './dynamic-component.component';

describe('DynamicComponentComponent', () => {
  let component: DynamicComponentComponent;
  let fixture: ComponentFixture<DynamicComponentComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DynamicComponentComponent],
    });
    fixture = TestBed.createComponent(DynamicComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
