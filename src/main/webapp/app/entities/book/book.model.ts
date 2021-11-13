import { ICategory } from 'app/entities/category/category.model';

export interface IBook {
  id?: number;
  title?: string | null;
  author?: string | null;
  fine_amount?: number | null;
  publisher?: string | null;
  quantity?: number | null;
  category?: ICategory | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public title?: string | null,
    public author?: string | null,
    public fine_amount?: number | null,
    public publisher?: string | null,
    public quantity?: number | null,
    public category?: ICategory | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
