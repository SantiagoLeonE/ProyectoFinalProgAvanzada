import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SolicitudService } from '../../../services/solicitud.service';
import { UsuarioService } from '../../../services/usuario.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-gestionar-solicitud',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestionar-solicitud.component.html'
})
export class GestionarSolicitudComponent implements OnInit {

  solicitud: any = null;
  historial: any[] = [];
  responsables: any[] = [];
  cargando = false;
  error = '';
  exito = '';

  // Campos para clasificar
  tipoSolicitud = '';
  impactoAcademico = '';
  fechaLimite = '';
  prioridadManual: string = '';

  // Campo para asignar responsable
  responsableId: number | null = null;

  // Campo para cerrar
  observacionCierre = '';

  tipos = [
    'REGISTRO_ASIGNATURA', 'HOMOLOGACION',
    'CANCELACION', 'SOLICITUD_CUPO', 'CONSULTA_ACADEMICA'
  ];

  prioridades = ['ALTA', 'MEDIA', 'BAJA'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private solicitudService: SolicitudService,
    private usuarioService: UsuarioService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.cargarSolicitud(id);

    // Solo ADMINISTRATIVO puede asignar responsables
    if (this.esAdministrativo) {
      this.cargarResponsables();
    }
  }

  cargarSolicitud(id: number): void {
    this.cargando = true;
    this.solicitudService.obtenerPorId(id).subscribe({
      next: (s) => {
        this.solicitud = s;
        this.tipoSolicitud = s.tipoSolicitud ?? '';
        this.cargarHistorial(id);
        this.cargando = false;
      },
      error: (err) => {
        this.cargando = false;
        if (err.status === 400) {
          this.error = 'No tienes permiso para ver esta solicitud.';
        } else {
          this.error = 'Error al cargar la solicitud.';
        }
      }
    });
  }

  cargarHistorial(id: number): void {
    this.solicitudService.obtenerHistorial(id).subscribe({
      next: (h) => { this.historial = h; },
      error: () => {}
    });
  }

  cargarResponsables(): void {
    this.usuarioService.listar('DOCENTE', true).subscribe({
      next: (usuarios) => { this.responsables = usuarios; },
      error: () => {}
    });
  }

  clasificar(): void {
    this.limpiarMensajes();
    const fechaSolicitud = new Date(this.solicitud.fechaRegistro);

    const fechaLimite = new Date(this.fechaLimite);

    if (fechaLimite <= fechaSolicitud) {

      this.error = 'La fecha límite debe ser posterior a la fecha de la solicitud.';

      return;
    }
    this.solicitudService.clasificar(this.solicitud.id, {
      tipoSolicitud: this.tipoSolicitud,
      impactoAcademico: this.impactoAcademico,
      fechaLimite: this.fechaLimite,
      prioridadManual: this.prioridadManual || null
    }).subscribe({
      next: (s) => {
        this.solicitud = s;
        this.exito = 'Solicitud clasificada correctamente.';
      },
      error: () => { this.error = 'Error al clasificar la solicitud.'; }
    });
  }

  asignarResponsable(): void {
    this.limpiarMensajes();
    if (!this.responsableId) {
      this.error = 'Seleccione un responsable.';
      return;
    }
    this.solicitudService.asignarResponsable(this.solicitud.id, {
      responsableId: this.responsableId
    }).subscribe({
      next: (s) => {
        this.solicitud = s;
        this.exito = 'Responsable asignado correctamente.';
      },
      error: () => { this.error = 'Error al asignar el responsable.'; }
    });
  }

  atender(): void {
    this.limpiarMensajes();
    this.solicitudService.atender(this.solicitud.id).subscribe({
      next: (s) => {
        this.solicitud = s;
        this.exito = 'Solicitud en atención.';
      },
      error: () => { this.error = 'Error al atender la solicitud.'; }
    });
  }

  resolver(): void {
    this.limpiarMensajes();
    this.solicitudService.resolver(this.solicitud.id).subscribe({
      next: (s) => {
        this.solicitud = s;
        this.exito = 'Solicitud resuelta correctamente.';
      },
      error: () => { this.error = 'Error al resolver la solicitud.'; }
    });
  }

  cerrar(): void {
    this.limpiarMensajes();
    if (!this.observacionCierre.trim()) {
      this.error = 'La observación de cierre es obligatoria.';
      return;
    }
    this.solicitudService.cerrar(this.solicitud.id, {
      observacionCierre: this.observacionCierre
    }).subscribe({
      next: (s) => {
        this.solicitud = s;
        this.exito = 'Solicitud cerrada correctamente.';
      },
      error: () => { this.error = 'Error al cerrar la solicitud.'; }
    });
  }

  usarSugerenciaIA(): void {
    const sugerencia = this.solicitud?.sugerenciaIA;
    if (!sugerencia) return;
    this.tipoSolicitud = sugerencia.tipoSolicitudSugerido;
    this.prioridadManual = sugerencia.prioridadSugerida;
  }

  volver(): void {
    this.router.navigate(['/solicitudes']);
  }

  limpiarMensajes(): void {
    this.error = '';
    this.exito = '';
  }

  get puedeClasificar(): boolean {
    return this.solicitud?.estadoSolicitud === 'REGISTRADA';
  }

  get puedeAsignar(): boolean {

    const yaTieneResponsable =
      this.solicitud?.responsableId != null;

    return (
      (
        this.solicitud?.estadoSolicitud === 'REGISTRADA'
        ||
        this.solicitud?.estadoSolicitud === 'CLASIFICADA'
      )
      &&
      !yaTieneResponsable
    );
  }

  get puedeAtender(): boolean {
    return this.solicitud?.estadoSolicitud === 'CLASIFICADA';
  }

  get puedeResolver(): boolean {
    return this.solicitud?.estadoSolicitud === 'EN_ATENCION';
  }

  get puedeCerrar(): boolean {
    return this.solicitud?.estadoSolicitud === 'ATENDIDA';
  }

  get estaCerrada(): boolean {
    return this.solicitud?.estadoSolicitud === 'CERRADA';
  }

  get esAdministrativo(): boolean {
    return this.authService.getRol() === 'ADMINISTRATIVO';
  }

  get esDocente(): boolean {
    return this.authService.getRol() === 'DOCENTE';
  }

  get esResponsableAsignado(): boolean {
    const usuario = this.authService.getUsuarioActual();

    return !!usuario &&
      this.solicitud?.responsableId === usuario.id;
  }

  getEstadoClass(estado: string): string {
    const clases: any = {
      'REGISTRADA': 'estado-registrada', 'CLASIFICADA': 'estado-clasificada',
      'EN_ATENCION': 'estado-atencion', 'ATENDIDA': 'estado-atendida',
      'CERRADA': 'estado-cerrada'
    };
    return clases[estado] ?? '';
  }

  getPrioridadClass(prioridad: string): string {
    const clases: any = {
      'ALTA': 'badge-alta', 'MEDIA': 'badge-media', 'BAJA': 'badge-baja'
    };
    return clases[prioridad] ?? '';
  }
}
