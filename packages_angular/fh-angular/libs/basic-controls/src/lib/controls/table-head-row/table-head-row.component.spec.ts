import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';

import {TableHeadRowComponent} from './table-row.component';

describe('TableRowComponent', () => {
  let component: TableHeadRowComponent;
  let fixture: ComponentFixture<TableHeadRowComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TableHeadRowComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TableHeadRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
