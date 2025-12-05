import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router'; // Adicionei RouterLink de volta para os botões funcionarem
import { PontoService } from '../../core/services/ponto.service';
import { AuthService } from '../../core/services/auth.service';
import { PontoTuristico } from '../../core/models/ponto.model'; // Verifique se o caminho é .models ou .model
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink], // RouterLink é essencial para os botões
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

      <div class="card p-3 mb-4 shadow-sm bg-light">
        <div class="row g-3">
          <div class="col-md-4">
            <input type="text" class="form-control" placeholder="Filtrar por cidade..."
                   [(ngModel)]="filtroCidade" (keyup.enter)="aplicarFiltros()">
          </div>
          <div class="col-md-3">
            <select class="form-select" [(ngModel)]="filtroCategoria" (change)="aplicarFiltros()">
              <option value="">Todas as Categorias</option>
              <option value="NATUREZA">Natureza</option>
              <option value="HISTORICO">Histórico</option>
              <option value="URBANO">Urbano</option>
              <option value="PRAIA">Praia</option>
              <option value="AVENTURA">Aventura</option>
              <option value="RELIGIOSO">Religioso</option>
              <option value="OUTRO">Outro</option>
            </select>
          </div>
          <div class="col-md-2 d-grid">
            <button class="btn btn-primary" (click)="aplicarFiltros()">Filtrar</button>
          </div>
        </div>

        @if (filtroCidade() || filtroCategoria() || filtroNota()) {
          <div class="mt-2">
            <small class="text-muted">Filtros ativos: </small>
            <button class="btn btn-link btn-sm text-decoration-none" (click)="limparFiltros()">Limpar tudo</button>
          </div>
        }
      </div>

      <p class="text-muted mb-3">
        Mostrando {{ pontos().length }} de {{ totalElements() }} resultados
      </p>

      @if (isLoading()) {
        <div class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">A carregar...</span>
          </div>
        </div>
      }

      <div class="row">
        @for (ponto of pontos(); track ponto.id) {
          <div class="col-md-4 mb-4">
            <div class="card h-100 shadow-sm border-0">

              <div class="bg-secondary bg-opacity-10 d-flex align-items-center justify-content-center" style="height: 200px;">
                <span class="text-muted fs-1">📷</span>
              </div>

              <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                  <h5 class="card-title mb-0">{{ ponto.nome }}</h5>
                  @if (ponto.categoria) {
                    <span class="badge bg-info text-dark">{{ ponto.categoria }}</span>
                  }
                </div>

                <h6 class="card-subtitle text-muted mb-2 small">
                  📍 {{ ponto.cidade }}
                  @if (ponto.estado) { - {{ ponto.estado }} }
                </h6>

                <p class="card-text text-truncate" style="max-height: 3em;">
                  {{ ponto.descricao }}
                </p>

                <p class="small text-muted mb-0">
                  Criado por: <strong>{{ ponto.criadoPorNome }}</strong>
                </p>
              </div>

              <div class="card-footer bg-white border-top-0 pb-3 pt-0">
                <div class="d-flex justify-content-between align-items-center">
                  <button class="btn btn-sm btn-outline-primary">Ver Detalhes</button>

                  @if (canEdit(ponto)) {
                    <div class="btn-group">
                      <a [routerLink]="['/pontos/editar', ponto.id]" class="btn btn-sm btn-outline-secondary">✏️</a>
                      <button class="btn btn-sm btn-outline-danger" (click)="excluir(ponto)">🗑️</button>
                    </div>
                  }
                </div>
              </div>

            </div>
          </div>
        }
      </div>

      @if (!isLoading() && pontos().length === 0) {
        <div class="alert alert-warning text-center mt-3">
          Nenhum ponto turístico encontrado com estes filtros.
        </div>
      }

      @if (totalPages() > 1) {
        <nav class="mt-4">
          <ul class="pagination justify-content-center">
            <li class="page-item" [class.disabled]="currentPage() === 0">
              <button class="page-link" (click)="mudarPagina(currentPage() - 1)">Anterior</button>
            </li>

            <li class="page-item disabled">
              <span class="page-link">Página {{ currentPage() + 1 }} de {{ totalPages() }}</span>
            </li>

            <li class="page-item" [class.disabled]="currentPage() === totalPages() - 1">
              <button class="page-link" (click)="mudarPagina(currentPage() + 1)">Próxima</button>
            </li>
          </ul>
        </nav>
      }

    </div>
  `
})
export class HomeComponent implements OnInit {
  // ... (O código TypeScript da classe pode manter o mesmo que você já tem)
  private pontoService = inject(PontoService);
  public authService = inject(AuthService);

  pontos = signal<PontoTuristico[]>([]);
  totalElements = signal(0);
  totalPages = signal(0);
  currentPage = signal(0);
  isLoading = signal(true);

  filtroCidade = signal('');
  filtroCategoria = signal('');
  filtroNota = signal('');

  ngOnInit() {
    this.carregarPontos();
  }

  carregarPontos(page: number = 0) {
    this.isLoading.set(true);
    const filtros = {
      cidade: this.filtroCidade() || undefined,
      categoria: this.filtroCategoria() || undefined,
    };

    this.pontoService.listar(page, 10, filtros).subscribe({
      next: (resp) => {
        this.pontos.set(resp.content);
        this.totalElements.set(resp.totalElements);
        this.totalPages.set(resp.totalPages);
        this.currentPage.set(resp.number);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  aplicarFiltros() { this.carregarPontos(0); }

  limparFiltros() {
    this.filtroCidade.set('');
    this.filtroCategoria.set('');
    this.filtroNota.set('');
    this.carregarPontos(0);
  }

  mudarPagina(pagina: number) {
    if (pagina >= 0 && pagina < this.totalPages()) {
      this.carregarPontos(pagina);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  canEdit(ponto: PontoTuristico): boolean {
    const user = this.authService.currentUser();
    if (!user) return false;
    if (user.role === 'ADMIN') return true;
    return ponto.criadoPorId === user.id;
  }

  excluir(ponto: PontoTuristico) {
    if (confirm(`Tem a certeza que deseja excluir "${ponto.nome}"?`)) {
      this.pontoService.excluir(ponto.id).subscribe({
        next: () => {
          this.pontos.update(lista => lista.filter(p => p.id !== ponto.id));
        },
        error: (err) => alert('Erro ao excluir ponto.')
      });
    }
  }
}
