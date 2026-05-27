import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../../services/usuario.service';

@Component({
  selector: 'app-lista-usuarios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lista-usuarios.component.html'
})
export class ListaUsuariosComponent implements OnInit {

  usuarios: any[] = [];
  cargando = false;
  filtroRol = '';
  error = '';

  roles = ['ESTUDIANTE', 'DOCENTE', 'ADMINISTRATIVO'];

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.usuarioService.listar(this.filtroRol || undefined).subscribe({
      next: (usuarios) => {
        this.usuarios = usuarios;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  activar(id: number): void {
    if (confirm('¿Está seguro de activar este usuario?')) {
      this.usuarioService.activar(id).subscribe({
        next: () => {
          this.error = '';
          this.cargarUsuarios();
        },
        error: (err) => {
          this.error = err.error?.mensaje ?? 'Error al activar el usuario.';
        }
      });
    }
  }

  desactivar(id: number): void {
    if (confirm('¿Está seguro de desactivar este usuario?')) {
      this.usuarioService.desactivar(id).subscribe({
        next: () => {
          this.cargarUsuarios();
        },
        error: () => {
          this.error = 'Error al desactivar el usuario.';
        }
      });
    }
  }

  limpiarFiltro(): void {
    this.filtroRol = '';
    this.cargarUsuarios();
  }
}
