const rules = [];
const exclusion = [];

(window as any).getRules = () => rules;
(window as any).getExclustions = () => exclusion;

const callback = async (mutationsList, observer) => {
    for (const rule of rules) {
        for(const mutation of mutationsList) {
            try {
                const exclustions = await Promise.all(exclusion.map(async ex => await ex.condition(mutation)));
                const excluded = exclustions.indexOf(true) > -1;
                if (!excluded) {
                    if ((mutation.type === 'childList' || mutation.type === 'attributes') && await rule.condition(mutation)) {
                        await rule.mutator(mutation);
                    }
                }
            } catch (e) {
                console.error(e.message, `\n on rule: `, rule, '\n on mutator: ', mutation);
            }
        }
    }
};

export const dynamicRelocator = (elQuery: string) => {
    if (!document.querySelector(elQuery)) {
      return;
    }
    const config = { attributes: true, childList: true, subtree: true };
    const targetNode = document.querySelector(elQuery);
    const observer = new MutationObserver(callback);
    observer.observe(targetNode, config);

    window.onresize = async () => {
      const f = async () => {
        for (const rule of rules) {
          const mutation = {target: document.querySelector(rule.selector)}
          try {
            if (!!mutation.target) {
              const excluded = exclusion.map(ex => ex.condition(mutation)).indexOf(true) > -1;
              if (!excluded) {
                if (await rule.condition(mutation)) {
                  await rule.mutator(mutation);
                }
              }
            }
          } catch (e) {
            console.error(e.message, `\n on rule: `, rule, '\n on mutator: ', mutation);
          }
          clearTimeout((window as any).resizeTimer);
          (window as any).resizeTimer = undefined;
        }
      }
      if ((window as any).resizeTimer) {
        clearTimeout((window as any).resizeTimer);
        (window as any).resizeTimer = undefined;
      }
      (window as any).resizeTimer = setTimeout(f, 200);
    };
    return observer;
}

export const addRule = (selector: string, condition: (mutation) => Promise<boolean>, mutator: (mutation) => Promise<void>) => {
    const executor = dynamicRelocator(selector);
    rules.push({selector, condition, mutator, executor});
}

export const excludeRule = (selector: string, condition: (mutation) => Promise<boolean>) => {
    exclusion.push({selector, condition});
}

