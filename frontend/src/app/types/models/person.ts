import { TuiDay } from '@taiga-ui/cdk';
import {
  CoordinatesCreateDto,
  CoordinatesGetDto,
  LocationCreateDto,
  LocationGetDto,
  PersonCreateDto,
  PersonGetDto,
} from './dtos/person.dtos';
import { Model } from './model';

export class Coordinates extends Model {
  constructor(public x: number, public y: number, override id: number | null) {
    super();
  }

  override asCreateDao(): CoordinatesCreateDto {
    return {
      x: this.x,
      y: this.y,
    };
  }

  static override fromGetDao(dao: CoordinatesGetDto): Coordinates {
    return new Coordinates(dao.x, dao.y, dao.id);
  }

  override toString(): string {
    return `(${this.x.toFixed(2)}; ${this.y})`;
  }
}

export class Person extends Model {
  constructor(
    public name: string,
    public coordinates: Coordinates,
    public height: number,
    public birthday: TuiDay,
    public weight: number | null,
    public nationality: Country,
    public location: Location,
    override id: number | null,
    override creationDate: Date = new Date()
  ) {
    super(id, creationDate);
  }

  static createBlank(dependencies: { coordinates: Coordinates[] }): Person {
    const defaultCoordinates = dependencies.coordinates[0];
    const defaultLocation = new Location(0, 0, '', null);
    return new Person(
      '',
      defaultCoordinates,
      0,
      TuiDay.currentLocal(),
      null,
      Country.Russia,
      defaultLocation,
      null,
      new Date()
    );
  }

  override asCreateDao(): PersonCreateDto {
    return {
      name: this.name,
      coordinates: this.coordinates.asCreateDao(),
      height: this.height,
      birthday: this.birthday.toUtcNativeDate().toISOString().slice(0, 10),
      weight: this.weight,
      nationality: this.nationality,
      location: this.location.asCreateDao() as LocationCreateDto,
    };
  }

  static override fromGetDao(dao: PersonGetDto | null): Person | null {
    return dao
      ? new Person(
          dao.name!,
          Coordinates.fromGetDao!(dao.coordinates!) as Coordinates,
          dao.height!,
          TuiDay.jsonParse(dao.birthday),
          (dao.weight as number | null) ?? null,
          dao.nationality!,
          Location.fromGetDao!(dao.location!) as Location,
          dao.id!,
          new Date(dao.creationDate!)
        )
      : null;
  }

  override toString(): string {
    return `${this.name} of ${this.location.name}`;
  }
}

export class Location extends Model {
  constructor(
    public x: number,
    public y: number,
    public name: string,
    override id: number | null
  ) {
    super();
  }

  override asCreateDao(): LocationCreateDto {
    return {
      x: this.x,
      y: this.y,
      name: this.name,
    };
  }

  static override fromGetDao(dao: LocationGetDto): Location | null {
    return dao ? new Location(dao.x, dao.y, dao.name, dao.id) : null;
  }

  override toString(): string {
    return `${this.name} (${this.x}; ${this.y})`;
  }
}

export enum Country {
  Russia = 'RUSSIA',
  China = 'CHINA',
  India = 'INDIA',
  Italy = 'ITALY',
  SouthKorea = 'SOUTH_KOREA',
}
