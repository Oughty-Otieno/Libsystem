import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBorrowing, Borrowing } from '../borrowing.model';
import { BorrowingService } from '../service/borrowing.service';

@Injectable({ providedIn: 'root' })
export class BorrowingRoutingResolveService implements Resolve<IBorrowing> {
  constructor(protected service: BorrowingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBorrowing> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((borrowing: HttpResponse<Borrowing>) => {
          if (borrowing.body) {
            return of(borrowing.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Borrowing());
  }
}
