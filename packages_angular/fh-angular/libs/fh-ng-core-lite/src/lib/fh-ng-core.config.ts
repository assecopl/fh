import {InjectionToken} from '@angular/core';

export const FHNG_CORE_CONFIG = new InjectionToken(
  'FH_NG_CORE_CONFIG'
);

export interface FhNgCoreConfig {
  production: boolean;
  development: boolean;
  debug?: boolean;

  [name: string]: any
}



