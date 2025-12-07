import { KeyValuePipe, NgForOf } from '@angular/common';
import {
  Component,
  DestroyRef,
  inject,
  INJECTOR,
  signal,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { FormControl, FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterModule,
} from '@angular/router';
import { PersonFormComponent } from '@dg-components/person-form/person-form.component';
import { ActionWithModel } from '@dg-types/action-with-model.types';
import { Person } from '@dg-types/models/person';
import { TuiRepeatTimes, tuiTakeUntilDestroyed } from '@taiga-ui/cdk';
import {
  TuiAlertService,
  TuiAppearance,
  TuiButton,
  TuiDataList,
  TuiDialogContext,
  TuiDialogService,
  TuiDropdown,
  TuiIcon,
  TuiRoot,
  TuiSurface,
  TuiTextfield,
  TuiTitle,
} from '@taiga-ui/core';
import {
  TuiAvatar,
  TuiBadge,
  TuiBadgeNotification,
  TuiChevron,
  TuiDataListDropdownManager,
  TuiFade,
  TuiSwitch,
  TuiTabs,
} from '@taiga-ui/kit';
import { TuiCardLarge, TuiHeader, TuiNavigation } from '@taiga-ui/layout';
import {
  PolymorpheusComponent,
  PolymorpheusContent,
} from '@taiga-ui/polymorpheus';
import { Observer } from 'rxjs';
import { PersonService } from 'src/app/services/person.service';
import { appRoutes } from './app.routes';
import { HomeComponent } from './pages/home/home.component';

@Component({
  standalone: true,
  imports: [
    RouterModule,
    TuiRoot,
    TuiButton,
    FormsModule,
    KeyValuePipe,
    NgForOf,
    RouterLink,
    RouterLinkActive,
    TuiAppearance,
    TuiAvatar,
    TuiBadge,
    TuiBadgeNotification,
    TuiButton,
    TuiCardLarge,
    TuiChevron,
    TuiDataList,
    TuiDataListDropdownManager,
    TuiDropdown,
    TuiFade,
    TuiHeader,
    TuiIcon,
    TuiNavigation,
    TuiRepeatTimes,
    TuiSurface,
    TuiSwitch,
    TuiTabs,
    TuiTitle,
    TuiTextfield,
    ReactiveFormsModule,
    HomeComponent,
  ],
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.less',
})
export class AppComponent {
  protected readonly dialogService = inject(TuiDialogService);
  protected readonly injector = inject(INJECTOR);
  protected readonly destroyRef = inject(DestroyRef);
  protected readonly personService = inject(PersonService);
  protected readonly alertService = inject(TuiAlertService);
  protected readonly router = inject(Router);

  protected expanded = false;
  protected open = false;
  protected switch = false;
  protected readonly routes = appRoutes;
  protected readonly searchPersonByNameControl = new FormControl('');
  protected readonly foundPersonsByName = signal<Person[]>([]);

  @ViewChild('dragonsTable', { static: true })
  dragonsTableTemplate!: TemplateRef<any>;
  protected readonly locationXControl = new FormControl<number | null>(null);
  protected readonly locationYControl = new FormControl<number | null>(null);
  protected readonly locationNameControl = new FormControl<string | null>(null);
  protected readonly minHeightControl = new FormControl<number | null>(null);
  protected readonly hairColorControl = new FormControl<string | null>(null);
  protected readonly nationalityControl = new FormControl<string | null>(null);
  protected readonly eyeColorControl = new FormControl<string | null>(null);

  createNewPerson(): void {
    this.dialogService
      .open<{ mode: ActionWithModel }>(
        new PolymorpheusComponent(PersonFormComponent, this.injector),
        {
          data: {
            mode: ActionWithModel.Create,
          },
          dismissible: true,
          label: 'Create person',
        }
      )
      .pipe(tuiTakeUntilDestroyed(this.destroyRef))
      .subscribe({
        complete: () => this.personService.refreshModelsList$.next(null),
      });
  }

  openCountByLocationDialog(
    dialog: PolymorpheusContent<TuiDialogContext<void, unknown>>
  ): void {
    this.dialogService.open(dialog, { label: 'По локации' }).subscribe();
  }

  openByMinHeightDialog(
    dialog: PolymorpheusContent<TuiDialogContext<void, unknown>>
  ): void {
    this.dialogService.open(dialog, { label: 'Выше роста' }).subscribe();
  }

  openHairColorDialog(
    dialog: PolymorpheusContent<TuiDialogContext<void, unknown>>
  ): void {
    this.dialogService.open(dialog, { label: 'По цвету волос' }).subscribe();
  }

  openEyesNationalityDialog(
    dialog: PolymorpheusContent<TuiDialogContext<void, unknown>>
  ): void {
    this.dialogService.open(dialog, { label: 'По нац. и глазам' }).subscribe();
  }

  showAverageWeight(): void {
    this.personService.getAverageWeight$().subscribe({
      next: (avg) => this.alertService.open(`Средний вес: ${avg}`).subscribe(),
    });
  }

  showCountByLocation(observer: Observer<unknown>): void {
    const x = this.locationXControl.value;
    const y = this.locationYControl.value;
    const name = this.locationNameControl.value ?? '';
    this.personService
      .countByLocation$(x as number, y as number, name)
      .subscribe({
        next: (count) => {
          observer.complete();
          this.alertService.open(`Найдено по локации: ${count}`).subscribe();
        },
      });
  }

  showByMinHeight(observer: Observer<unknown>): void {
    const minHeight = this.minHeightControl.value;
    if (minHeight == null) {
      this.alertService.open('minHeight обязателен').subscribe();
      return;
    }
    this.personService.getByHeight$(minHeight).subscribe({
      next: (people) => {
        this.foundPersonsByName.set(people);
        observer.complete();
        // Open dialog to show found persons
        this.dialogService
          .open(this.dragonsTableTemplate, {
            label: `Люди выше ${minHeight} см (найдено: ${people.length})`,
            size: 'l',
            dismissible: true,
          })
          .subscribe();
      },
    });
  }

  showCountByHairColor(observer: Observer<unknown>): void {
    const hairColor = this.hairColorControl.value ?? '';
    this.personService.countByHairColor$(hairColor).subscribe({
      next: (count) => {
        observer.complete();
        this.alertService
          .open(`Количество по цвету волос: ${count}`)
          .subscribe();
      },
    });
  }

  showEyesNationalityPercentage(observer: Observer<unknown>): void {
    const nationality = this.nationalityControl.value ?? '';
    const eyeColor = this.eyeColorControl.value ?? '';
    this.personService
      .getEyesNationalityPercentage$(nationality, eyeColor)
      .subscribe({
        next: (percentage) => {
          observer.complete();
          this.alertService
            .open(`Доля по нац. и цвету глаз: ${percentage}%`)
            .subscribe();
        },
      });
  }
}
