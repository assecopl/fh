// export const LANUGAGES = [ "sr", "ro", "ii", "ty", "tl", "yi", "ak", "ms", "ar", "no", "oj", "ff", "fa", "sq", "ay", "az", "zh", "cr", "et", "gn", "ik", "iu", "kr", "kv", "kg", "ku", "lv", "mg", "mn", "om", "ps", "qu", "sc", "sw", "uz", "za", "bi", "nb", "nn", "id", "tw", "eo", "ia", "ie", "io", "vo", "bh", "he", "sa", "cu", "pi", "ae", "la", "hy", "ss", "bo", "nr", "sl", "or", "nd", "na", "mi", "mr", "lu", "rn", "km", "fy", "bn", "av", "ab", "aa", "af", "am", "an", "as", "bm", "ba", "eu", "be", "bs", "br", "bg", "my", "ca", "ch", "ce", "ny", "cv", "kw", "co", "hr", "cs", "da", "dv", "nl", "dz", "en", "ee", "fo", "fj", "fi", "fr", "gl", "ka", "de", "el", "gu", "ht", "ha", "hz", "hi", "ho", "hu", "ga", "ig", "is", "it", "ja", "jv", "kl", "kn", "ks", "kk", "ki", "rw", "ky", "ko", "kj", "lb", "lg", "li", "ln", "lo", "lt", "gv", "mk", "ml", "mt", "mh", "nv", "ne", "ng", "oc", "os", "pa", "pl", "pt", "rm", "ru", "sd", "se", "sm", "sg", "gd", "sn", "si", "sk", "so", "st", "es", "su", "sv", "ta", "te", "tg", "th", "ti", "tk", "tn", "to", "tr", "ts", "tt", "ug", "uk", "ur", "ve", "vi", "wa", "cy", "wo", "xh", "yo", "zu" ] 
export const LANUGAGES = ['en', 'lt', 'pl'];

export class LanguageResiterer {
  public _i18n;
  public static getInstance(i18n) {
    return new LanguageResiterer(i18n)
  }

  private constructor(i18n) {
    this._i18n = i18n;
    this._i18n.supportedLanguages.push('lt');
  }

  public registerLanguags(instance) {
    LANUGAGES.forEach(async (lang) => {
      const file = `${instance.constructor.name}.${lang}`;
      try {
        if (instance.constructor.name) {
          const translation = await import(`../inputs/i18n/${file}`);
          console.log('-----------', translation)
          if (this._i18n) {
            const keys = Object.keys(translation);
            keys.forEach((key: string) => {
              this._i18n.registerStrings(lang, translation[key]);
            })
          }
        }
      } catch (e) {console.log(`Translation ${file} not exists`)}
    });
  }
}