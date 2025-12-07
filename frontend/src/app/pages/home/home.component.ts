import { AsyncPipe, NgForOf } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  DestroyRef,
  inject,
  INJECTOR,
  input,
  OnInit,
  signal,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import {
  FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
} from '@angular/forms';
import {
  PersonFormComponent,
  PersonFormDialogContext,
} from '@dg-components/person-form/person-form.component';
import { ActionWithModel } from '@dg-types/action-with-model.types';
import { Person } from '@dg-types/models/person';
import {
  TuiTable,
  TuiTableFilters,
  TuiTablePagination,
} from '@taiga-ui/addon-table';
import { TuiDay, tuiTakeUntilDestroyed } from '@taiga-ui/cdk';
import {
  TuiButton,
  TuiCalendar,
  TuiDataList,
  tuiDateFormatProvider,
  TuiDialogService,
  TuiDropdown,
  TuiLoader,
  TuiTextfield,
} from '@taiga-ui/core';
import {
  TuiAccordion,
  TuiChevron,
  TuiChip,
  TuiDataListWrapper,
  TuiInputDate,
  tuiInputDateOptionsProviderNew,
  TuiSelect,
  TuiStatus,
} from '@taiga-ui/kit';
import {
  TuiInputModule,
  TuiInputNumberModule,
  TuiTextfieldControllerModule,
} from '@taiga-ui/legacy';
import { PolymorpheusComponent } from '@taiga-ui/polymorpheus';
import { merge, startWith, switchMap, tap } from 'rxjs';
import { PersonService } from 'src/app/services/person.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    TuiTableFilters,
    AsyncPipe,
    FormsModule,
    NgForOf,
    ReactiveFormsModule,
    TuiButton,
    TuiDropdown,
    TuiInputModule,
    TuiInputNumberModule,
    TuiLoader,
    TuiTable,
    TuiTablePagination,
    TuiTextfieldControllerModule,
    TuiTextfield,
    TuiAccordion,
    TuiStatus,
    TuiButton,
    TuiDataList,
    TuiDataListWrapper,
    TuiTextfield,
    TuiSelect,
    TuiChevron,
    TuiChip,
    TuiInputDate,
    TuiCalendar,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.less',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    tuiInputDateOptionsProviderNew({
      valueTransformer: {
        fromControlValue: (value: string | null): TuiDay | null =>
          value ? TuiDay.fromLocalNativeDate(new Date(value)) : null,
        toControlValue: (value: TuiDay | null): string | null =>
          value ? value.toJSON() : null, // This gives yyyy-MM-dd format
      },
    }),
    tuiDateFormatProvider({ mode: 'YMD', separator: '.' }),
  ],
})
export class HomeComponent implements OnInit {
  private readonly personService = inject(PersonService);
  private readonly dialogService = inject(TuiDialogService);
  private readonly injector = inject(INJECTOR);
  private readonly destroyRef = inject(DestroyRef);

  private readonly PEOPLE_LIST_REFRESH_INTERVAL_MS = 5000;

  readonly inputData = input<Person[] | null>(null);
  readonly hideControls = input<boolean>(false);

  readonly filtersForm = new FormGroup({
    idFilterMode: new FormControl<'eq' | 'range'>('eq'),
    idEqValue: new FormControl<string>(''),
    idGtValue: new FormControl<string>(''),
    idLtValue: new FormControl<string>(''),
    nameValue: new FormControl<string>(''),
    heightFilterMode: new FormControl<'eq' | 'range'>('eq'),
    heightEqValue: new FormControl<string>(''),
    heightGtValue: new FormControl<string>(''),
    heightLtValue: new FormControl<string>(''),
    weightFilterMode: new FormControl<'eq' | 'range'>('eq'),
    weightEqValue: new FormControl<string>(''),
    weightGtValue: new FormControl<string>(''),
    weightLtValue: new FormControl<string>(''),
    nationalityValue: new FormControl<string>(''),
    birthdayFilterMode: new FormControl<'eq' | 'range'>('eq'),
    birthdayEqValue: new FormControl<string>(''),
    birthdayGtValue: new FormControl<string>(''),
    birthdayLtValue: new FormControl<string>(''),
  });
  readonly filters$ = this.filtersForm.valueChanges.pipe(
    startWith(this.filtersForm.value)
  );
  readonly modeChips = ['eq', 'lt', 'gt'] as const;

