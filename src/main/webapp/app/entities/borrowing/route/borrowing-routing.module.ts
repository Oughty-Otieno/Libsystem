import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BorrowingComponent } from '../list/borrowing.component';
import { BorrowingDetailComponent } from '../detail/borrowing-detail.component';
import { BorrowingUpdateComponent } from '../update/borrowing-update.component';
import { BorrowingRoutingResolveService } from './borrowing-routing-resolve.service';

const borrowingRoute: Routes = [
  {
    path: '',
    component: BorrowingComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BorrowingDetailComponent,
    resolve: {
      borrowing: BorrowingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BorrowingUpdateComponent,
    resolve: {
      borrowing: BorrowingRoutingResolveService,
    },
    data: {
         authorities: ['ROLE_ADMIN'],
          },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BorrowingUpdateComponent,
    resolve: {
      borrowing: BorrowingRoutingResolveService,
    },
    data: {
          authorities: ['ROLE_ADMIN'],
          },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(borrowingRoute)],
  exports: [RouterModule],
})
export class BorrowingRoutingModule {}
