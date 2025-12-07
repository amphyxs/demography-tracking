import { AsyncPipe } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  effect,
  inject,
  signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActionWithModel } from '@dg-types/action-with-model.types';
import {
  Coordinates,
  Country,
  Location,
  Person,
} from '@dg-types/models/person';
import { TuiDay } from '@taiga-ui/cdk';
import {
  TuiButton,
  TuiCalendar,
  TuiDataList,
  tuiDateFormatProvider,
  TuiDialogContext,
  TuiError,
  TuiLabel,
  TuiLoader,
  TuiTextfield,
} from '@taiga-ui/core';
import {
  TUI_VALIDATION_ERRORS,
  TuiDataListWrapper,
  TuiFieldErrorPipe,
  TuiInputDate,
  tuiInputDateOptionsProviderNew,
} from '@taiga-ui/kit';
import {
  TuiInputDateModule,
  TuiInputNumberModule,
  TuiSelectModule,
} from '@taiga-ui/legacy';
import { injectContext } from '@taiga-ui/polymorpheus';
import { PersonService } from 'src/app/services/person.service';

export type PersonFormDialogContext = {
  mode: ActionWithModel;
  item?: Person;
};

@Component({
  selector: 'app-person-form',
  standalone: true,
  imports: [
    TuiTextfield,
    ReactiveFormsModule,
    TuiLabel,
    TuiInputDateModule,
    TuiSelectModule,
    TuiDataListWrapper,
    TuiDataList,
    TuiButton,
    TuiLoader,
    TuiError,
    TuiFieldErrorPipe,
    AsyncPipe,
    TuiInputNumberModule,
    TuiCalendar,
    TuiInputDate,
    TuiCalendar,
  ],
  providers: [
    {
      provide: TUI_VALIDATION_ERRORS,
      useValue: {
        minlength: ({ requiredLength }: { requiredLength: string }): string =>
          `At least ${requiredLength} characters`,
        required: 'Required',
        min: 'Should be bigger than 0',
      },
    },
    tuiInputDateOptionsProviderNew({
      valueTransformer: {
        fromControlValue: (value: TuiDay | null): TuiDay | null => value,
        toControlValue: (value: TuiDay | null): TuiDay | null => value,
      },
    }),
    tuiDateFormatProvider({ mode: 'YMD', separator: '.' }),
  ],
  templateUrl: './person-form.component.html',
  styleUrl: './person-form.component.less',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PersonFormComponent {
  protected readonly context =
    injectContext<TuiDialogContext<void, PersonFormDialogContext>>();
  protected readonly fb = inject(FormBuilder);
  protected readonly personService = inject(PersonService);

  personForm?: FormGroup;

  coordinates = toSignal(this.personService.getCoordinatesList$());
  isLoading = signal(true);

  get isEditable(): boolean {
    return [ActionWithModel.Update, ActionWithModel.Create].includes(
      this.context.data.mode
    );
  }

  constructor() {
    effect(
      () => {
        const dependencies = {
          coordinates: this.coordinates()!,
        };

        if (Object.values(dependencies).some((dependency) => !dependency)) {
          return;
        }

        const person: Person =
          this.context.data.item ?? Person.createBlank(dependencies);

        this.personForm = this.fb.group({
          id: [person.id],
          name: [person.name, [Validators.required, Validators.minLength(1)]],
          coordinates: this.fb.group({
            x: [person.coordinates.x, Validators.required],
            y: [person.coordinates.y, Validators.required],
          }),
          creationDate: [person.creationDate, Validators.required],
          height: [
            person.height,
            [Validators.required, Validators.min(0.0000001)],
          ],
          birthday: [person.birthday, Validators.required],
          weight: [person.weight, Validators.min(0.0000001)],
          nationality: [person.nationality, Validators.required],
          location: this.fb.group({
            x: [person.location.x, Validators.required],
            y: [person.location.y, Validators.required],
            name: [
              person.location.name,
              [Validators.required, Validators.minLength(1)],
            ],
          }),
        });

        if (this.context.data.mode === ActionWithModel.Read) {
          this.personForm.disable();
        } else {
          this.personForm.markAllAsTouched();
        }

        this.isLoading.set(false);
      },
      { allowSignalWrites: true }
    );
  }

  save(): void {
    const formValues = this.personForm!.value;
    const person = new Person(
      formValues.name,
      new Coordinates(formValues.coordinates.x, formValues.coordinates.y, null),
      parseFloat(formValues.height),
      formValues.birthday,
      formValues.weight != null ? parseFloat(formValues.weight) : null,
      formValues.nationality as Country,
      new Location(
        formValues.location.x,
        formValues.location.y,
        formValues.location.name,
        null
      ),
      null,
      new Date()
    );

    switch (this.context.data.mode) {
      case ActionWithModel.Create:
        this.personService.createModel$(person).subscribe({
          next: () => {
            // Only close dialog after successful creation
            this.context.completeWith();
          },
          error: (error) => {
            // Error handling is already done in the service
            // The dialog will remain open to allow user to fix the issue
            console.error('Error creating person:', error);
          },
        });
        break;
      case ActionWithModel.Update:
        person.id = formValues.id;
        this.personService.updateModel$(person).subscribe({
          next: () => {
            // Only close dialog after successful update
            this.context.completeWith();
          },
          error: (error) => {
            // Error handling is already done in the service
            // The dialog will remain open to allow user to fix the issue
            console.error('Error updating person:', error);
          },
        });
        break;
    }
  }

  cancel(): void {
    this.context.completeWith();
  }
}
