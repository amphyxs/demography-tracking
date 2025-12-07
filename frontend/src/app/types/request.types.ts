import { Model } from './models/model';

export type Filters<TEntity> = Record<string, string>;

export type SortedRequest<TEntity> = {
  field: keyof TEntity | string;
  direction: SortDirection;
};

export type PaginatedRequest = {
  page: number;
  pageSize: number;
};

export type ModelGetRequest<T extends Model> = {
  filters?: Filters<T>;
  sort?: SortedRequest<T>;
  pagination?: PaginatedRequest;
};

export enum SortDirection {
  Ascending = 1,
  Descending = -1,
}
