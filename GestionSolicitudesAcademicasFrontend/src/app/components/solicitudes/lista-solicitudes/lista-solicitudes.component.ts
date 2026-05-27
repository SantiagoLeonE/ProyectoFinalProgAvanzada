import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SolicitudService } from '../../../services/solicitud.service';
import { AuthService } from '../../../services/auth.service';
import { IaService } from '../../../services/ia.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-lista-solicitudes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DatePipe],
  templateUrl: './lista-solicitudes.component.html'
})
export class ListaSolicitudesComponent implements OnInit {

  solicitudes: any[] = [];
  cargando = false;
  filtroEstado = '';
  filtroPrioridad = '';
  totalPages = 0;
  paginaActual = 0;
  resumenIA: any = null;
  solicitudSeleccionada: any = null;
  cargandoResumen = false;
  historialModal: any[] = [];
  solicitudHistorial: any = null;
  cargandoHistorial = false;

  estados = ['REGISTRADA', 'CLASIFICADA', 'EN_ATENCION', 'ATENDIDA', 'CERRADA'];
  prioridades = ['ALTA', 'MEDIA', 'BAJA'];

  constructor(
    private solicitudService: SolicitudService,
    public authService: AuthService,
    private iaService: IaService
  ) {}

  ngOnInit(): void {
    this.cargarSolicitudes();
  }

  cargarSolicitudes(): void {
    this.cargando = true;

    const usuario = this.authService.getUsuarioActual();

    const filtros: any = {
      estado: this.filtroEstado || undefined,
      prioridad: this.filtroPrioridad || undefined,
      page: this.paginaActual,
      size: 10
    };

    // Si es DOCENTE solo ve sus solicitudes asignadas
    if (usuario?.rol === 'DOCENTE') {
      filtros.responsableId = usuario.id;
    }

    this.solicitudService.listar(filtros).subscribe({
      next: (page) => {
        this.solicitudes = page.content;
        this.totalPages = page.totalPages;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  generarResumen(solicitud: any): void {
    this.solicitudSeleccionada = solicitud;
    this.resumenIA = null;
    this.cargandoResumen = true;

    this.iaService.generarResumen(solicitud.id).subscribe({
      next: (res) => {
        this.resumenIA = res;
        this.cargandoResumen = false;
      },
      error: () => {
        this.resumenIA = {
          resumen: 'No se pudo generar el resumen en este momento.',
          iaDisponible: false
        };
        this.cargandoResumen = false;
      }
    });
  }

  cerrarModal(): void {
    this.resumenIA = null;
    this.solicitudSeleccionada = null;
    this.cargandoResumen = false;
  }

  cambiarPagina(pagina: number): void {
    this.paginaActual = pagina;
    this.cargarSolicitudes();
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroPrioridad = '';
    this.paginaActual = 0;
    this.cargarSolicitudes();
  }

  get esAdministrativo(): boolean {
    return this.authService.getRol() === 'ADMINISTRATIVO';
  }

  get esDocente(): boolean {
    return this.authService.getRol() === 'DOCENTE';
  }

  get puedeGestionar(): boolean {
    return this.esAdministrativo || this.esDocente;
  }

  get puedeGenerarResumen(): boolean {
    return this.esDocente;
  }

  verHistorial(solicitud: any): void {
    this.solicitudHistorial = solicitud;
    this.historialModal = [];
    this.cargandoHistorial = true;

    this.solicitudService.obtenerHistorial(solicitud.id).subscribe({
      next: (h) => {
        this.historialModal = h;
        this.cargandoHistorial = false;
      },
      error: () => {
        this.historialModal = [];
        this.cargandoHistorial = false;
      }
    });
  }

  cerrarHistorial(): void {
    this.solicitudHistorial = null;
    this.historialModal = [];
  }

  get puedeVerHistorial(): boolean {
    const rol = this.authService.getRol();
    return rol === 'ADMINISTRATIVO' || rol === 'DOCENTE' || rol === 'ESTUDIANTE';
  }

  getPrioridadClass(prioridad: string): string {
    const clases: any = {
      'ALTA': 'badge-alta',
      'MEDIA': 'badge-media',
      'BAJA': 'badge-baja'
    };
    return clases[prioridad] ?? '';
  }

  getEstadoClass(estado: string): string {
    const clases: any = {
      'REGISTRADA': 'estado-registrada',
      'CLASIFICADA': 'estado-clasificada',
      'EN_ATENCION': 'estado-atencion',
      'ATENDIDA': 'estado-atendida',
      'CERRADA': 'estado-cerrada'
    };
    return clases[estado] ?? '';
  }
}
