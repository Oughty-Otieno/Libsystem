import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISpaces } from '../spaces.model';

@Component({
  selector: 'jhi-spaces-detail',
  templateUrl: './spaces-detail.component.html',
})
export class SpacesDetailComponent implements OnInit {
  spaces: ISpaces | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spaces }) => {
      this.spaces = spaces;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
