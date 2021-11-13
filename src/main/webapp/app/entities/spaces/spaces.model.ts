import * as dayjs from 'dayjs';
import { IUser } from 'app/entities/user/user.model';

export interface ISpaces {
  id?: number;
  date?: dayjs.Dayjs | null;
  user?: IUser | null;
}

export class Spaces implements ISpaces {
  constructor(public id?: number, public date?: dayjs.Dayjs | null, public user?: IUser | null) {}
}

export function getSpacesIdentifier(spaces: ISpaces): number | undefined {
  return spaces.id;
}
