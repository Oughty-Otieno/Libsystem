import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'category',
        data: { pageTitle: 'Categories' },
        loadChildren: () => import('./category/category.module').then(m => m.CategoryModule),
      },
      {
        path: 'book',
        data: { pageTitle: 'Books' },
        loadChildren: () => import('./book/book.module').then(m => m.BookModule),
      },
      {
        path: 'borrowing',
        data: { pageTitle: 'Borrowings' },
        loadChildren: () => import('./borrowing/borrowing.module').then(m => m.BorrowingModule),
      },
      {
        path: 'spaces',
        data: { pageTitle: 'Spaces' },
        loadChildren: () => import('./spaces/spaces.module').then(m => m.SpacesModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
