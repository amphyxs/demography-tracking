import { Route } from '@angular/router';
import { HomeComponent } from '@dg-pages/home/home.component';

export const appRoutes: Route[] = [
  {
    path: '**',
    component: HomeComponent,
  },
];
