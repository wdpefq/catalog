import { NgModule } from '@angular/core';

import { CatalogSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [CatalogSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [CatalogSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class CatalogSharedCommonModule {}
