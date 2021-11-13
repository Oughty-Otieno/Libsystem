import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BorrowingComponent } from './list/borrowing.component';
import { BorrowingDetailComponent } from './detail/borrowing-detail.component';
import { BorrowingUpdateComponent } from './update/borrowing-update.component';
import { BorrowingDeleteDialogComponent } from './delete/borrowing-delete-dialog.component';
import { BorrowingRoutingModule } from './route/borrowing-routing.module';

@NgModule({
  imports: [SharedModule, BorrowingRoutingModule],
  declarations: [BorrowingComponent, BorrowingDetailComponent, BorrowingUpdateComponent, BorrowingDeleteDialogComponent],
  entryComponents: [BorrowingDeleteDialogComponent],
})
export class BorrowingModule {}