  getModeSymbol(mode: 'eq' | 'lt' | 'gt'): string {
    switch (mode) {
      case 'eq':
        return '=';
      case 'lt':
        return '<';
      case 'gt':
        return '>';
    }
  }
  readonly isLoading = signal(true);
  readonly page = signal(0);
  readonly page$ = toObservable(this.page);
  readonly totalItems = signal(0);
  readonly data = signal<Person[]>([]);
  readonly pageSize = signal(10);
  readonly pageSize$ = toObservable(this.pageSize);
  readonly sort = signal<{ field: string; direction: 'asc' | 'desc' }[]>([]);
  readonly sort$ = toObservable(this.sort);
  readonly personColumns = [
    'id',
    'name',
    'coordinatesX',
    'coordinatesY',
    'creationDate',
    'height',
    'birthday',
    'weight',
    'nationality',
    'locationX',
    'locationY',
    'locationName',
  ] as const;
  readonly columns = [...this.personColumns, 'actions'] as const;
  readonly columnNames = {
    id: 'ID',
    name: 'Name',
    coordinatesX: 'Coord X',
    coordinatesY: 'Coord Y',
    creationDate: 'Creation Date',
    height: 'Height',
    birthday: 'Birthday',
    weight: 'Weight',
    nationality: 'Nationality',
    locationX: 'Loc X',
    locationY: 'Loc Y',
    locationName: 'Loc Name',
    actions: 'Actions',
  };
  readonly actionWithPerson = ActionWithModel;

  ngOnInit(): void {
    const inputData = this.inputData();
    if (inputData) {
      this.data.set(inputData);
      this.totalItems.set(inputData.length);
      this.isLoading.set(false);
      return;
    }

    merge(
      this.personService.refreshModelsList$,
      this.page$,
      this.pageSize$,
      this.sort$,
      this.filters$
    )
      .pipe(
        tuiTakeUntilDestroyed(this.destroyRef),
        tap((event) => {
          if (event && typeof event === 'object') {
            this.page.set(0);
          }
        }),
        switchMap(() =>
          this.personService.getModelsList$(
            {
              filters: this.buildFilters(),
              pagination: {
                page: this.page(),
                pageSize: this.pageSize(),
              },
            },
            this.sort()
          )
        )
      )
      .subscribe((response) => {
        this.data.set(response.data);
        this.totalItems.set(response.total);
        this.isLoading.set(false);
      });
  }

  onPaginationChange(event: { page: number; size: number }): void {
    this.page.set(event.page);
    this.pageSize.set(event.size);
  }

  private mapColumnToBackendField(column: string): string {
    switch (column) {
      case 'coordinatesX':
        return 'coordinates.x';
      case 'coordinatesY':
        return 'coordinates.y';
      case 'locationX':
        return 'location.x';
      case 'locationY':
        return 'location.y';
      case 'locationName':
        return 'location.name';
      default:
        return column;
    }
  }

  toggleSort(column: string, event?: MouseEvent): void {
    const backendField = this.mapColumnToBackendField(column);
    const isMulti = !!(event?.ctrlKey || event?.metaKey);
    const currentSort = this.sort();

    if (!isMulti) {
      const existing = currentSort.find((s) => s.field === backendField);
      if (!existing) {
        this.sort.set([{ field: backendField, direction: 'asc' }]);
        return;
      }
      if (existing.direction === 'asc') {
        this.sort.set([{ field: backendField, direction: 'desc' }]);
      } else {
        this.sort.set([]);
      }
      return;
    }

    // Multi-sort with Ctrl/Cmd: toggle within the array
    const idx = currentSort.findIndex((s) => s.field === backendField);
    if (idx === -1) {
      this.sort.set([
        ...currentSort,
        { field: backendField, direction: 'asc' },
      ]);
      return;
    }
    const item = currentSort[idx];
    if (item.direction === 'asc') {
      const next = [...currentSort];
      next[idx] = { field: backendField, direction: 'desc' };
      this.sort.set(next);
    } else {
      const next = currentSort.filter((_, i) => i !== idx);
      this.sort.set(next);
    }
  }

