import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common'; // Importante para diretivas básicas
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ExportService } from '../../core/services/export.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container">
        <a class="navbar-brand fw-bold" routerLink="/">ZARPAR ⛵</a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <a class="nav-link" routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Home</a>
            </li>
            @if (authService.isLoggedIn()) {
              <li class="nav-item">
                <a class="nav-link" routerLink="/pontos/novo">Novo Ponto</a>
              </li>
            }
            @if (authService.isAdmin()) {
              <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                  <i class="bi bi-download"></i> Exportar Dados
                </a>
                <ul class="dropdown-menu">
                  <li><a class="dropdown-item" (click)="exportarTodos('json')"><i class="bi bi-filetype-json"></i> JSON</a></li>
                  <li><a class="dropdown-item" (click)="exportarTodos('csv')"><i class="bi bi-filetype-csv"></i> CSV</a></li>
                  <li><a class="dropdown-item" (click)="exportarTodos('xml')"><i class="bi bi-filetype-xml"></i> XML</a></li>
                </ul>
              </li>
            }
          </ul>

          <ul class="navbar-nav ms-auto align-items-center">

            @if (!authService.isLoggedIn()) {
              <li class="nav-item">
                <a class="nav-link" routerLink="/login">Entrar</a>
              </li>
              <li class="nav-item ms-2">
                <a class="btn btn-light text-primary btn-sm rounded-pill px-3 fw-bold" routerLink="/register">
                  Criar Conta
                </a>
              </li>
            }

            @if (authService.isLoggedIn()) {
              <li class="nav-item me-3 text-light">
                <small>Olá, <strong>{{ authService.currentUser()?.nome }}</strong></small>
                @if (authService.isAdmin()) {
                  <span class="badge bg-warning text-dark ms-1">ADMIN</span>
                }
              </li>
              <li class="nav-item">
                <button class="btn btn-outline-light btn-sm" (click)="logout()">
                  Sair
                </button>
              </li>
            }

          </ul>
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  public authService = inject(AuthService);
  private exportService = inject(ExportService);

  logout() {
    this.authService.logout().subscribe();
  }

  exportarTodos(formato: 'json' | 'csv' | 'xml') {
    this.exportService.exportarTodos(formato);
  }
}
