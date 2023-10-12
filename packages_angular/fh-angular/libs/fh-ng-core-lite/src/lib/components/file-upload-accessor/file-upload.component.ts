import {Component, ElementRef, HostListener, Injector, Input, ViewChild,} from '@angular/core';
import {FhngHTMLElementC} from './../../models/componentClasses/FhngHTMLElementC';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {FhngNotificationService} from '@fhng/ng-core';
import {TranslateService} from '@ngx-translate/core';

/**
 * ControlValueAccessor wykorzystywany do bardziej specyficznej i bezpośredniej pracy z zawartością kontrolki.
 * Dzięki niemu mamy wpływ bezpośredni na wartość i możemy wykonać wstępną walidacje przed zapisem do elementu value kontrolki ReactiveForm.
 */
@Component({
  selector: 'fhng-file-upload-accessor',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: FileUploadAccessorComponent,
      multi: true,
    },
  ],
})
export class FileUploadAccessorComponent
  extends FhngHTMLElementC
  implements ControlValueAccessor {
  @Input() progress;
  onChange: Function;
  public file: File | File[] | null = null;

  @Input()
  public label: string;

  @Input()
  public maxSize: number = 1000000;

  @Input()
  public extensions: string = null;

  @Input()
  public multiple: boolean = false;

  @ViewChild('file') fileInput: ElementRef<HTMLInputElement>;

  @HostListener('change', ['$event.target.files']) emitFiles(event: FileList) {
    let file: File | File[] | null = event && event.item(0);
    if (this.multiple && event) {
      file = Array.from(event);
    }

    // @ts-ignore
    if (
      this.requiredFilesType(
        file,
        this.extensions ? this.extensions.split(',') : null
      ) &&
      this.filesSizeValidator(file)
    ) {
      this.onChange(file);
      this.file = file;
    } else {
      this.onChange(null);
      this.file = null;
      this.fileInput.nativeElement.value = null;
    }
  }

  constructor(
    private host: ElementRef<HTMLInputElement>,
    public injector: Injector,
    public notification: FhngNotificationService,
    public translate: TranslateService
  ) {
    super(injector, null);
  }

  writeValue(value: null) {
    // clear file input
    this.host.nativeElement.value = '';
    this.file = null;
  }

  registerOnChange(fn: Function) {
    this.onChange = fn;
  }

  registerOnTouched(fn: Function) {
  }

  public filesSizeValidator(files: File | File[]) {
    if (files instanceof Array) {
      for (let i = 0; i < files.length; i++) {
        if (!this.fileSizeValidator(files[i])) {
          return false;
        }
      }
      return true;
    }
    return this.fileSizeValidator(files);
  }

  public fileSizeValidator(file: File) {
    if (file && this.maxSize) {
      if (file && file.size >= this.maxSize) {
        //TODO Translate!!
        this.notification.showError(
          `Plik ${file.name} jest za duży. Maksymalny rozmiar pliku to ${
            this.maxSize / 1024
          } KB.`,
          null,
          10000
        );
        return false;
      } else {
        return true;
      }
    }
    return true;
  }

  public requiredFilesType(files: File | File[], type: string[]): boolean {
    if (files instanceof Array) {
      for (let i = 0; i < files.length; i++) {
        if (!this.requiredFileType(files[i], type)) {
          return false;
        }
      }
      return true;
    }
    return this.requiredFileType(files, type);
  }

  /**
   * This method logic must be similar to FhngValidators.requiredOneFileType
   * @param file
   * @param type
   */
  public requiredFileType(file: File, type: string[]): boolean {
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
        this.notification.showError(
          `Plik tego formatu nie jest obsługiwany. Akceptowalne pliki : ${this.extensions}.`,
          null,
          10000
        );
        return false;
      }
    }
    return true;
  }
}
