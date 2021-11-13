import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SpacesComponent } from '../list/spaces.component';
import { SpacesDetailComponent } from '../detail/spaces-detail.component';
import { SpacesUpdateComponent } from '../update/spaces-update.component';
import { SpacesRoutingResolveService } from './spaces-routing-resolve.service';

const spacesRoute: Routes = [
  {
    path: '',
    component: SpacesComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SpacesDetailComponent,
    resolve: {
      spaces: SpacesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SpacesUpdateComponent,
    resolve: {
      spaces: SpacesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SpacesUpdateComponent,
    resolve: {
      spaces: SpacesRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(spacesRoute)],
  exports: [RouterModule],
})
export class SpacesRoutingModule {}
