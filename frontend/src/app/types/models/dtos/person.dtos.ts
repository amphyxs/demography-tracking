import { Country } from '../person';

export type CoordinatesCreateDto = {
  x: number;
  y: number;
};

export type LocationCreateDto = {
  x: number;
  y: number;
  name: string;
};

export type PersonCreateDto = {
  name: string;
  coordinates: CoordinatesCreateDto;
  height: number;
  birthday: string; // YYYY-MM-DD
  weight: number | null;
  nationality: Country;
  location: LocationCreateDto;
};

export type PersonGetDto = {
  id: number;
  name: string;
  coordinates: CoordinatesGetDto;
  creationDate: string;
  height: number;
  birthday: string;
  weight: number | null;
  nationality: Country;
  location: LocationGetDto;
} | null;

export type CoordinatesGetDto = {
  id: number;
  x: number;
  y: number;
};

export type LocationGetDto = {
  id: number;
  x: number;
  y: number;
  name: string;
};
