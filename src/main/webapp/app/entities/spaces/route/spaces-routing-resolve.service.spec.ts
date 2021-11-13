jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISpaces, Spaces } from '../spaces.model';
import { SpacesService } from '../service/spaces.service';

import { SpacesRoutingResolveService } from './spaces-routing-resolve.service';

describe('Service Tests', () => {
  describe('Spaces routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SpacesRoutingResolveService;
    let service: SpacesService;
    let resultSpaces: ISpaces | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SpacesRoutingResolveService);
      service = TestBed.inject(SpacesService);
      resultSpaces = undefined;
    });

    describe('resolve', () => {
      it('should return ISpaces returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSpaces = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSpaces).toEqual({ id: 123 });
      });

      it('should return new ISpaces if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSpaces = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSpaces).toEqual(new Spaces());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Spaces })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSpaces = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSpaces).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
