import {TestBed} from '@angular/core/testing';

import {i18nService} from './i18n.service';

describe('I18nService', () => {
  let service: i18nService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(i18nService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
