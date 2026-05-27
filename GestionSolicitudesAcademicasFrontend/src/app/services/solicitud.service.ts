import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  Solicitud, CrearSolicitudRequest, ClasificarSolicitudRequest,
  AsignarResponsableRequest, CerrarSolicitudRequest, PageResponse
} from '../models/solicitud.interface';

@Injectable({ providedIn: 'root' })
export class SolicitudService {

  private apiUrl = `${environment.apiUrl}/solicitudes`;

  constructor(private http: HttpClient) {}

  crear(request: CrearSolicitudRequest): Observable<Solicitud> {
    return this.http.post<Solicitud>(this.apiUrl, request);
  }

  listar(filtros?: any): Observable<PageResponse<Solicitud>> {
    let params = new HttpParams();
    if (filtros?.estado) params = params.set('estadoSolicitud', filtros.estado);
    if (filtros?.tipo) params = params.set('tipoSolicitud', filtros.tipo);
    if (filtros?.prioridad) params = params.set('prioridad', filtros.prioridad);
    if (filtros?.responsableId) params = params.set('responsableId', filtros.responsableId);
    params = params.set('page', filtros?.page ?? 0);
    params = params.set('size', filtros?.size ?? 10);
    return this.http.get<PageResponse<Solicitud>>(this.apiUrl, { params });
  }

  obtenerPorId(id: number): Observable<Solicitud> {
    return this.http.get<Solicitud>(`${this.apiUrl}/${id}`);
  }

  clasificar(id: number, request: ClasificarSolicitudRequest): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/clasificar`, request);
  }

  asignarResponsable(id: number, request: AsignarResponsableRequest): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/asignar`, request);
  }

  atender(id: number): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/atender`, {});
  }

  resolver(id: number): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/resolver`, {});
  }

  cerrar(id: number, request: CerrarSolicitudRequest): Observable<Solicitud> {
    return this.http.patch<Solicitud>(`${this.apiUrl}/${id}/cerrar`, request);
  }

  obtenerHistorial(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/historial`);
  }
}
