import { HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '@dg-environment';
import { Coordinates, Person } from '@dg-types/models/person';
import { ModelGetRequest } from '@dg-types/request.types';
import {
  PaginatedResponse,
  PersonsApiResponse,
} from '@dg-types/response.types';
import { TuiAlertService } from '@taiga-ui/core';
import { catchError, map, Observable, of } from 'rxjs';
import { AbstractModelService } from 'src/app/services/abstract-model.service';

@Injectable({
  providedIn: 'root',
})
export class PersonService extends AbstractModelService<Person> {
  private readonly alertService = inject(TuiAlertService);

  override getModelsList$(
    requestParams: ModelGetRequest<Person>,
    sorting?: { field: string; direction: 'asc' | 'desc' }[]
  ): Observable<PaginatedResponse<Person>> {
    const page = requestParams.pagination?.page ?? 0;
    const pageSize = requestParams.pagination?.pageSize ?? 20;
    const sortParams = (sorting ?? []).map((s) => `${s.field},${s.direction}`);
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(pageSize));
    // apply filters
    Object.entries(requestParams.filters ?? {}).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        params = params.set(key, String(value));
      }
    });
    sortParams.forEach((s) => (params = params.append('sort', s)));
    return this.http
      .get<PersonsApiResponse>(`${environment.personsApiUrl}/persons`, {
        params,
      })
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверные параметры запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open('Ошибка загрузки списка людей', { appearance: 'error' })
              .subscribe();
          }
          return of({ persons: [], size: 0 } as PersonsApiResponse);
        }),
        map((response) => {
          const data = response.persons.map((dao) => Person.fromGetDao(dao)!);
          return {
            data,
            total: response.size,
          } as PaginatedResponse<Person>;
        })
      );
  }

  getCoordinatesList$(): Observable<Coordinates[]> {
    return this.getDependenciesList$('coordinates') as Observable<
      Coordinates[]
    >;
  }

  override createModel$(person: Person): Observable<void> {
    return this.http
      .post<void>(`${environment.personsApiUrl}/persons`, person.asCreateDao())
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверный формат запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 422) {
            this.alertService
              .open('Ошибка валидации', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open('Ошибка создания человека', { appearance: 'error' })
              .subscribe();
          }
          throw error; // Re-throw to maintain error flow
        })
      );
  }

  override removeModel$(person: Person): Observable<void> {
    return this.http
      .delete<void>(`${environment.personsApiUrl}/persons/${person.id}`)
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверный формат запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 422) {
            this.alertService
              .open('Ошибка валидации', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open('Ошибка удаления человека', { appearance: 'error' })
              .subscribe();
          }
          throw error; // Re-throw to maintain error flow
        })
      );
  }

  override updateModel$(updatedPerson: Person): Observable<void> {
    return this.http
      .put<void>(
        `${environment.personsApiUrl}/persons/${updatedPerson.id}`,
        updatedPerson.asCreateDao()
      )
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверный формат запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 404) {
            this.alertService
              .open('Такой человек не найден', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 422) {
            this.alertService
              .open('Ошибка валидации', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open('Ошибка обновления человека', { appearance: 'error' })
              .subscribe();
          }
          throw error; // Re-throw to maintain error flow
        })
      );
  }

  // Special endpoints
  getAverageWeight$(): Observable<number> {
    return this.http
      .get<number>(`${environment.personsApiUrl}/persons/average-weight`)
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверный формат запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open('Ошибка получения среднего веса', { appearance: 'error' })
              .subscribe();
          }
          throw error;
        })
      );
  }

  countByLocation$(x: number, y: number, name: string): Observable<number> {
    const params = new URLSearchParams();
    if (x !== undefined && x !== null) params.set('x', String(x));
    if (y !== undefined && y !== null) params.set('y', String(y));
    if (name !== undefined && name !== null) params.set('name', String(name));
    return this.http
      .get<number>(
        `${
          environment.personsApiUrl
        }/persons/count-by-location?${params.toString()}`
      )
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверный формат запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open('Ошибка подсчета по местоположению', {
                appearance: 'error',
              })
              .subscribe();
          }
          throw error;
        })
      );
  }

  getByHeight$(minHeight: number): Observable<Person[]> {
    return this.http
      .get<Person[]>(
        `${
          environment.personsApiUrl
        }/persons/by-height?minHeight=${encodeURIComponent(String(minHeight))}`
      )
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверный формат запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open('Ошибка получения людей по росту', { appearance: 'error' })
              .subscribe();
          }
          throw error;
        })
      );
  }

  countByHairColor$(hairColor: string): Observable<number> {
    return this.http
      .get<number>(
        `${environment.demographyApiUrl}/hair-color/${encodeURIComponent(
          hairColor
        )}`
      )
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверный формат запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open('Ошибка подсчета по цвету волос', { appearance: 'error' })
              .subscribe();
          }
          throw error;
        })
      );
  }

  getEyesNationalityPercentage$(
    nationality: string,
    eyeColor: string
  ): Observable<number> {
    return this.http
      .get<number>(
        `${environment.demographyApiUrl}/nationality/${encodeURIComponent(
          nationality
        )}/eye-color/${encodeURIComponent(eyeColor)}/percentage`
      )
      .pipe(
        catchError((error) => {
          if (error?.status === 400) {
            this.alertService
              .open('Неверный формат запроса', { appearance: 'error' })
              .subscribe();
          } else if (error?.status === 500) {
            this.alertService
              .open('Внутренняя ошибка сервера', { appearance: 'error' })
              .subscribe();
          } else {
            this.alertService
              .open(
                'Ошибка получения процента по национальности и цвету глаз',
                { appearance: 'error' }
              )
              .subscribe();
          }
          throw error;
        })
      );
  }
}
