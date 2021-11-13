import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBorrowing } from '../borrowing.model';

@Component({
  selector: 'jhi-borrowing-detail',
  templateUrl: './borrowing-detail.component.html',
})
export class BorrowingDetailComponent implements OnInit {
  borrowing: IBorrowing | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ borrowing }) => {
      this.borrowing = borrowing;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
