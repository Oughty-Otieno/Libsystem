import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SpacesComponent } from './list/spaces.component';
import { SpacesDetailComponent } from './detail/spaces-detail.component';
import { SpacesUpdateComponent } from './update/spaces-update.component';
import { SpacesDeleteDialogComponent } from './delete/spaces-delete-dialog.component';
import { SpacesRoutingModule } from './route/spaces-routing.module';

@NgModule({
  imports: [SharedModule, SpacesRoutingModule],
  declarations: [SpacesComponent, SpacesDetailComponent, SpacesUpdateComponent, SpacesDeleteDialogComponent],
  entryComponents: [SpacesDeleteDialogComponent],
})
export class SpacesModule {}
