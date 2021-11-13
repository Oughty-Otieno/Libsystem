import * as dayjs from 'dayjs';
import { IBook } from 'app/entities/book/book.model';
import { IUser } from 'app/entities/user/user.model';

export interface IBorrowing {
  id?: number;
  date_borrowed?: dayjs.Dayjs | null;
  due_date?: dayjs.Dayjs | null;
  return_date?: dayjs.Dayjs | null;
  status?: boolean | null;
  book?: IBook | null;
  user?: IUser | null;
}

export class Borrowing implements IBorrowing {
  constructor(
    public id?: number,
    public date_borrowed?: dayjs.Dayjs | null,
    public due_date?: dayjs.Dayjs | null,
    public return_date?: dayjs.Dayjs | null,
    public status?: boolean | null,
    public book?: IBook | null,
    public user?: IUser | null
  ) {
    this.status = this.status ?? false;
  }
}

export function getBorrowingIdentifier(borrowing: IBorrowing): number | undefined {
  return borrowing.id;
}
