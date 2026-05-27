import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule, RouterLink],
  templateUrl: './login.component.html'
})
export class LoginComponent {

  email = '';
  contrasena = '';
  error = '';
  cargando = false;

  constructor(private authService: AuthService, private router: Router) {}

  login(): void {
    this.cargando = true;
    this.error = '';

    this.authService.login({ email: this.email, contrasena: this.contrasena }).subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigate(['/solicitudes']);
      },
      error: () => {
        this.cargando = false;
        this.error = 'Credenciales incorrectas. Verifique su email y contraseña.';
      }
    });
  }
}
