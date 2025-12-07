import { PersonGetDto } from './models/dtos/person.dtos';

export type PaginatedResponse<T> = {
  data: T[];
  total: number;
};

export type PersonsApiResponse = {
  persons: PersonGetDto[];
  size: number;
};
