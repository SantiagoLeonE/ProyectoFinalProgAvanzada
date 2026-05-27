import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './registro.component.html'
})
export class RegistroComponent {

  identificacion = '';
  nombre = '';
  email = '';
  contrasena = '';
  rol = 'ESTUDIANTE';
  error = '';
  cargando = false;

  constructor(private authService: AuthService, private router: Router) {}

  registro(): void {
    this.cargando = true;
    this.error = '';

    this.authService.registro({
      identificacion: this.identificacion,
      nombre: this.nombre,
      email: this.email,
      contrasena: this.contrasena,
      rol: this.rol
    }).subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigate(['/solicitudes']);
      },
      error: () => {
        this.cargando = false;
        this.error = 'Error al registrarse. Verifique los datos ingresados.';
      }
    });
  }
}
