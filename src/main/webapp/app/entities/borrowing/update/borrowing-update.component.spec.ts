jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { BorrowingService } from '../service/borrowing.service';
import { IBorrowing, Borrowing } from '../borrowing.model';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { BorrowingUpdateComponent } from './borrowing-update.component';

describe('Component Tests', () => {
  describe('Borrowing Management Update Component', () => {
    let comp: BorrowingUpdateComponent;
    let fixture: ComponentFixture<BorrowingUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let borrowingService: BorrowingService;
    let bookService: BookService;
    let userService: UserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BorrowingUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BorrowingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BorrowingUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      borrowingService = TestBed.inject(BorrowingService);
      bookService = TestBed.inject(BookService);
      userService = TestBed.inject(UserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Book query and add missing value', () => {
        const borrowing: IBorrowing = { id: 456 };
        const book: IBook = { id: 71097 };
        borrowing.book = book;

        const bookCollection: IBook[] = [{ id: 92915 }];
        jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
        const additionalBooks = [book];
        const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
        jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ borrowing });
        comp.ngOnInit();

        expect(bookService.query).toHaveBeenCalled();
        expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(bookCollection, ...additionalBooks);
        expect(comp.booksSharedCollection).toEqual(expectedCollection);
      });

      it('Should call User query and add missing value', () => {
        const borrowing: IBorrowing = { id: 456 };
        const user: IUser = { id: 28507 };
        borrowing.user = user;

        const userCollection: IUser[] = [{ id: 93481 }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ borrowing });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const borrowing: IBorrowing = { id: 456 };
        const book: IBook = { id: 3351 };
        borrowing.book = book;
        const user: IUser = { id: 85160 };
        borrowing.user = user;

        activatedRoute.data = of({ borrowing });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(borrowing));
        expect(comp.booksSharedCollection).toContain(book);
        expect(comp.usersSharedCollection).toContain(user);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Borrowing>>();
        const borrowing = { id: 123 };
        jest.spyOn(borrowingService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ borrowing });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: borrowing }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(borrowingService.update).toHaveBeenCalledWith(borrowing);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Borrowing>>();
        const borrowing = new Borrowing();
        jest.spyOn(borrowingService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ borrowing });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: borrowing }));
        saveSubject.complete();

        // THEN
        expect(borrowingService.create).toHaveBeenCalledWith(borrowing);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Borrowing>>();
        const borrowing = { id: 123 };
        jest.spyOn(borrowingService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ borrowing });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(borrowingService.update).toHaveBeenCalledWith(borrowing);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackBookById', () => {
        it('Should return tracked Book primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackBookById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
