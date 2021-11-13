import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISpaces, getSpacesIdentifier } from '../spaces.model';

export type EntityResponseType = HttpResponse<ISpaces>;
export type EntityArrayResponseType = HttpResponse<ISpaces[]>;

@Injectable({ providedIn: 'root' })
export class SpacesService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/spaces');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(spaces: ISpaces): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spaces);
    return this.http
      .post<ISpaces>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(spaces: ISpaces): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spaces);
    return this.http
      .put<ISpaces>(`${this.resourceUrl}/${getSpacesIdentifier(spaces) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(spaces: ISpaces): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spaces);
    return this.http
      .patch<ISpaces>(`${this.resourceUrl}/${getSpacesIdentifier(spaces) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ISpaces>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISpaces[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSpacesToCollectionIfMissing(spacesCollection: ISpaces[], ...spacesToCheck: (ISpaces | null | undefined)[]): ISpaces[] {
    const spaces: ISpaces[] = spacesToCheck.filter(isPresent);
    if (spaces.length > 0) {
      const spacesCollectionIdentifiers = spacesCollection.map(spacesItem => getSpacesIdentifier(spacesItem)!);
      const spacesToAdd = spaces.filter(spacesItem => {
        const spacesIdentifier = getSpacesIdentifier(spacesItem);
        if (spacesIdentifier == null || spacesCollectionIdentifiers.includes(spacesIdentifier)) {
          return false;
        }
        spacesCollectionIdentifiers.push(spacesIdentifier);
        return true;
      });
      return [...spacesToAdd, ...spacesCollection];
    }
    return spacesCollection;
  }

  protected convertDateFromClient(spaces: ISpaces): ISpaces {
    return Object.assign({}, spaces, {
      date: spaces.date?.isValid() ? spaces.date.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? dayjs(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((spaces: ISpaces) => {
        spaces.date = spaces.date ? dayjs(spaces.date) : undefined;
      });
    }
    return res;
  }
}
