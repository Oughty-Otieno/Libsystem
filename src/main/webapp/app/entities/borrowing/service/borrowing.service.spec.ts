import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IBorrowing, Borrowing } from '../borrowing.model';

import { BorrowingService } from './borrowing.service';

describe('Service Tests', () => {
  describe('Borrowing Service', () => {
    let service: BorrowingService;
    let httpMock: HttpTestingController;
    let elemDefault: IBorrowing;
    let expectedResult: IBorrowing | IBorrowing[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(BorrowingService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        date_borrowed: currentDate,
        due_date: currentDate,
        return_date: currentDate,
        status: false,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            date_borrowed: currentDate.format(DATE_FORMAT),
            due_date: currentDate.format(DATE_FORMAT),
            return_date: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Borrowing', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            date_borrowed: currentDate.format(DATE_FORMAT),
            due_date: currentDate.format(DATE_FORMAT),
            return_date: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date_borrowed: currentDate,
            due_date: currentDate,
            return_date: currentDate,
          },
          returnedFromService
        );

        service.create(new Borrowing()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Borrowing', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            date_borrowed: currentDate.format(DATE_FORMAT),
            due_date: currentDate.format(DATE_FORMAT),
            return_date: currentDate.format(DATE_FORMAT),
            status: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date_borrowed: currentDate,
            due_date: currentDate,
            return_date: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Borrowing', () => {
        const patchObject = Object.assign(
          {
            date_borrowed: currentDate.format(DATE_FORMAT),
            due_date: currentDate.format(DATE_FORMAT),
            return_date: currentDate.format(DATE_FORMAT),
          },
          new Borrowing()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            date_borrowed: currentDate,
            due_date: currentDate,
            return_date: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Borrowing', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            date_borrowed: currentDate.format(DATE_FORMAT),
            due_date: currentDate.format(DATE_FORMAT),
            return_date: currentDate.format(DATE_FORMAT),
            status: true,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date_borrowed: currentDate,
            due_date: currentDate,
            return_date: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Borrowing', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addBorrowingToCollectionIfMissing', () => {
        it('should add a Borrowing to an empty array', () => {
          const borrowing: IBorrowing = { id: 123 };
          expectedResult = service.addBorrowingToCollectionIfMissing([], borrowing);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(borrowing);
        });

        it('should not add a Borrowing to an array that contains it', () => {
          const borrowing: IBorrowing = { id: 123 };
          const borrowingCollection: IBorrowing[] = [
            {
              ...borrowing,
            },
            { id: 456 },
          ];
          expectedResult = service.addBorrowingToCollectionIfMissing(borrowingCollection, borrowing);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Borrowing to an array that doesn't contain it", () => {
          const borrowing: IBorrowing = { id: 123 };
          const borrowingCollection: IBorrowing[] = [{ id: 456 }];
          expectedResult = service.addBorrowingToCollectionIfMissing(borrowingCollection, borrowing);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(borrowing);
        });

        it('should add only unique Borrowing to an array', () => {
          const borrowingArray: IBorrowing[] = [{ id: 123 }, { id: 456 }, { id: 9368 }];
          const borrowingCollection: IBorrowing[] = [{ id: 123 }];
          expectedResult = service.addBorrowingToCollectionIfMissing(borrowingCollection, ...borrowingArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const borrowing: IBorrowing = { id: 123 };
          const borrowing2: IBorrowing = { id: 456 };
          expectedResult = service.addBorrowingToCollectionIfMissing([], borrowing, borrowing2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(borrowing);
          expect(expectedResult).toContain(borrowing2);
        });

        it('should accept null and undefined values', () => {
          const borrowing: IBorrowing = { id: 123 };
          expectedResult = service.addBorrowingToCollectionIfMissing([], null, borrowing, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(borrowing);
        });

        it('should return initial array if no Borrowing is added', () => {
          const borrowingCollection: IBorrowing[] = [{ id: 123 }];
          expectedResult = service.addBorrowingToCollectionIfMissing(borrowingCollection, undefined, null);
          expect(expectedResult).toEqual(borrowingCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
