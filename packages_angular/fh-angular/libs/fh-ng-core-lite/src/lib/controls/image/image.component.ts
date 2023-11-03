import {
  Component,
  forwardRef,
  Host,
  HostBinding,
  Injector,
  Input,
  OnInit,
  Optional,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngHTMLElementC} from '../../models/componentClasses/FhngHTMLElementC';
import {BootstrapWidthEnum} from '../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

type labelPosition = 'UP' | 'DOWN' | 'LEFT' | 'RIGHT';
type imageArea = {id: string, xl: number, xp: number, yl: number, yp: number};

interface IImageDataAttributes extends IDataAttributes {
  src: string;
  alt: string;
  onAreaClick?: string;
  onClick?: string;
  imageAreas?: {id: string, xl: number, xp: number, yl: number, yp: number}[];
  labelPosition?: labelPosition;
  label?: string;
}

@Component({
  selector: 'fhng-image',
  templateUrl: './image.component.html',
  styleUrls: ['./image.component.scss'],
  providers: [
    {provide: FhngComponent, useExisting: forwardRef(() => ImageComponent)},
  ],
})
export class ImageComponent extends FhngHTMLElementC implements OnInit {
  public override width: string = BootstrapWidthEnum.MD2;

  public override mb2: boolean = false;

  @Input()
  public src: string;

  @Input()
  public alt: string;

  @Input()
  public onAreaClick: string;

  @Input()
  public onClick: string;

  @HostBinding('class.form-group')
  public classFormGroup: boolean = true;

  @Input()
  public imageAreas: imageArea[] = [];

  @HostBinding('class.positioned')
  public get isPositioned (): boolean {
    return !!this.labelPosition;
  }

  @HostBinding('class')
  public get classPosition (): string {
    return this.labelPosition ? this.labelPosition.toLowerCase() : '';
  }

  constructor(
    public override injector: Injector,
    @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  public override ngOnInit(): void {
    super.ngOnInit();
  }

  public override ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: IImageDataAttributes): void {
    super.mapAttributes(data);

    this.src = data.src || this.src;
    this.alt = data.alt || data.label || this.alt;
    this.onAreaClick = data.onAreaClick || this.onAreaClick;
    this.onClick = data.onClick || this.onClick;
    this.imageAreas = data.imageAreas || this.imageAreas;
    this.labelPosition = data.labelPosition;

    //TODO: do usunięcia jak już będzie właściwie podawane URL itp. Tylko dla dema.
    if (this.formId === 'documentationHolder') {
      let reg: RegExp = /^http|^\//gi;

      if (!reg.test(this.src)) this.src = '/fhdp-demo-app/' + this.src;
    }
  }

  public onClickEvent ($event): void {
    $event.preventDefault();

    if (this.onClick) {
      if (this.formId === 'FormPreview') {
        this.fireEvent('onClick', this.onClick);
      } else {
        this.fireEventWithLock('onClick', this.onClick);
      }
    }
  }

  public onAreaClickEvent ($event: Event, areaId: string): void {
    $event.preventDefault();

    if (this.onAreaClick) {
      if (this.formId === 'FormPreview') {
        this.fireEvent('onAreaClick#' + areaId, this.onAreaClick);
      } else {
        this.fireEventWithLock('onAreaClick#' + areaId, this.onAreaClick);
      }
    }
  }
}
