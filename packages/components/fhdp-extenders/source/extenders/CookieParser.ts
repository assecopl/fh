const fromEntries = (entries: Array<Array<any>>): {[key: string]: any} => {
  const result: {[key: string]: any} = {};
  for (const entry of entries) {
    result[entry[0]] = entry[1];
  }
  return result;
}

export const getCookie = () => fromEntries(document.cookie.split('; ').map(el => (el.split('='))) as any);