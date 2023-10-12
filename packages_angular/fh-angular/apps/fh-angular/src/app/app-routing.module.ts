import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {FhFormsManagerNgComponent} from 'projects/fh-forms-manager-ng/src/lib/fh-forms-manager-ng.component';

const routes: Routes = [{path: '/', component: FhFormsManagerNgComponent}];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {
}
