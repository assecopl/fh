# QuickStart!

## Example use:

```
const fhInstance = FhApplication.getInstance({
            registerStandardModules: true,
            registerChartsControls: true,
            registerFhDPControls: true,
            extensionsConfig: {
                extendFHML: true
            }
});

fhInstance.init();
```

## Example use in portlet:

```
const localPumaWsURL = 'ws://localhost:8090/socketForms';
const fhInstance = FhApplication.getInstance({
            registerStandardModules: true,
            registerChartsControls: true,
            registerFhDPControls: true,
            extensionsConfig: {
                extendFHML: true
            },
            liferay: {
              enabled: true,
              fhBaseUrl,
              fhContextPath,
              localPumaWsURL,
              Liferay
            }
});

fhInstance.init();
```

Values `fhBaseUrl`, `fhContextPath` and `Liferay` should be provided from template jsp file, and defined at the top as:

```
declare const Liferay: any;
declare const fhBaseUrl: string;
declare const fhContextPath: string;
```

## Additional info

1. Type `extensionsConfig` is provided by library `fhdp-extenders`.
2. This package contains the latest versions of fh components, fhdp-components and fdhp-extenders, if you're including this package you don't have need to including them.

## Versions table:

| fhdp-fh-starter version | lib name          | included version |
|:------------------------:|:------------------|:----------------:|
| v.18.0.0 - v.18.0.11     | fh-basic-controls | 4.6.25-test2     |
|                          | fh-charts-controls| 4.6.25           |
|                          | fh-designer       | 4.6.25           |
|                          | fh-forms-handler  | 4.6.25           |
|                          | fh-maps-controls  | 4.6.25           |
|                          | fh-printer-agent  | 4.6.25           |
|                          | fh-sensors        | 4.6.25           |
|                          | fhdp-controls     | 18.1.3           |
|                          | fhdp-extenders    | 18.0.4           |
|--------------------------|-------------------|------------------|
