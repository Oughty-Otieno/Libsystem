import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ISpaces, Spaces } from '../spaces.model';

import { SpacesService } from './spaces.service';

describe('Service Tests', () => {
  describe('Spaces Service', () => {
    let service: SpacesService;
    let httpMock: HttpTestingController;
    let elemDefault: ISpaces;
    let expectedResult: ISpaces | ISpaces[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SpacesService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        date: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            date: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Spaces', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            date: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.create(new Spaces()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Spaces', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            date: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Spaces', () => {
        const patchObject = Object.assign(
          {
            date: currentDate.format(DATE_FORMAT),
          },
          new Spaces()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Spaces', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            date: currentDate.format(DATE_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Spaces', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSpacesToCollectionIfMissing', () => {
        it('should add a Spaces to an empty array', () => {
          const spaces: ISpaces = { id: 123 };
          expectedResult = service.addSpacesToCollectionIfMissing([], spaces);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(spaces);
        });

        it('should not add a Spaces to an array that contains it', () => {
          const spaces: ISpaces = { id: 123 };
          const spacesCollection: ISpaces[] = [
            {
              ...spaces,
            },
            { id: 456 },
          ];
          expectedResult = service.addSpacesToCollectionIfMissing(spacesCollection, spaces);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Spaces to an array that doesn't contain it", () => {
          const spaces: ISpaces = { id: 123 };
          const spacesCollection: ISpaces[] = [{ id: 456 }];
          expectedResult = service.addSpacesToCollectionIfMissing(spacesCollection, spaces);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(spaces);
        });

        it('should add only unique Spaces to an array', () => {
          const spacesArray: ISpaces[] = [{ id: 123 }, { id: 456 }, { id: 17579 }];
          const spacesCollection: ISpaces[] = [{ id: 123 }];
          expectedResult = service.addSpacesToCollectionIfMissing(spacesCollection, ...spacesArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const spaces: ISpaces = { id: 123 };
          const spaces2: ISpaces = { id: 456 };
          expectedResult = service.addSpacesToCollectionIfMissing([], spaces, spaces2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(spaces);
          expect(expectedResult).toContain(spaces2);
        });

        it('should accept null and undefined values', () => {
          const spaces: ISpaces = { id: 123 };
          expectedResult = service.addSpacesToCollectionIfMissing([], null, spaces, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(spaces);
        });

        it('should return initial array if no Spaces is added', () => {
          const spacesCollection: ISpaces[] = [{ id: 123 }];
          expectedResult = service.addSpacesToCollectionIfMissing(spacesCollection, undefined, null);
          expect(expectedResult).toEqual(spacesCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
