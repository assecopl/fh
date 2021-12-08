import {getCookie} from "./source/extenders/CookieParser";
import {addTranslationForCookiePolitics, rerenderCookieAlert} from "./source/extenders/CookiePoliticsHandler";
import {addRule, excludeRule} from "./source/extenders/DynamicRelocator";
import {extendFHML} from "./source/extenders/FHMLExtender"
import {
    // addFHMLToTooltipRule,
    // excludeSessionCounterRule,
    initCurrentMenuElementHighlightRule,
    initDynamicFooterPositionRule,
    initErrorBelowFieldRule,
    initMobileNavbarRules,
    SET_BORDER_WINDOW_WIDHT
} from "./source/RelocatorRules";

export interface DynamicRelocatorExtensionRule {
  type: 'extension';
  selector: string;
  condition: (mutation: any) => Promise<boolean>;
  ruleMutator: (mutation: any) => Promise<void>;
}

export interface DynamicRelocatorExclusionRule {
  type: 'exclusion';
  selector: string;
  condition: (mutation: any) => Promise<boolean>;
}

export interface ExtenderConfig {
  i18n: any;
  extendFHML?: boolean;
  mobileMaxWidth?: number;
  enableMobileNavbar?: boolean;
  enableErrorBelowField?: boolean;
  enableDynamicFooterPosition?: boolean;
  enableCurrentMenuElementHighlight?:boolean;
  currentMenuElementHighlighBottomBorderStyle?: string;
  enableFHMLinTooltips?: boolean;
  enableSessionCounterRules?: boolean;
  enableCookieAlert?: boolean;
  additionalRules?: Array<DynamicRelocatorExclusionRule | DynamicRelocatorExtensionRule>;
}

class FHDPExtender {
    constructor(config: ExtenderConfig) {
      if (config.extendFHML) {
        extendFHML();
      }
      if (config.mobileMaxWidth) {
        SET_BORDER_WINDOW_WIDHT(config.mobileMaxWidth);
      }
      if (config.enableMobileNavbar) {
        initMobileNavbarRules();
      }
      if (config.enableErrorBelowField) {
        initErrorBelowFieldRule();
      }
      if (config.enableDynamicFooterPosition) {
        initDynamicFooterPositionRule()
      }
      if(config.enableCurrentMenuElementHighlight) {
        initCurrentMenuElementHighlightRule(config.currentMenuElementHighlighBottomBorderStyle || '3px solid #ffc107');
      }
      // if (config.enableFHMLinTooltips) {
      //   addFHMLToTooltipRule();
      // }
      // if (config.enableSessionCounterRules === undefined || config.enableSessionCounterRules === true) {
      //   excludeSessionCounterRule();
      // }
      if (config.enableCookieAlert) {
        rerenderCookieAlert(config.i18n);
      }
      if (config.additionalRules && config.additionalRules.length > 0) {
        for (const rule of config.additionalRules) {
          if (rule.type === 'exclusion') {
            excludeRule(rule.selector, rule.condition)
          } else if (rule.type === 'extension') {
            addRule(rule.selector, rule.condition, rule.ruleMutator);
          }
        }
      }
    }
}

export {FHDPExtender, getCookie, addTranslationForCookiePolitics}
