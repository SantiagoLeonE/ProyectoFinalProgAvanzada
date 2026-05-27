import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SolicitudService } from '../../../services/solicitud.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-crear-solicitud',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './crear-solicitud.component.html'
})
export class CrearSolicitudComponent {

  descripcion = '';
  tipoSolicitud = '';
  canalOrigen = '';
  error = '';
  cargando = false;
  solicitudCreada: any = null;

  tipos = [
    'REGISTRO_ASIGNATURA',
    'HOMOLOGACION',
    'CANCELACION',
    'SOLICITUD_CUPO',
    'CONSULTA_ACADEMICA'
  ];

  canales = ['PRESENCIAL', 'CSU', 'CORREO', 'SAC', 'TELEFONICO'];

  constructor(
    private solicitudService: SolicitudService,
    private authService: AuthService,
    private router: Router
  ) {}

  crear(): void {
    this.cargando = true;
    this.error = '';

    // Obtiene el usuario autenticado desde el servicio
    const usuario = this.authService.getUsuarioActual();

    // Validación básica antes de enviar
    if (!this.descripcion || !this.tipoSolicitud || !this.canalOrigen) {
      this.error = 'Por favor complete todos los campos.';
      this.cargando = false;
      return;
    }

    this.solicitudService.crear({
      descripcion: this.descripcion,
      tipoSolicitud: this.tipoSolicitud,
      canalOrigen: this.canalOrigen,
      solicitanteId: usuario?.id ?? 0
    }).subscribe({
      next: (solicitud) => {
        this.cargando = false;
        this.solicitudCreada = solicitud;
      },
      error: () => {
        this.cargando = false;
        this.error = 'Error al crear la solicitud. Verifique los datos.';
      }
    });
  }

  irAMisSolicitudes(): void {
    this.router.navigate(['/solicitudes']);
  }
}
