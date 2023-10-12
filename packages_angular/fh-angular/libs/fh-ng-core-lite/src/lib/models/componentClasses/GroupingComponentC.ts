import {
  AfterContentInit,
  Directive,
  Host,
  Injector,
  OnChanges,
  OnInit,
  Optional,
  QueryList,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from './FhngHTMLElementC';
import {FhngComponent} from './FhngComponent';

// @ts-ignore
/**
 * Komponent odpowiedzialny za tworzenie grup komponentów
 *
 */
@Directive()
export abstract class GroupingComponentC<T extends FhngHTMLElementC>
  extends FhngHTMLElementC
  implements OnInit, OnChanges, AfterContentInit {
  constructor(
    public override injector: Injector,
    @Optional() @Host() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  /**
   * @Deprecated
   * Apear afterContentInit
   * Use @ContentChild(T) in base class
   * //TODO Ogarnac to bardziej generycznie.
   */
  public subcomponents?: QueryList<T> = new QueryList<T>();

  /**
   * Array of subcomponents
   * Push elements to this array inside  T construtor.
   * Then you can access components earlier then afterContentInit.
   */
  public _subcomponents: T[] = [];

  public abstract updateSubcomponent: (subcomponent: T, index: number) => void;

  public abstract getSubcomponentInstance(): new (...args: any[]) => T;

  public override ngAfterContentInit(): void {
    if (this._subcomponents.length === 0) {
      //If components array was not fill programaticaly then we get it from QueryList.
      this._subcomponents = this.subcomponents.toArray();
    }
    this.updateSubcomponents();
  }

  //Now we can access subcomponents
  public override ngOnInit(): void {
    super.ngOnInit();

    // this.childFhngComponents.forEach((c) => {
    //     if (c instanceof this.getSubcomponentInstance()) {
    //         this._subcomponents.push(c);
    //         if (this.availabilityDirective._myAvailability) {
    //             c.availabilityDirective._myAvailability = this.availabilityDirective._myAvailability
    //         }
    //     }
    // })
  }

  public updateSubcomponents() {
    if (this.updateSubcomponent != null) {
      this._subcomponents.forEach((item, index) => {
        this.updateSubcomponent(item, index);
      });
    }
  }

  protected _findSubcomponentById(id: string): T | null {
    return this._subcomponents.find((p) => p.innerId === id);
  }

  protected _getSubcomponent(index: number): T | null {
    return this._subcomponents[index];
  }

  override ngOnChanges(changes: SimpleChanges) {
    super.ngOnChanges(changes);
  }
}
