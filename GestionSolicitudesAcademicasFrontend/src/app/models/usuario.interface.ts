export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  identificacion: string;
  rol: string;
  activo: boolean;
}

export interface LoginRequest {
  email: string;
  contrasena: string;
}

export interface RegistroRequest {
  identificacion: string;
  nombre: string;
  email: string;
  contrasena: string;
  rol: string;
}

export interface LoginResponse {
  id: number;
  token: string;
  nombre: string;
  email: string;
  rol: string;
}
