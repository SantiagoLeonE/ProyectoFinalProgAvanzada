import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Usuario, RegistroRequest } from '../models/usuario.interface';

@Injectable({ providedIn: 'root' })
export class UsuarioService {

  private apiUrl = `${environment.apiUrl}/usuarios`;

  constructor(private http: HttpClient) {}

  listar(rol?: string, activo?: boolean): Observable<Usuario[]> {
    let params = new HttpParams();
    if (rol) params = params.set('rol', rol);
    if (activo !== undefined) params = params.set('activo', activo.toString());
    return this.http.get<Usuario[]>(this.apiUrl, { params });
  }

  obtenerPorId(id: number): Observable<Usuario> {
    return this.http.get<Usuario>(`${this.apiUrl}/${id}`);
  }

  crear(request: RegistroRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.apiUrl, request);
  }

  actualizar(id: number, request: any): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.apiUrl}/${id}`, request);
  }

  activar(id: number): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.apiUrl}/${id}/activar`, {});
  }

  desactivar(id: number): Observable<Usuario> {
    return this.http.patch<Usuario>(`${this.apiUrl}/${id}/desactivar`, {});
  }
}
