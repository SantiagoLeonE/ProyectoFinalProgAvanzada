export interface Solicitud {
  id: number;
  descripcion: string;
  fechaRegistro: string;
  justificacionPrioridad: string;
  observacionCierre: string;
  tipoSolicitud: string;
  estadoSolicitud: string;
  prioridad: string;
  canalOrigen: string;
  solicitanteId: number;
  responsableId: number;
  sugerenciaIA?: SugerenciaIA;
}

export interface SugerenciaIA {
  tipoSolicitudSugerido: string;
  prioridadSugerida: string;
  justificacion: string;
  iaDisponible: boolean;
}

export interface CrearSolicitudRequest {
  descripcion: string;
  tipoSolicitud: string;
  canalOrigen: string;
  solicitanteId: number;
}

export interface ClasificarSolicitudRequest {
  tipoSolicitud: string;
  impactoAcademico: string;
  fechaLimite: string;
  prioridadManual?: string | null;
}

export interface AsignarResponsableRequest {
  responsableId: number;
}

export interface CerrarSolicitudRequest {
  observacionCierre: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
