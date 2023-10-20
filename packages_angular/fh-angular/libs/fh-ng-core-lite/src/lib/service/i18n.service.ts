import {Injectable} from '@angular/core';
import {LanguageChangeObserver} from './LanguageChangeObserver';

@Injectable({
  providedIn: 'root',
})
export class I18nService {
  constructor() {
  }

  supportedLanguages: string[] = [];
  strings: any = {};
  defaultStrings: any = {};
  selectedLanguage: string = null;

  /**
   * Components observing language change event
   * @type {LanguageChangeObserver[]}
   */
  private observers: LanguageChangeObserver[] = [];

  public subscribe(component: LanguageChangeObserver) {
    this.observers.push(component);
  }

  public unsubscribe(component: LanguageChangeObserver) {
    this.observers = this.observers.filter((item) => {
      if (item !== component) {
        return item;
      }
      return null;
    });
  }

  private isLanguageSupported(code: string): boolean {
    return this.supportedLanguages.indexOf(code) !== -1;
  }

  public registerStrings(
    code: string,
    strings: any,
    isCustom: boolean = false
  ): void {
    if (!this.isLanguageSupported(code)) {
      this.supportedLanguages.push(code);
      if (this.supportedLanguages.length === 1) {
        // this.defaultLanguage = code;
        this.selectedLanguage = code;
      }
    }

    if (isCustom) {
      this.strings[code] = (<any>Object).assign(
        this.strings[code] || {},
        strings
      );
    } else {
      this.defaultStrings[code] = (<any>Object).assign(
        this.defaultStrings[code] || {},
        strings
      );
    }
  }

  private overrideStrings(code: string, strings: any): void {
    this.registerStrings(code, strings, true);
  }

  public selectLanguage(code: string): void {
    if (!this.isLanguageSupported(code)) {
      console.warn('Selected language (' + code + ') is not supported');
    } else {
      this.selectedLanguage = code;
      this.observers.forEach(function (item) {
        item.languageChanged(code);
      });
    }
  }

  public translateString(string: string, args: any, code: string): string {
    let template;
    if (code) {
      template =
        (this.strings[code] ? this.strings[code][string] : undefined) ||
        (this.defaultStrings[code]
          ? this.defaultStrings[code][string]
          : undefined) ||
        string;
    } else {
      template =
        (this.strings[this.selectedLanguage]
          ? this.strings[this.selectedLanguage][string]
          : undefined) ||
        (this.defaultStrings[this.selectedLanguage]
          ? this.defaultStrings[this.selectedLanguage][string]
          : undefined) ||
        string;
    }
    if (template != null && args != null) {
      for (let i = 0; i < args.length; i++) {
        template = template.replace(
          new RegExp('\\{' + i + '\\}', 'g'),
          args[i]
        );
      }
    }
    return template;
  }

  public __(string: string, args: any = null, code: string = null): string {
    return this.translateString(string, args, code);
  }
}
