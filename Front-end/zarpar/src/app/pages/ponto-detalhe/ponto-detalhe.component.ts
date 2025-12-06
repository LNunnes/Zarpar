import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PontoService } from '../../core/services/ponto.service';
import { AvaliacaoService } from '../../core/services/avaliacao.service';
import { AuthService } from '../../core/services/auth.service';
import { PontoTuristico, Avaliacao } from '../../core/models/ponto.model';

@Component({
  selector: 'app-ponto-detalhe',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DatePipe],
  template: `
    @if (ponto(); as p) {
      <div class="bg-dark text-white py-5" style="background: linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5)), url('https://placehold.co/1200x400'); background-size: cover; background-position: center;">
        <div class="container">
          <span class="badge bg-warning text-dark mb-2">{{ p.categoria }}</span>
          <h1 class="display-3 fw-bold">{{ p.nome }}</h1>
          <p class="lead"><i class="bi bi-geo-alt"></i> {{ p.cidade }} - {{ p.estado }}</p>

          <div class="d-flex align-items-center gap-2 mt-3">
             <div class="d-flex text-warning fs-4">
               <span *ngFor="let s of [1,2,3,4,5]">
                 <i [class]="s <= (p.mediaAvaliacoes || 0) ? 'bi bi-star-fill' : 'bi bi-star'"></i>
               </span>
             </div>
             <span class="fs-5 fw-bold">{{ p.mediaAvaliacoes || 0 }}</span>
             <span class="text-light opacity-75">({{ avaliacoes().length }} avaliações)</span>
          </div>
        </div>
      </div>

      <div class="container py-5">
        <div class="row">
          <div class="col-lg-8">
            <div class="card shadow-sm mb-4">
              <div class="card-body">
                <h4 class="card-title text-primary mb-3">Sobre o local</h4>
                <p class="card-text fs-5">{{ p.descricao }}</p>

                <hr>

                <h5><i class="bi bi-compass"></i> Como Chegar</h5>
                <p class="text-muted">{{ p.comoChegar || 'Informação não disponível.' }}</p>

                <h5><i class="bi bi-map"></i> Endereço</h5>
                <p class="text-muted">{{ p.endereco || 'Endereço não informado.' }}</p>
              </div>
            </div>

            <h3 class="mb-4 mt-5">Avaliações e Comentários</h3>

            @if (authService.isLoggedIn()) {
              <div class="card mb-4 bg-light border-primary">
                <div class="card-body">
                  <h5 class="card-title">Conte sua experiência</h5>

                  <div class="mb-3">
                    <label class="form-label d-block">Sua Nota:</label>
                    <div class="d-flex gap-1 text-warning fs-3" style="cursor: pointer;">
                      @for (star of [1,2,3,4,5]; track star) {
                        <i (click)="minhaNota.set(star)"
                           [class]="star <= minhaNota() ? 'bi bi-star-fill' : 'bi bi-star'"></i>
                      }
                    </div>
                  </div>

                  <div class="mb-3">
                    <textarea class="form-control" rows="3"
                              placeholder="O que você achou deste lugar? (Obrigatório)"
                              [(ngModel)]="meuComentario" maxlength="500"></textarea>
                    <div class="text-end text-muted small">{{ meuComentario().length }}/500</div>
                  </div>

                  @if (errorMsg()) { <div class="alert alert-danger">{{ errorMsg() }}</div> }

                  <button class="btn btn-primary" (click)="enviarAvaliacao()"
                          [disabled]="!minhaNota() || !meuComentario() || isLoading()">
                    @if (isLoading()) { Enviando... } @else { Publicar Avaliação }
                  </button>
                </div>
              </div>
            } @else {
              <div class="alert alert-info">
                Para avaliar, você precisa <a routerLink="/login">fazer login</a>.
              </div>
            }

            @for (av of avaliacoes(); track av.id) {
              <div class="card mb-3 border-0 shadow-sm">
                <div class="card-body">
                  <div class="d-flex justify-content-between">
                    <div>
                      <h6 class="fw-bold mb-0">{{ av.nomeUsuario }}</h6>
                      <small class="text-muted">{{ av.data | date:'dd/MM/yyyy HH:mm' }}</small>
                    </div>

                    <div class="text-warning">
                       <span *ngFor="let s of [1,2,3,4,5]">
                         <i [class]="s <= av.nota ? 'bi bi-star-fill' : 'bi bi-star'"></i>
                       </span>
                    </div>
                  </div>

                  <p class="mt-2 mb-0">{{ av.comentario }}</p>

                  @if (canDelete(av)) {
                    <div class="text-end mt-2">
                      <button class="btn btn-sm btn-outline-danger border-0" (click)="excluirAvaliacao(av)">
                        <i class="bi bi-trash"></i> Remover
                      </button>
                    </div>
                  }
                </div>
              </div>
            }

            @if (avaliacoes().length === 0) {
              <p class="text-muted text-center py-4">Seja o primeiro a avaliar este local!</p>
            }

          </div>

          <div class="col-lg-4">
             </div>
        </div>
      </div>
    }
  `
})
export class PontoDetalheComponent implements OnInit {

  private route = inject(ActivatedRoute);
  private pontoService = inject(PontoService);
  private avaliacaoService = inject(AvaliacaoService);
  public authService = inject(AuthService);

  ponto = signal<PontoTuristico | null>(null);
  avaliacoes = signal<Avaliacao[]>([]);

  minhaNota = signal(0);
  meuComentario = signal('');
  isLoading = signal(false);
  errorMsg = signal('');

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.carregarDados(Number(id));
    }
  }

  carregarDados(id: number) {
    this.pontoService.buscarPorId(id).subscribe(p => this.ponto.set(p));

    this.carregarAvaliacoes(id);
  }

  carregarAvaliacoes(id: number) {
    this.avaliacaoService.listar(id).subscribe(lista => {
      this.avaliacoes.set(lista);

      const user = this.authService.currentUser();
      if (user) {
        const minha = lista.find(a => a.usuarioId === user.id);
        if (minha) {
          this.minhaNota.set(minha.nota);
          this.meuComentario.set(minha.comentario);
        }
      }
    });
  }

  enviarAvaliacao() {
    const p = this.ponto();
    if (!p) return;

    if (this.meuComentario().length > 500) {
      this.errorMsg.set('Comentário muito longo (max 500).');
      return;
    }

    this.isLoading.set(true);
    const req = { nota: this.minhaNota(), comentario: this.meuComentario() };

    this.avaliacaoService.salvar(p.id, req).subscribe({
      next: (av) => {
        this.isLoading.set(false);
        this.errorMsg.set('');

        this.carregarDados(p.id);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMsg.set(err.error || 'Erro ao avaliar');
      }
    });
  }

  canDelete(av: Avaliacao): boolean {
    const user = this.authService.currentUser();
    if (!user) return false;
    return user.role === 'ADMIN' || user.id === av.usuarioId;
  }

  excluirAvaliacao(av: Avaliacao) {
    if (confirm('Remover sua avaliação?')) {
      this.avaliacaoService.excluir(av.id).subscribe(() => {

        const user = this.authService.currentUser();
        if (user && user.id === av.usuarioId) {
            this.minhaNota.set(0);
            this.meuComentario.set('');
        }
        if (this.ponto()) this.carregarDados(this.ponto()!.id);
      });
    }
  }
}
