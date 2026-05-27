import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const rolesPermitidos: string[] = route.data['roles'];
    const rolUsuario = this.authService.getRol();

    if (rolesPermitidos.includes(rolUsuario ?? '')) {
      return true;
    }

    // Si no tiene permiso lo manda a solicitudes
    this.router.navigate(['/solicitudes']);
    return false;
  }
}
