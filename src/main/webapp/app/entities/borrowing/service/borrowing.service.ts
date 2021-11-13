import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBorrowing, getBorrowingIdentifier } from '../borrowing.model';

export type EntityResponseType = HttpResponse<IBorrowing>;
export type EntityArrayResponseType = HttpResponse<IBorrowing[]>;

@Injectable({ providedIn: 'root' })
export class BorrowingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/borrowings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(borrowing: IBorrowing): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(borrowing);
    return this.http
      .post<IBorrowing>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(borrowing: IBorrowing): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(borrowing);
    return this.http
      .put<IBorrowing>(`${this.resourceUrl}/${getBorrowingIdentifier(borrowing) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(borrowing: IBorrowing): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(borrowing);
    return this.http
      .patch<IBorrowing>(`${this.resourceUrl}/${getBorrowingIdentifier(borrowing) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBorrowing>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBorrowing[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBorrowingToCollectionIfMissing(
    borrowingCollection: IBorrowing[],
    ...borrowingsToCheck: (IBorrowing | null | undefined)[]
  ): IBorrowing[] {
    const borrowings: IBorrowing[] = borrowingsToCheck.filter(isPresent);
    if (borrowings.length > 0) {
      const borrowingCollectionIdentifiers = borrowingCollection.map(borrowingItem => getBorrowingIdentifier(borrowingItem)!);
      const borrowingsToAdd = borrowings.filter(borrowingItem => {
        const borrowingIdentifier = getBorrowingIdentifier(borrowingItem);
        if (borrowingIdentifier == null || borrowingCollectionIdentifiers.includes(borrowingIdentifier)) {
          return false;
        }
        borrowingCollectionIdentifiers.push(borrowingIdentifier);
        return true;
      });
      return [...borrowingsToAdd, ...borrowingCollection];
    }
    return borrowingCollection;
  }

  protected convertDateFromClient(borrowing: IBorrowing): IBorrowing {
    return Object.assign({}, borrowing, {
      date_borrowed: borrowing.date_borrowed?.isValid() ? borrowing.date_borrowed.format(DATE_FORMAT) : undefined,
      due_date: borrowing.due_date?.isValid() ? borrowing.due_date.format(DATE_FORMAT) : undefined,
      return_date: borrowing.return_date?.isValid() ? borrowing.return_date.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date_borrowed = res.body.date_borrowed ? dayjs(res.body.date_borrowed) : undefined;
      res.body.due_date = res.body.due_date ? dayjs(res.body.due_date) : undefined;
      res.body.return_date = res.body.return_date ? dayjs(res.body.return_date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((borrowing: IBorrowing) => {
        borrowing.date_borrowed = borrowing.date_borrowed ? dayjs(borrowing.date_borrowed) : undefined;
        borrowing.due_date = borrowing.due_date ? dayjs(borrowing.due_date) : undefined;
        borrowing.return_date = borrowing.return_date ? dayjs(borrowing.return_date) : undefined;
      });
    }
    return res;
  }
}
