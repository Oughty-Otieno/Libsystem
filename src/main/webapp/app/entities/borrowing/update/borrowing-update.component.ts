import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBorrowing, Borrowing } from '../borrowing.model';
import { BorrowingService } from '../service/borrowing.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-borrowing-update',
  templateUrl: './borrowing-update.component.html',
})
export class BorrowingUpdateComponent implements OnInit {
  isSaving = false;

  booksCollection: IBook[] = [];
  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    date_borrowed: [],
    due_date: [],
    return_date: [],
    status: [],
    book: [],
    user: [],
  });

  constructor(
    protected borrowingService: BorrowingService,
    protected bookService: BookService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ borrowing }) => {
      this.updateForm(borrowing);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const borrowing = this.createFromForm();
    if (borrowing.id !== undefined) {
      this.subscribeToSaveResponse(this.borrowingService.update(borrowing));
    } else {
      this.subscribeToSaveResponse(this.borrowingService.create(borrowing));
    }
  }

  trackBookById(index: number, item: IBook): number {
    return item.id!;
  }

  trackUserById(index: number, item: IUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBorrowing>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(borrowing: IBorrowing): void {
    this.editForm.patchValue({
      id: borrowing.id,
      date_borrowed: borrowing.date_borrowed,
      due_date: borrowing.due_date,
      return_date: borrowing.return_date,
      status: borrowing.status,
      book: borrowing.book,
      user: borrowing.user,
    });

    this.booksCollection = this.bookService.addBookToCollectionIfMissing(this.booksCollection, borrowing.book);
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, borrowing.user);
  }

  protected loadRelationshipsOptions(): void {
    this.bookService
      .query({ filter: 'id-is-null' })
      .pipe(map((res: HttpResponse<IBook[]>) => res.body ?? []))
      .pipe(map((books: IBook[]) => this.bookService.addBookToCollectionIfMissing(books, this.editForm.get('book')!.value)))
      .subscribe((books: IBook[]) => (this.booksCollection = books));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): IBorrowing {
    return {
      ...new Borrowing(),
      id: this.editForm.get(['id'])!.value,
      date_borrowed: this.editForm.get(['date_borrowed'])!.value,
      due_date: this.editForm.get(['due_date'])!.value,
      return_date: this.editForm.get(['return_date'])!.value,
      status: this.editForm.get(['status'])!.value,
      book: this.editForm.get(['book'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }
}
