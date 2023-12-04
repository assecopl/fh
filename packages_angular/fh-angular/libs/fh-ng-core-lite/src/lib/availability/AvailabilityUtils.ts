import {AbstractControl, NgControl} from '@angular/forms';
import {AvailabilityEnum} from './enums/AvailabilityEnum';

/**
 *
 */
export class AvailabilityUtils {

  public static processOnEdit(availability: AvailabilityEnum | string, fn:() => any, defaultReturn:any) {
    if (!availability) return defaultReturn;
    if (AvailabilityUtils.stringToEnum(availability) === AvailabilityEnum.EDIT) return fn();
    return defaultReturn;
  }

  public static isHidden(availability: AvailabilityEnum | string): boolean {
    if (!availability) return false;

    if (AvailabilityUtils.stringToEnum(availability) === AvailabilityEnum.HIDDEN) return true
    return false;
  }

  public static setHtmlElementAvailability(node: HTMLElement, availability: AvailabilityEnum, hiddenElementsTakeUpSpace: boolean = false) {
    if (availability !== AvailabilityEnum.HIDDEN) {
      node.classList.remove('d-none');
      node.classList.remove('invisible');

    }
    if (availability !== AvailabilityEnum.DEFECTED && availability !== AvailabilityEnum.VIEW) {
      node.classList.remove('fh-disabled');
      node.classList.remove('disabled');
    }
    if (availability !== AvailabilityEnum.EDIT) {
      node.classList.remove('fh-editable');
    }
    if (availability !== AvailabilityEnum.VIEW) {
      node.classList.remove('disabledElement');
    }

    switch (availability as AvailabilityEnum) {
      case AvailabilityEnum.EDIT:
        AvailabilityUtils.setInputELementAvaialbility(node, availability);
        break;
      case AvailabilityEnum.VIEW:
        AvailabilityUtils.setInputELementAvaialbility(node, availability);
        break;
      case AvailabilityEnum.HIDDEN:
        node.classList.add(hiddenElementsTakeUpSpace ? 'invisible' : 'd-none');
        break;
      case AvailabilityEnum.INVISIABLE:
        node.classList.add('invisible');
        break;
      case AvailabilityEnum.DEFECTED:
        AvailabilityUtils.setInputELementAvaialbility(node, availability);
        node.title = 'Broken control';
        break;
    }


  }

  public static setInputELementAvaialbility(node: HTMLElement, availability: AvailabilityEnum) {
    switch (node.nodeName) {
      case 'BUTTON':
      case 'INPUT':
      case 'TEXTAREA':
      case 'SELECT':
      case 'OPTION':
      case 'OPTGROUP':
      case 'FIELDSET':
        if (availability !== AvailabilityEnum.EDIT) {
          node.setAttribute('disabled', 'disabled');
          node.classList.add('fh-disabled');
          node.classList.remove('fh-editable');
        } else {
          node.classList.add('fh-editable');
          node.classList.remove('fh-disabled');
          node.removeAttribute('disabled');
        }

        break;
      default:
        if (availability !== AvailabilityEnum.EDIT) {
          node.classList.add('fh-disabled');
          node.classList.add('disabled');
          node.classList.remove('fh-editable');
        } else {
          node.classList.add('fh-editable');
          node.classList.remove('fh-disabled');
          node.classList.remove('disabled');
        }
    }
  }


  public static setFormControlAvailability(control: NgControl, availability: AvailabilityEnum) {
    /**
     * Timeout is basicly for first change when components are being initilize.
     * It prevents from error : Expression has changed after it was checked.
     * TODO Think of better solution.
     */
    let action = 'enable';
    setTimeout(function () {
      switch (availability as AvailabilityEnum) {
        case AvailabilityEnum.EDIT:
          action = 'enable';
          break;
        case AvailabilityEnum.VIEW:
          action = 'disable';
          break;
        case AvailabilityEnum.HIDDEN:
          action = 'disable';
          break;
        case AvailabilityEnum.INVISIABLE:
          action = 'disable';
          break;
        case AvailabilityEnum.DEFECTED:
          action = 'disable';
          break;
      }
      control[action]();
    }, 0)


  }

  static stringToEnum(availability: AvailabilityEnum | string): AvailabilityEnum {
    let a: AvailabilityEnum = null;
    if (typeof availability === 'string') {
      a = AvailabilityEnum[availability];
    } else {
      a = availability;
    }
    return a;
  }


}
