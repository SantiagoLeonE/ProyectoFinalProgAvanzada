import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService, private router: Router) {}

  // Agrega automáticamente el token JWT a todas las peticiones HTTP
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const token = this.authService.getToken();

    let request = req;

    if (token) {
      request = req.clone({
        setHeaders: { Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {

        if (error.status === 401 || error.status === 403) {

          //Limpiar sesión inválida
          this.authService.logout(); // o limpia localStorage

          localStorage.removeItem('token');
          sessionStorage.clear();

          //Redirigir a login
          this.router.navigate(['/login']);
        }

        return throwError(() => error);
      })
    );
  }
}
