import {TestBed} from '@angular/core/testing';

import {Fh} from './f-h.service';

describe('FormsManagerService', () => {
    let service: Fh;

  beforeEach(() => {
    TestBed.configureTestingModule({});
      service = TestBed.inject(Fh);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
