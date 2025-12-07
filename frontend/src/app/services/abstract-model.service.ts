import { HttpClient } from '@angular/common/http';
import { inject } from '@angular/core';
import { Model } from '@dg-types/models/model';
import {
  Filters,
  ModelGetRequest,
  PaginatedRequest,
  SortedRequest,
} from '@dg-types/request.types';
import { PaginatedResponse } from '@dg-types/response.types';
import {
  BehaviorSubject,
  distinct,
  map,
  mergeMap,
  Observable,
  take,
  toArray,
} from 'rxjs';

export abstract class AbstractModelService<T extends Model> {
  protected readonly http = inject(HttpClient);

  readonly refreshModelsList$ = new BehaviorSubject(null);

  abstract getModelsList$(
    requestParams: ModelGetRequest<T>
  ): Observable<PaginatedResponse<T>>;

  abstract createModel$(model: T): Observable<void>;

  abstract updateModel$(updatedModel: T): Observable<void>;

  abstract removeModel$(model: T): Observable<void>;

  protected paginate<T>(data: T[], pagination: PaginatedRequest): T[] {
    const start = pagination.page * pagination.pageSize;
    const end = Math.min(
      (pagination.page + 1) * pagination.pageSize,
      data.length
    );

    return data.slice(start, end);
  }

  protected sort<T>(data: T[], sort: SortedRequest<T>): T[] {
    return data.sort((a, b) => {
      const key = sort.field as keyof T;
      return (
        (a[key]?.toString() ?? '').localeCompare(b[key]?.toString() ?? '') *
        sort.direction
      );
    });
  }

  protected filter<T>(data: T[], filters: Filters<T>): T[] {
    return data.filter((item) => {
      return Object.entries(filters).every(([_key, _value]) => {
        const key = _key as keyof T;
        const value = _value as string | undefined;
        return (
          item[key]?.toString().includes(value ?? '') ?? value === undefined
        );
      });
    });
  }

  protected getDependenciesList$(
    dependencyName: keyof T
  ): Observable<unknown[]> {
    return this.getModelsList$({}).pipe(
      take(1),
      map((response) => response.data),
      mergeMap((models) => models),
      map((models) => models[dependencyName]),
      distinct((dependency) => dependency?.toString()),
      map((dependency) => dependency as unknown),
      toArray()
    );
  }

  protected processModelsList(
    models: T[],
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    requestParams: ModelGetRequest<T>
  ): PaginatedResponse<T> {
    // eslint-disable-next-line prefer-const
    let data = models;
    const total = data.length;

    // Вернуть, если делаем пагинацию, фильтрацию на фронте
    // if (requestParams.pagination) {
    //   data = this.paginate(data, requestParams.pagination);
    // }

    // if (requestParams.sort) {
    //   data = this.sort(data, requestParams.sort);
    // }

    // if (requestParams.filters) {
    //   data = this.filter(data, requestParams.filters);
    // }

    return {
      total,
      data,
    };
  }
}
