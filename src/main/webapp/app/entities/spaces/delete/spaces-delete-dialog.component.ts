import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISpaces } from '../spaces.model';
import { SpacesService } from '../service/spaces.service';

@Component({
  templateUrl: './spaces-delete-dialog.component.html',
})
export class SpacesDeleteDialogComponent {
  spaces?: ISpaces;

  constructor(protected spacesService: SpacesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.spacesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
