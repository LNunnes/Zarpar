import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { PontoService } from '../../core/services/ponto.service';
import { AuthService } from '../../core/services/auth.service';
import { PontoTuristico } from '../../core/models/ponto.model';

@Component({
  selector: 'app-ponto-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container py-4" *ngIf="ponto()">
      <!-- Cabeçalho com título e ações -->
      <div class="d-flex justify-content-between align-items-start mb-4">
        <div>
          <h1 class="display-5 fw-bold">{{ ponto()!.nome }}</h1>
          <p class="text-muted mb-0">
            <i class="bi bi-geo-alt-fill"></i>
            {{ ponto()!.cidade }}, {{ ponto()!.estado }} - {{ ponto()!.pais }}
          </p>
        </div>

        <div class="btn-group" *ngIf="canEdit()">
          <a [routerLink]="['/pontos/editar', ponto()!.id]" class="btn btn-outline-primary">
            <i class="bi bi-pencil"></i> Editar Ponto
          </a>
          <button class="btn btn-outline-danger" (click)="excluir()">
            <i class="bi bi-trash"></i>
          </button>
        </div>
      </div>

      <!-- Informações Gerais -->
      <section class="card mb-4 shadow-sm">
        <div class="card-body">
          <h2 class="h4 mb-3"><i class="bi bi-info-circle"></i> Sobre este local</h2>
          <p class="lead">{{ ponto()!.descricao }}</p>

          <div class="row mt-4">
            <div class="col-md-6">
              <p class="mb-2"><strong>Endereço:</strong></p>
              <p class="text-muted">{{ ponto()!.endereco || 'Não informado' }}</p>
            </div>
            <div class="col-md-6">
              <p class="mb-2"><strong>Categoria:</strong></p>
              <span class="badge bg-info text-dark fs-6">{{ ponto()!.categoria || 'Não categorizado' }}</span>
            </div>
          </div>
        </div>
      </section>

      <section class="card mb-4 shadow-sm">
        <div class="card-body">
          <h2 class="h4 mb-3"><i class="bi bi-map"></i> Como Chegar</h2>

          <div class="row">
            <div class="col-md-6 mb-3">
              <p class="mb-2"><strong>Coordenadas GPS:</strong></p>
              <div class="p-3 bg-light rounded" *ngIf="ponto()!.latitude && ponto()!.longitude">
                <p class="mb-1 font-monospace">
                  <i class="bi bi-geo"></i>
                  {{ ponto()!.latitude }}, {{ ponto()!.longitude }}
                </p>
                <button class="btn btn-sm btn-outline-primary mt-2" (click)="copiarCoordenadas()">
                  <i class="bi bi-clipboard"></i> Copiar Coordenadas
                </button>
              </div>
              <p class="text-muted" *ngIf="!ponto()!.latitude || !ponto()!.longitude">
                Coordenadas não disponíveis
              </p>
            </div>

            <div class="col-md-6 mb-3">
              <p class="mb-2"><strong>Orientações:</strong></p>
              <div class="p-3 bg-light rounded" *ngIf="ponto()!.comoChegar">
                <p class="mb-0">{{ ponto()!.comoChegar }}</p>
              </div>
              <p class="text-muted" *ngIf="!ponto()!.comoChegar">
                Orientações não disponíveis
              </p>
            </div>
          </div>
        </div>
      </section>

      <section class="card mb-4 shadow-sm">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="h4 mb-0"><i class="bi bi-images"></i> Galeria de Fotos</h2>
            <button class="btn btn-primary" *ngIf="authService.isLoggedIn()" disabled>
              <i class="bi bi-camera"></i> Adicionar Foto
            </button>
          </div>

          <div class="alert alert-info">
            <i class="bi bi-info-circle"></i>
            Nenhuma foto ainda. Seja o primeiro a adicionar!
            <small class="d-block mt-2 text-muted">(Funcionalidade em desenvolvimento)</small>
          </div>
        </div>
      </section>

      <!-- Avaliações e Comentários (Placeholder) -->
      <section class="card mb-4 shadow-sm">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="h4 mb-0">
              <i class="bi bi-star-fill text-warning"></i> Avaliações
              <span class="text-muted fs-6">(0 avaliações)</span>
            </h2>
          </div>

          <div class="alert alert-info">
            <i class="bi bi-info-circle"></i>
            Nenhuma avaliação ainda. Seja o primeiro a avaliar!
            <small class="d-block mt-2 text-muted">(Funcionalidade em desenvolvimento)</small>
          </div>

          <div class="mt-4" *ngIf="authService.isLoggedIn()">
            <h3 class="h5">Deixe sua avaliação</h3>
            <button class="btn btn-outline-primary" disabled>
              <i class="bi bi-star"></i> Avaliar este local
            </button>
          </div>
        </div>
      </section>

      <!-- Hospedagens Próximas (Placeholder) -->
      <section class="card mb-4 shadow-sm">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h2 class="h4 mb-0"><i class="bi bi-building"></i> Hospedagens Próximas</h2>
            <button class="btn btn-primary" *ngIf="authService.isLoggedIn()" disabled>
              <i class="bi bi-plus-circle"></i> Adicionar Hospedagem
            </button>
          </div>

          <div class="alert alert-info">
            <i class="bi bi-info-circle"></i>
            Nenhuma hospedagem cadastrada ainda.
            <small class="d-block mt-2 text-muted">(Funcionalidade em desenvolvimento)</small>
          </div>
        </div>
      </section>

      <!-- Informações de Criação -->
      <div class="text-muted small mt-4">
        <p class="mb-0">
          <i class="bi bi-person"></i> Criado por: <strong>{{ ponto()!.criadoPorNome }}</strong>
        </p>
      </div>

    </div>

    <!-- Loading -->
    <div class="container py-5 text-center" *ngIf="isLoading()">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Carregando...</span>
      </div>
      <p class="mt-3 text-muted">Carregando detalhes...</p>
    </div>

    <!-- Erro -->
    <div class="container py-5" *ngIf="erro()">
      <div class="alert alert-danger">
        <h4 class="alert-heading">Erro ao carregar</h4>
        <p>{{ erro() }}</p>
        <hr>
        <button class="btn btn-primary" routerLink="/">Voltar para Início</button>
      </div>
    </div>
  `,
  styles: [`
    .card {
      transition: box-shadow 0.3s ease;
    }
    .card:hover {
      box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
    }
    .font-monospace {
      font-family: 'Courier New', monospace;
    }
  `]
})
export class PontoDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private pontoService = inject(PontoService);
  public authService = inject(AuthService);

  ponto = signal<PontoTuristico | null>(null);
  isLoading = signal(true);
  erro = signal('');

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.carregarPonto(+id);
    } else {
      this.erro.set('ID do ponto não fornecido');
      this.isLoading.set(false);
    }
  }

  carregarPonto(id: number) {
    this.isLoading.set(true);
    this.pontoService.buscarPorId(id).subscribe({
      next: (ponto) => {
        this.ponto.set(ponto);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Erro ao carregar ponto:', err);
        this.erro.set('Ponto turístico não encontrado ou erro ao carregar.');
        this.isLoading.set(false);
      }
    });
  }

  canEdit(): boolean {
    const user = this.authService.currentUser();
    const pontoAtual = this.ponto();
    if (!user || !pontoAtual) return false;
    if (user.role === 'ADMIN') return true;
    return pontoAtual.criadoPorId === user.id;
  }

  copiarCoordenadas() {
    const pontoAtual = this.ponto();
    if (pontoAtual && pontoAtual.latitude && pontoAtual.longitude) {
      const coords = `${pontoAtual.latitude}, ${pontoAtual.longitude}`;
      navigator.clipboard.writeText(coords).then(() => {
        alert('Coordenadas copiadas!');
      });
    }
  }

  excluir() {
    const pontoAtual = this.ponto();
    if (pontoAtual && confirm(`Tem certeza que deseja excluir "${pontoAtual.nome}"?\n\nIsso removerá também fotos, comentários e avaliações.`)) {
      this.pontoService.excluir(pontoAtual.id).subscribe({
        next: () => {
          alert('Ponto turístico excluído com sucesso!');
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Erro ao excluir:', err);
          alert('Erro ao excluir ponto turístico.');
        }
      });
    }
  }
}

