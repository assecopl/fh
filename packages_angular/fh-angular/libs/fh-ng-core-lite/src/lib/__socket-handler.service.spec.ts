import {TestBed} from '@angular/core/testing';

import {__socketHandlerService} from './__socket-handler.service';

describe('SocketHandlerService', () => {
    let service: __socketHandlerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
      service = TestBed.inject(__socketHandlerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
