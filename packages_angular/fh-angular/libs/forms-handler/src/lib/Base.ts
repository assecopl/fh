import 'reflect-metadata'
import {AvailabilityEnum} from "./availability/enums/AvailabilityEnum";


// unavailable outside this module
// @dynamic
export class StringUtils {
  static firstLetterToUpper(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
  }

  static firstLetterToLower(string) {
    return string.charAt(0).toLowerCase() + string.slice(1);
  }

  static isNullOrEmpty(string: string) {
    return (!string || string.length === 0);
  }

  static equals(string1: string, string2: string) {
    if (!string1 && !string2) {
      return true;
    }
    if (!string1 || !string2) {
      return false;
    }
    return string1 == string2;
  }

  static startsWith(string1: string, string2: string) {
    if (!string1 || !string2) {
      return false;
    }
    return string1.startsWith(string2);
  }

  static endsWith(string1: string, string2: string) {
    if (!string1 || !string2) {
      return false;
    }
    return string1.endsWith(string2);
  }

  static contains(string1: string, string2: string) {
    if (!string1 || !string2) {
      return false;
    }
    return string1.indexOf(string2) >= 0;
  }

  static equalsIgnoreCase(string1: string, string2: string) {
    if (!string1 && !string2) {
      return true;
    }
    if (!string1 || !string2) {
      return false;
    }
    return string1.toLowerCase() == string2.toLowerCase();
  }

  static startsWithIgnoreCase(string1: string, string2: string) {
    if (!string1 || !string2) {
      return false;
    }
    return string1.toLowerCase().startsWith(string2.toLowerCase());
  }

  static endsWithIgnoreCase(string1: string, string2: string) {
    if (!string1 || !string2) {
      return false;
    }
    return string1.toLowerCase().endsWith(string2.toLowerCase());
  }

  static containsIgnoreCase(string1: string, string2: string) {
    if (!string1 || !string2) {
      return false;
    }
    return string1.toLowerCase().indexOf(string2.toLowerCase()) >= 0;
  }
}

export class TypeUtils {
  static isArray(input) {
    return !(input == null) && input instanceof Array;
  };

  static isSet(input) {
    return !(input == null) && input instanceof Set;
  };

  static isMap(input) {
    return !(input == null) && input instanceof Map;
  };

  static isObject(input) {
    return !(input == null) && input instanceof Object;
  };

  static isSingleElementObject(input) {
    return this.isObject(input) && !this.isArray(input) && !this.isSet(input) && !this.isMap(input);
  };

  static isFunction(input) {
    return !(input == null) && input instanceof Function;
  };
}

export class Deffered<T> {
  innerResolve: (value?: T | PromiseLike<T>) => void;

  innerReject: (reason?: any) => void;

  promise: Promise<T>;

  constructor(executor?: (resolve: (value?: T | PromiseLike<T>) => void, reject: (reason?: any) => void) => void) {
    this.promise = new Promise<T>((resolve, reject) => {
      if (executor) {
        executor(resolve, reject);
      }

      this.innerResolve = resolve;
      this.innerReject = reject;
    });
  }

  resolve(value?: T | PromiseLike<T>): Promise<T> {
    this.innerResolve(value);
    return this.promise
  }

  reject(reason?: any): Promise<T> {
    this.innerReject(reason);
    return this.promise;
  }
}

// export function getConfig():any {
//   const injector = getInjector();
//   const config:any = injector.get(FHNG_CORE_CONFIG);
//   return config
// }
//
// export function getInjector():Injector{
//   return ɵɵdirectiveInject(INJECTOR);
// }

export enum Direction {
  ASC,
  DESC,
}

export class Order {
  constructor(public property: string, public direction: Direction = Direction.ASC) {
  }
}

/**
 * Provides pageable implementation
 */
export class Page<T> extends Array<T> {
  public pageNumber: number;
  public pageSize: number;
  public orders: Order[] = [];
  public totalElements: number;
  public totalPages: number;
  public first: boolean;
  public last: boolean;
  public empty: boolean;
  public source: (pageRequest) => this;

  async refresh(): Promise<this> {
    let newPage = await this.source(this.asPageable());
    // @ts-ignore
    this.clear();
    Object.assign(this, newPage);

    return this;
  }

  refreshNeeded(): Promise<this> {
    return this.refresh();
  }

  asPageable(): Pageable {
    return new Pageable(this.pageNumber, this.pageSize, this.orders);
  }

  /**
   * Creates Page object from serialized Spring PageImpl or simple Array
   *
   * @param object Array or Spring PageImpl
   */
  static override of<T>(object: any): Page<T> {
    if (object.pageable) {
      let page;
      if (object.content) {
        page = new Page<T>(...object.content);
      } else {
        page = new Page<T>(...[]);
      }

      page.pageNumber = object.pageable.pageNumber;
      page.pageSize = object.pageable.pageSize;
      if (TypeUtils.isArray(object.pageable.sort)) {
        object.pageable.sort.forEach(order => {
          page.orders.push(new Order(order.property, Direction[order.direction as string]));
        });
      }
      page.totalElements = object.totalElements;
      page.totalPages = object.totalPages;
      page.first = object.first;
      page.last = object.last;
      page.empty = object.empty;

      return page;
    }

    let page = new Page<T>(...object);

    page.pageNumber = 0;
    page.pageSize = page.length;
    page.totalElements = page.length;
    page.totalPages = page.length == 0 ? 0 : 1;
    page.first = true;
    page.last = true;
    page.empty = page.length == 0;

    return page;
  }
}

export class Pageable {
  constructor(public pageNumber: number,
              public pageSize: number,
              public sort: Order[] = []) {
  }
}

export interface IForm {
  accessibility: AvailabilityEnum
  blockFocusForModal: boolean
  blocked: boolean
  container: string //"MODAL_VIRTUAL_CONTAINER"
  designDeletable: boolean
  designMode: boolean
  effectiveFormType: "MODAL_OVERFLOW" | "STANDARD" | "MODAL" | "FLOATING" | "HEADER" | "MINIMAL"
  focusFirstElement: boolean
  formType: "MODAL_OVERFLOW" | "STANDARD" | "MODAL" | "FLOATING" | "HEADER" | "MINIMAL"
  fromCloud: boolean
  hideHeader: boolean
  hintType: 'STANDARD' | 'STANDARD_POPOVER' | 'STATIC' | 'STATIC_POPOVER' | 'STATIC_POPOVER_LEFT' | 'STATIC_LEFT'
  id: string
  invisible: boolean
  label: string
  modal: boolean
  modalSize: "REGULAR" | "SMALL" | "LARGE" | "XLARGE" | "XXLARGE" | "FULL"
  push: false
  state: "ACTIVE" | "INACTIVE_PENDING" | "INACTIVE" | "SHADOWED" | "HIDDEN" | "CLOSED"
  subelements: any[]
  nonVisualSubcomponents?: any[]

}
