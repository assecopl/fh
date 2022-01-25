# FHDP-Extender
This library extends abilities of stock fh behaviors.

It works on Mutator Observer and inject code between luna rendering, and displaying it on client side.
Also includes fhml extender.

## How to use

### cookie parser

```
import {getCookie} from 'fhdp-extenders';

...

const cookie = getCookie();
```

it returns cookie dictionary parsed to json.


### init extenders

```
import {FHDPExtender} from 'fhdp-extenders';

...

new FHDPExtender({
  i18n: i18nInstance,                         /* needs to provide i18n instance to handle translations*/
  extendFHML: true,                           /* extends fhml */
  enableMobileNavbar: true,                   /* enable changing from desktop to mobile navbar */
  enableHintOnHover: true,                    /* enable hints on hover */
  enableRequiredAtLabel: true,                /* enable moving required star from field to label */
  enableErrorBelowField: true,                /* enable moving error from field to label */
  enableDynamicFooterPosition: true,          /* enable dynamic footer sliding */
  enableCurrentMenuElementHighlight: true,    /* enable highlight for currently selected element */
  enableFHMLinTooltips: true,                 /* enable fhml in tooltips */
  enableSessionCounterRules: true,            /* enable session counter rules */
  enableCookieAlert: true,                    /* enable cookie alert at bottom */
});

```

### extend translations for cookie alert

``` 

import {addTranslationForCookiePolitics} from 'fhdp-extenders';

addTranslationForCookiePolitics('pl' {
  warning: 'Uwaga!',
  message: 'Używając tej strony akceptujesz politykę prywatności i przetwarzanie plików cookie',
  close: 'Akceptuję'
})

```

### extend rules

In FHDPExtender config add node `additionalRules`.
This is list of objects:
```
{
  // 'exclusion' type excludes all mutations described by condition param,
  // 'extension' type extends rules. For this param ruleMutator is obligatory
  type: 'exclusion' | 'extension' 
  // 'selector' its marker for recognising whitch element is selected (only used in debug),
  selector: string 
  // async function for recognisiong if element fits into mutation rule
  condition: (mutation: Mutation) => Promise<boolean> 
  // rule mutator is actual function that modifies elements in DOM. It runs only if type = extension and condition returns true.
  ruleMutator: (mutation: Mutation) => Promise<void>
}
```

Example adding div to dom only if not exists:

```
new FHDPExtender({
  i18n: i18nInstance,
  additionalRules: [
    {
      type: 'extension',
      selector: '#myComponent',
      condition: async (mutation) => !!document.querySelector('#myComponent'),
      ruleMutator: async (mutation) => {
        const myElement = document.createElement(div);
        myElement.id = '#myComponent';
        myElement.innerText = 'Thats my element!';
        document.body.appendChild(myElement);
      }
    }
    ...
  ]
});
```
