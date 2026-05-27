import { Component, OnInit } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  usuario: any = null;

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.usuario = this.authService.getUsuarioActual();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Usa getRol() que ya normaliza quitando ROLE_
  get esEstudiante(): boolean { return this.authService.getRol() === 'ESTUDIANTE'; }
  get esDocente(): boolean { return this.authService.getRol() === 'DOCENTE'; }
  get esAdmin(): boolean { return this.authService.getRol() === 'ADMINISTRATIVO'; }
}
