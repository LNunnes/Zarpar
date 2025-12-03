import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="bg-light py-5 mb-5">
      <div class="container text-center">
        <h1 class="display-4 fw-bold text-primary">Descubra seu próximo destino</h1>
        <p class="lead text-muted">Compartilhe experiências, encontre hospedagens e explore o mundo com o Zarpar.</p>
        <div class="d-flex justify-content-center gap-3 mt-4">
          <button class="btn btn-primary btn-lg">Explorar Pontos</button>
          <a routerLink="/register" class="btn btn-outline-secondary btn-lg">Cadastre-se Grátis</a>
        </div>
      </div>
    </div>

    <div class="container">
      <h3 class="mb-4">Destaques Recentes</h3>

      <div class="row">
        <div class="col-md-4 mb-4">
          <div class="card h-100 shadow-sm border-0">
            <div class="card-header bg-transparent border-0 pt-3">
              <span class="badge bg-success">Natureza</span>
            </div>
            <div class="card-body">
              <h5 class="card-title">Cachoeira do Exemplo</h5>
              <p class="card-text text-muted">Uma bela cachoeira localizada no interior de Goiás...</p>
              <p class="small text-muted"><i class="bi bi-geo-alt"></i> Pirenópolis, GO</p>
            </div>
            <div class="card-footer bg-white border-0 pb-3">
              <button class="btn btn-outline-primary w-100">Ver Detalhes</button>
            </div>
          </div>
        </div>

        <div class="col-md-12 text-center mt-5 text-muted">
          <p><em>Os dados reais virão do banco de dados em breve...</em></p>
        </div>
      </div>
    </div>
  `
})
export class HomeComponent {}
