jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IBorrowing, Borrowing } from '../borrowing.model';
import { BorrowingService } from '../service/borrowing.service';

import { BorrowingRoutingResolveService } from './borrowing-routing-resolve.service';

describe('Service Tests', () => {
  describe('Borrowing routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: BorrowingRoutingResolveService;
    let service: BorrowingService;
    let resultBorrowing: IBorrowing | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(BorrowingRoutingResolveService);
      service = TestBed.inject(BorrowingService);
      resultBorrowing = undefined;
    });

    describe('resolve', () => {
      it('should return IBorrowing returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBorrowing = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultBorrowing).toEqual({ id: 123 });
      });

      it('should return new IBorrowing if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBorrowing = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultBorrowing).toEqual(new Borrowing());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Borrowing })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBorrowing = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultBorrowing).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
