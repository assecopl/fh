import {
  Component,
  forwardRef,
  HostBinding,
  Injector,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ElementRef,
  ViewChild,
  ViewContainerRef
} from '@angular/core';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {PanelGroupComponent} from "../panel-group/panel-group.component";
import {TreeElementComponent} from "../tree-element/tree-element.component";
import {TreeComponent} from "../tree/tree.component";
import {DropdownItemComponent} from "../dropdown-item/dropdown-item.component";
import {DropdownComponent} from "../dropdown/dropdown.component";
import {ButtonComponent} from "../button/button.component";
import {RowComponent} from "../row/row.component";
import {OutputLabelComponent} from "../output-label/output-label.component";
import {FormsManager} from "../../Socket/FormsManager";
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";

@Component({
  selector: 'fh-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.scss'],
  providers: [
    /**
     * Dodajemy deklaracje klasy ogólnej aby wstrzykiwanie i odnajdowanie komponentów wewnątrz siebie było możliwe.
     * Dzięki temu budujemy hierarchię kontrolek Fhng.
     */
    {provide: FhngComponent, useExisting: forwardRef(() => FormComponent)},
  ],
})
export class FormComponent extends FhngComponent implements OnInit, OnChanges {

  @Input() data: any = {};

  // @Input() public model: any = null;
  @Input() public label: string = null;
  @Input() public hideHeader: boolean = false;
  @Input() public formType: string = 'STANDARD';

  @HostBinding('class.navbar-nav')
  header: boolean = false;

  @HostBinding('class.card')
  cssCard: boolean = true;

  @HostBinding('class.fc')
  cssFc: boolean = true;

  constructor(
    public override injector: Injector, private formManager: FormsManager
  ) {
    super(injector, null);
  }

  override ngOnInit() {
    super.ngOnInit();
    console.log(this.formType + "" + this.id);
    if (this.formType == 'HEADER') {
      this.header = true;
      this.cssCard = false
    }

  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.formType == 'HEADER') {
      this.header = true;
      this.cssCard = false
    }
  }

  public override mapAttributes(data: IDataAttributes): void {
    super.mapAttributes(data)
    this.formType = data.formType
  }
}
