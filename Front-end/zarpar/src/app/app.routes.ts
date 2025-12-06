import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { RegisterComponent } from './pages/register/register';
import { HomeComponent } from './pages/home/home';
import { PontoFormComponent } from './pages/ponto-form/ponto-form.component';
import { PontoDetailComponent } from './pages/ponto-detail/ponto-detail.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },

    {
      path: 'pontos/novo',
      component: PontoFormComponent,
      canActivate: [authGuard]
    },

    {
      path: 'pontos/editar/:id',
      component: PontoFormComponent,
      canActivate: [authGuard]
    },

    {
      path: 'pontos/:id',
      component: PontoDetailComponent
    },

    { path: '**', redirectTo: '' }
];
