import {AbstractControl} from '@angular/forms';
import {AvailabilityEnum} from './enums/AvailabilityEnum';

/**
 *
 */
export class AvailabilityUtils {


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


  public static setFormControlAvailability(control: AbstractControl, availability: AvailabilityEnum) {
    /**
     * Timeout is basicly for first change when components are being initilize.
     * It prevents from error : Expression has changed after it was checked.
     * TODO Think of better solution.
     */
    setTimeout(function () {
      switch (availability as AvailabilityEnum) {
        case AvailabilityEnum.EDIT:
          control.enable({onlySelf: true, emitEvent: false});
          break;
        case AvailabilityEnum.VIEW:
          control.disable({onlySelf: true, emitEvent: false});
          break;
        case AvailabilityEnum.HIDDEN:
          control.disable({onlySelf: true, emitEvent: false});
          break;
        case AvailabilityEnum.INVISIABLE:
          control.disable({onlySelf: true, emitEvent: false});
          break;
        case AvailabilityEnum.DEFECTED:
          control.disable({onlySelf: true, emitEvent: false});
          break;
      }
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
