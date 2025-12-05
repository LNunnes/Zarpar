import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PontoService } from '../../core/services/ponto.service';
import { AuthService } from '../../core/services/auth.service';
import { PontoTuristico } from '../../core/models/ponto.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="bg-light py-5 mb-5">
      <div class="container text-center">
        <h1 class="display-4 fw-bold text-primary">Descubra seu próximo destino</h1>
        <p class="lead text-muted">Compartilhe experiências e explore o mundo com o Zarpar.</p>

        <div class="d-flex justify-content-center gap-3 mt-4">
          @if (authService.isLoggedIn()) {
            <a routerLink="/pontos/novo" class="btn btn-primary btn-lg">
              <i class="bi bi-plus-circle"></i> Novo Ponto
            </a>
          } @else {
            <a routerLink="/register" class="btn btn-outline-primary btn-lg">Cadastre-se para Participar</a>
          }
        </div>
      </div>
    </div>

    <div class="container pb-5">
      <h3 class="mb-4">Pontos Turísticos Recentes</h3>

      @if (isLoading()) {
        <div class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">A carregar...</span>
          </div>
        </div>
      }

      @if (!isLoading() && pontos().length === 0) {
        <div class="alert alert-info text-center">
          Nenhum ponto turístico cadastrado ainda. Seja o primeiro!
        </div>
      }

      <div class="row">
        @for (ponto of pontos(); track ponto.id) {
          <div class="col-md-4 mb-4">
            <div class="card h-100 shadow-sm border-0">

              <div class="bg-secondary bg-opacity-10 d-flex align-items-center justify-content-center" style="height: 200px;">
                <span class="text-muted"><i class="bi bi-image fs-1"></i></span>
              </div>

              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <h5 class="card-title mb-0">{{ ponto.nome }}</h5>
                  @if (ponto.estado) {
                    <span class="badge bg-light text-dark border">{{ ponto.estado }}</span>
                  }
                </div>

                <h6 class="card-subtitle text-muted mb-2 small">
                  <i class="bi bi-geo-alt-fill text-danger"></i> {{ ponto.cidade }}
                </h6>

                <p class="card-text text-truncate" style="max-height: 3em;">
                  {{ ponto.descricao }}
                </p>

                <p class="small text-muted mb-0">
                  Cadastrado por: <strong>{{ ponto.criadoPorNome }}</strong>
                </p>
              </div>

              <div class="card-footer bg-white border-top-0 pb-3 pt-0">
                <div class="d-flex justify-content-between align-items-center">

                  <button class="btn btn-sm btn-outline-primary">Ver Detalhes</button>

                  @if (canEdit(ponto)) {
                    <div class="btn-group">
                      <a [routerLink]="['/pontos/editar', ponto.id]" class="btn btn-sm btn-outline-secondary" title="Editar">
                        ✏️
                      </a>
                      <button class="btn btn-sm btn-outline-danger" (click)="excluir(ponto)" title="Excluir">
                        🗑️
                      </button>
                    </div>
                  }
                </div>
              </div>

            </div>
          </div>
        }
      </div>
    </div>
  `
})
export class HomeComponent implements OnInit {

  private pontoService = inject(PontoService);
  public authService = inject(AuthService);

  pontos = signal<PontoTuristico[]>([]);
  isLoading = signal(true);

  ngOnInit() {
    this.carregarPontos();
  }

  carregarPontos() {
    this.isLoading.set(true);
    this.pontoService.listar(0, 20).subscribe({
      next: (page) => {
        this.pontos.set(page.content);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar pontos', err);
        this.isLoading.set(false);
      }
    });
  }

  canEdit(ponto: PontoTuristico): boolean {
    const user = this.authService.currentUser();
    if (!user) return false;

    if (user.role === 'ADMIN') return true;

    return ponto.criadoPorId === user.id;
  }

  excluir(ponto: PontoTuristico) {
    if (confirm(`Tem a certeza que deseja excluir "${ponto.nome}"? Isso removerá fotos e comentários associados.`)) {
      this.pontoService.excluir(ponto.id).subscribe({
        next: () => {
          this.pontos.update(lista => lista.filter(p => p.id !== ponto.id));
        },
        error: (err) => alert('Erro ao excluir ponto.')
      });
    }
  }
}
