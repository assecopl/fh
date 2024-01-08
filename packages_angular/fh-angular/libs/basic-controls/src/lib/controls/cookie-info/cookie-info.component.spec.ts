import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {CookieInfoComponent} from './cookie-info.component';

describe('ComboComponent', () => {
  let component: CookieInfoComponent;
  let fixture: ComponentFixture<CookieInfoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [CookieInfoComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CookieInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
