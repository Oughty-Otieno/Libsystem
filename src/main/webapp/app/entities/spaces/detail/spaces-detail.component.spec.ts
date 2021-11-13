import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SpacesDetailComponent } from './spaces-detail.component';

describe('Component Tests', () => {
  describe('Spaces Management Detail Component', () => {
    let comp: SpacesDetailComponent;
    let fixture: ComponentFixture<SpacesDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SpacesDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ spaces: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SpacesDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SpacesDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load spaces on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.spaces).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
