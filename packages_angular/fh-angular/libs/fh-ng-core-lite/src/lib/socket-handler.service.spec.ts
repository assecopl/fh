import {TestBed} from '@angular/core/testing';

import {SocketHandlerService} from './socket-handler.service';

describe('SocketHandlerService', () => {
  let service: SocketHandlerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SocketHandlerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
