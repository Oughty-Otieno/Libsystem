import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBorrowing } from '../borrowing.model';
import { BorrowingService } from '../service/borrowing.service';

@Component({
  templateUrl: './borrowing-delete-dialog.component.html',
})
export class BorrowingDeleteDialogComponent {
  borrowing?: IBorrowing;

  constructor(protected borrowingService: BorrowingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.borrowingService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