  getSortMeta(column: string): {
    direction: 'asc' | 'desc' | null;
    index: number | null;
  } {
    const backendField = this.mapColumnToBackendField(column);
    const idx = this.sort().findIndex((s) => s.field === backendField);
    if (idx === -1) {
      return { direction: null, index: null };
    }
    return { direction: this.sort()[idx].direction, index: idx + 1 };
  }

  getCellValue(item: Person, column: string): unknown {
    switch (column) {
      case 'coordinatesX':
        return item.coordinates?.x;
      case 'coordinatesY':
        return item.coordinates?.y;
      case 'locationX':
        return item.location?.x;
      case 'locationY':
        return item.location?.y;
      case 'locationName':
        return item.location?.name;
      default:
        return (item as unknown as Record<string, unknown>)[column];
    }
  }

  private buildFilters(): Record<string, string> {
    const formValues = this.filtersForm.value;
    const result: Record<string, string> = {};

    const applyFilter = (
      field: string,
      mode: 'eq' | 'range' | null | undefined,
      eqValue: string | null | undefined,
      gtValue: string | null | undefined,
      ltValue: string | null | undefined
    ) => {
      if (mode === 'eq' && eqValue) {
        result[field] = eqValue;
      } else if (mode === 'range') {
        if (gtValue) {
          result[`${field}[gt]`] = gtValue;
        }
        if (ltValue) {
          result[`${field}[lt]`] = ltValue;
        }
      }
    };

    applyFilter(
      'id',
      formValues.idFilterMode,
      formValues.idEqValue,
      formValues.idGtValue,
      formValues.idLtValue
    );

    if (formValues.nameValue) {
      result['name'] = formValues.nameValue;
    }

    applyFilter(
      'height',
      formValues.heightFilterMode,
      formValues.heightEqValue,
      formValues.heightGtValue,
      formValues.heightLtValue
    );

    applyFilter(
      'weight',
      formValues.weightFilterMode,
      formValues.weightEqValue,
      formValues.weightGtValue,
      formValues.weightLtValue
    );

    if (formValues.nationalityValue) {
      result['nationality'] = formValues.nationalityValue;
    }

    applyFilter(
      'birthday',
      formValues.birthdayFilterMode,
      formValues.birthdayEqValue,
      formValues.birthdayGtValue,
      formValues.birthdayLtValue
    );

    return result;
  }

  edit(item: Person): void {
    this.dialogService
      .open<{ item: Person; mode: ActionWithModel }>(
        new PolymorpheusComponent(PersonFormComponent, this.injector),
        {
          data: {
            item,
            mode: ActionWithModel.Update,
          } as PersonFormDialogContext,
          dismissible: true,
          label: 'Edit person',
        }
      )
      .pipe(tuiTakeUntilDestroyed(this.destroyRef))
      .subscribe({
        complete: () => this.personService.refreshModelsList$.next(null),
      });
  }

  remove(item: Person): void {
    this.personService.removeModel$(item).subscribe({
      next: () => this.personService.refreshModelsList$.next(null),
      error: (error) => {
        // Error handling is already done in the service
        console.error('Error deleting person:', error);
      },
    });
  }

  view(item: Person): void {
    this.dialogService
      .open<{ item: Person; mode: ActionWithModel }>(
        new PolymorpheusComponent(PersonFormComponent, this.injector),
        {
          data: {
            item,
            mode: ActionWithModel.Read,
          },
          dismissible: true,
          label: 'View person',
        }
      )
      .pipe(tuiTakeUntilDestroyed(this.destroyRef))
      .subscribe();
  }

  createNew(): void {
    this.dialogService
      .open<{ mode: ActionWithModel }>(
        new PolymorpheusComponent(PersonFormComponent, this.injector),
        {
          data: {
            mode: ActionWithModel.Create,
          } as PersonFormDialogContext,
          dismissible: true,
          label: 'Create person',
        }
      )
      .pipe(tuiTakeUntilDestroyed(this.destroyRef))
      .subscribe({
        complete: () => this.personService.refreshModelsList$.next(null),
      });
  }
  checkUserCanPerformActionWithPerson(
    action: ActionWithModel,
    item: Person
  ): boolean {
    return !!action || !!item || true;
  }
}
