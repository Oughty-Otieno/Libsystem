import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpaces, Spaces } from '../spaces.model';
import { SpacesService } from '../service/spaces.service';

@Injectable({ providedIn: 'root' })
export class SpacesRoutingResolveService implements Resolve<ISpaces> {
  constructor(protected service: SpacesService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISpaces> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((spaces: HttpResponse<Spaces>) => {
          if (spaces.body) {
            return of(spaces.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Spaces());
  }
}
