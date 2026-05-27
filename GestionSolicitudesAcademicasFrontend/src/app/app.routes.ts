import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';
import { LoginComponent } from './components/login/login.component';
import { RegistroComponent } from './components/registro/registro.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { ListaSolicitudesComponent } from './components/solicitudes/lista-solicitudes/lista-solicitudes.component';
import { CrearSolicitudComponent } from './components/solicitudes/crear-solicitud/crear-solicitud.component';
import { ListaUsuariosComponent } from './components/usuarios/lista-usuarios/lista-usuarios.component';
import { GestionarSolicitudComponent } from './components/solicitudes/gestionar-solicitud/gestionar-solicitud.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegistroComponent },
  {
    path: '',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    children: [
      // Todos los roles autenticados pueden ver solicitudes
      { path: 'solicitudes', component: ListaSolicitudesComponent },

      // Solo ESTUDIANTE puede crear solicitudes
      {
        path: 'solicitudes/nueva',
        component: CrearSolicitudComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ESTUDIANTE'] }
      },

      // Solo ADMINISTRATIVO puede gestionar usuarios
      {
        path: 'usuarios',
        component: ListaUsuariosComponent,
        canActivate: [RoleGuard],
        data: { roles: ['ADMINISTRATIVO'] }
      },

      { path: 'dashboard', redirectTo: 'solicitudes' },

      {
        path: 'solicitudes/:id/gestionar',
        component: GestionarSolicitudComponent,
        canActivate: [RoleGuard],
        data: { roles: ['DOCENTE', 'ADMINISTRATIVO'] }
      }
    ]
  },
  { path: '**', redirectTo: 'login' }
];
