import {
  AfterViewInit,
  Directive,
  ElementRef,
  OnInit,
  Renderer2,
  TemplateRef,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';

/**
 * @Deprecated
 * Raczej nie będziemy z tego korzystać - w tej formie psuje odwołania ViewChildren.
 */
@Directive()
export abstract class NoHostC implements OnInit, AfterViewInit {
  @ViewChild(TemplateRef, {read: TemplateRef, static: true})
  public abstract template: TemplateRef<void>;

  constructor(
    public readonly renderer2: Renderer2,
    public readonly elementRef: ElementRef<HTMLElement>,
    public readonly viewContainerRef: ViewContainerRef
  ) {
  }

  public ngOnInit() {
  }

  ngAfterViewInit(): void {
    //TODO Zastanowić się czy logiki noHost nie wynieść nadrzędnego komponentu.
    const comment = this.renderer2.createComment(
      this.elementRef.nativeElement.tagName.toLowerCase()
    );
    const parentNode = this.elementRef.nativeElement.parentNode;
    this.viewContainerRef.createEmbeddedView(this.template);
    this.renderer2.insertBefore(
      parentNode,
      comment,
      this.elementRef.nativeElement
    );
    this.renderer2.removeChild(parentNode, this.elementRef.nativeElement);
  }
}
