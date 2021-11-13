import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BorrowingDetailComponent } from './borrowing-detail.component';

describe('Component Tests', () => {
  describe('Borrowing Management Detail Component', () => {
    let comp: BorrowingDetailComponent;
    let fixture: ComponentFixture<BorrowingDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [BorrowingDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ borrowing: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(BorrowingDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BorrowingDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load borrowing on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.borrowing).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
