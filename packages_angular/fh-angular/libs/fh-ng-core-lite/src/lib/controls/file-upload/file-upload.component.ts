import {
  Component,
  EventEmitter,
  forwardRef,
  Host,
  Injector,
  Input,
  OnInit,
  Optional,
  Output,
  SimpleChanges,
  SkipSelf,
} from '@angular/core';
import {FhngReactiveInputC} from '../../models/componentClasses/FhngReactiveInputC';
import {BootstrapWidthEnum} from './../../models/enums/BootstrapWidthEnum';
import {FhngComponent} from '../../models/componentClasses/FhngComponent';
import {NG_VALUE_ACCESSOR, ValidatorFn} from '@angular/forms';
import {IDataAttributes} from "../../models/interfaces/IDataAttributes";
import {BootstrapStyleType, FORM_VALUE_ATTRIBUTE_NAME} from "../../models/CommonTypes";
import {BootstrapStyleEnum} from "../../models/enums/BootstrapStyleEnum";
import {FileUploadAccessorComponent} from "../../components/file-upload-accessor/file-upload.component";

type UpdateOn =  'change' | 'blur' | 'submit';

export interface IFileUploadDataAttributes extends IDataAttributes {
  onUpload?: string;
  multiple: boolean;
  language?: string;
  maxSize?: number;
  extensions?: string;
  style: BootstrapStyleType
}

@Component({
  selector: 'fhng-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
  providers: [
    {
      provide: FhngComponent,
      useExisting: forwardRef(() => FileUploadComponent),
    },
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: FileUploadAccessorComponent,
      multi: true,
    },
  ],
})
export class FileUploadComponent extends FhngReactiveInputC implements OnInit {
  public override width = BootstrapWidthEnum.MD3;

  public override mb2 = false;

  @Input()
  public override updateOn: UpdateOn = 'change'; //Domyślnie upload plików musi aktualizować/odpisywać się od razu.

  @Input()
  public maxSize: number = 1000000; //KB

  @Input()
  public extensions: string = null;

  @Input()
  public multiple: boolean = false;

  @Output()
  public upload: EventEmitter<File> = new EventEmitter<File>();

  @Input()
  public onUpload: string;

  @Input()
  public language: string;

  @Input()
  public style: BootstrapStyleType = BootstrapStyleEnum.PRIMARY;

  @Input()
  public file: File | File[] | null = null;

  constructor(
      public override injector: Injector,
      @Optional() @SkipSelf() parentFhngComponent: FhngComponent
  ) {
    super(injector, parentFhngComponent);
  }

  public override ngOnInit(): void{
    super.ngOnInit();
  }

  public onFileChangeEvent(event: Event): void{
    if (event) {
      this.onChangeEvent();
    }
  }

  public override ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  public override mapAttributes(data: IFileUploadDataAttributes): void {
    super.mapAttributes(data);

    this.extensions = data.extensions;
    this.onUpload = data.onUpload;
    this.multiple = data.multiple;
    this.language = data.language;
    this.maxSize = data.maxSize;
    this.style = data.style || BootstrapStyleEnum.PRIMARY;
  }

  public onAddedFiles($event: Event & {target?: {files?: FileList}}): void {
    $event.preventDefault();

    let file: File | File[] | null =
        $event && $event.target.files && $event.target.files.item(0);

    if (this.multiple && $event &&  $event.target.files) {
      file = Array.from($event.target.files);
    }

    if (
        this._requiredFilesType(file, this.getExtensions()) &&
        this._filesSizeValidator(file)
    ) {
      this.file = file;
    } else {
      this.file = null;
    }

    console.log('this.add', this.file);
    this.rawValue = this.file;
    this.valueChanged = true;
    this.fireEventWithLock('onUpload', this.onUpload);
  }

  public getExtensions(): string[] {
    return this.extensions ? this.extensions.split(',') : null;
  }

  public override extractChangedAttributes() {
    let attrs = {};
    if (this.valueChanged) {
      attrs[FORM_VALUE_ATTRIBUTE_NAME] = this.rawValue;
      this.valueChanged = false;
    }

    return attrs;
  };

  private _requiredFilesType(files: File | File[], type: string[]): boolean {
    if (files instanceof Array) {
      for (let i = 0; i < files.length; i++) {
        if (!this._requiredFileType(files[i], type)) {
          return false;
        }
      }
      return true;
    }
    return this._requiredFileType(files, type);
  }

  private _requiredFileType(file: File, type: string[]): boolean {
    let existValue: boolean = false;
    if (file && type) {
      const path = file.name.replace(/^.*[\\\/]/, '');
      var re = /(?:\.([^.]+))?$/;

      const extension = '.' + re.exec(path)[1];
      for (let i = 0; i < type.length; i++) {
        const typeFile = type[i].toLowerCase();
        if (typeFile === extension.toLowerCase()) {
          existValue = true;
        }
      }
      if (existValue == true) {
        return true;
      } else {
        //TODO Translate!!
        // this.notification.showError(
        //   `Plik tego formatu nie jest obsługiwany. Akceptowalne pliki : ${this.extensions}.`,
        //   null,
        //   10000
        // );
        return false;
      }
    }
    return true;
  }

  private _filesSizeValidator(files: File | File[]) {
    if (files instanceof Array) {
      for (let i = 0; i < files.length; i++) {
        if (!this._filesSizeValidator(files[i])) {
          return false;
        }
      }
      return true;
    }

    return (files as File).size <= this.maxSize;
  }
}
