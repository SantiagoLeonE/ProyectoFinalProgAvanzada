import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { LoginRequest, RegistroRequest, LoginResponse } from '../models/usuario.interface';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = environment.apiUrl;
  private usuarioActual = new BehaviorSubject<LoginResponse | null>(
    this.obtenerUsuarioGuardado()
  );

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/login`, request).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('usuario', JSON.stringify(response));
        this.usuarioActual.next(response);
      })
    );
  }

  registro(request: RegistroRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/auth/registro`, request).pipe(
      tap(response => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('usuario', JSON.stringify(response));
        this.usuarioActual.next(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    this.usuarioActual.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUsuarioActual(): LoginResponse | null {
    return this.usuarioActual.value;
  }

  // Normaliza el rol quitando el prefijo ROLE_ si existe
  getRol(): string | null {
    const rol = this.usuarioActual.value?.rol ?? null;
    if (!rol) return null;
    return rol.replace('ROLE_', '');
  }

  estaAutenticado(): boolean {
    return !!this.getToken();
  }

  private obtenerUsuarioGuardado(): LoginResponse | null {
    const data = localStorage.getItem('usuario');
    return data ? JSON.parse(data) : null;
  }
}
