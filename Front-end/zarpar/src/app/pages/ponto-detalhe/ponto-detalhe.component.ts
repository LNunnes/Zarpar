import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PontoService } from '../../core/services/ponto.service';
import { AvaliacaoService } from '../../core/services/avaliacao.service';
import { AuthService } from '../../core/services/auth.service';
import { PontoTuristico, Avaliacao, Foto } from '../../core/models/ponto.model';
import { FotoService } from '../../core/services/foto.service';

@Component({
  selector: 'app-ponto-detalhe',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, DatePipe],
  template: `
    @if (ponto(); as p) {
      <div class="bg-dark text-white py-5"
        [style.background]="getHeroBackgroundUrl(p)"
        style="background-size: cover; background-position: center; transition: background 0.3s ease-in-out;"
      >
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

        <div class="mb-5">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h3>Galeria de Fotos ({{ fotos().length }}/10)</h3>

            @if (authService.isLoggedIn()) {
              <button class="btn btn-primary" (click)="toggleUpload()"
                      [disabled]="fotos().length >= 10">
                <i class="bi bi-camera"></i> Adicionar Foto
              </button>
            }
          </div>

          @if (showUploadForm()) {
             <div class="card mb-4 bg-light border-secondary">
               <div class="card-body">
                 <h5>Nova Foto</h5>
                 <div class="row g-3">
                   <div class="col-md-4">
                     <label class="form-label">Arquivo (Max 5MB)</label>
                     <input type="file" class="form-control" (change)="onFileSelected($event)" accept="image/*">
                   </div>
                   <div class="col-md-4">
                     <label class="form-label">Título (Opcional)</label>
                     <input type="text" class="form-control" [(ngModel)]="novoTitulo">
                   </div>
                   <div class="col-md-4">
                     <label class="form-label">Descrição (Opcional)</label>
                     <input type="text" class="form-control" [(ngModel)]="novaDescricao">
                   </div>
                 </div>

                 @if (uploadError()) { <div class="alert alert-danger mt-2">{{ uploadError() }}</div> }

                 <div class="mt-3 text-end">
                   <button class="btn btn-secondary me-2" (click)="toggleUpload()">Cancelar</button>
                   <button class="btn btn-success" (click)="enviarFoto()" [disabled]="!selectedFile || isUploading()">
                     @if (isUploading()) { Enviando... } @else { Enviar Foto }
                   </button>
                 </div>
               </div>
             </div>
          }

          @if (fotos().length === 0) {
            <div class="alert alert-secondary text-center">Nenhuma foto adicionada ainda.</div>
          }

          <div class="row g-3">
            @for (foto of fotos(); track foto.id) {
              <div class="col-md-3 col-6">
                <div class="card h-100 shadow-sm">
                  <img [src]="'http://localhost:8080/api/fotos/arquivo/' + foto.filename"
                       class="card-img-top" style="height: 150px; object-fit: cover;"
                       alt="{{ foto.titulo }}">

                  <div class="card-body p-2">
                    <h6 class="card-title text-truncate mb-1" title="{{ foto.titulo }}">{{ foto.titulo || 'Sem título' }}</h6>
                    <small class="d-block text-muted text-truncate">{{ foto.descricao }}</small>
                    <small class="d-block text-muted" style="font-size: 0.75rem">Por: {{ foto.nomeUsuario }}</small>

                    @if (canDeleteFoto(foto)) {
                       <button class="btn btn-link text-danger p-0 mt-1" style="font-size: 0.8rem" (click)="excluirFoto(foto)">
                         Excluir
                       </button>
                    }
                  </div>
                </div>
              </div>
            }
          </div>
        </div>

        <hr>

        <div class="row mt-4">
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
  private fotoService = inject(FotoService);
  public authService = inject(AuthService);

  ponto = signal<PontoTuristico | null>(null);
  avaliacoes = signal<Avaliacao[]>([]);
  fotos = signal<Foto[]>([]);

  minhaNota = signal(0);
  meuComentario = signal('');
  isLoading = signal(false);
  errorMsg = signal('');

  showUploadForm = signal(false);
  selectedFile: File | null = null;
  novoTitulo = '';
  novaDescricao = '';
  isUploading = signal(false);
  uploadError = signal('');

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.carregarDados(Number(id));
    }
  }

  carregarDados(id: number) {
    this.pontoService.buscarPorId(id).subscribe(p => this.ponto.set(p));
    this.carregarAvaliacoes(id);
    this.carregarFotos(id);
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

  carregarFotos(id: number) {
    this.fotoService.listar(id).subscribe(lista => this.fotos.set(lista));
  }

  toggleUpload() {
    this.showUploadForm.update(v => !v);
    this.uploadError.set('');
    this.selectedFile = null;
    this.novoTitulo = '';
    this.novaDescricao = '';
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  enviarFoto() {
    if (!this.selectedFile || !this.ponto()) return;

    // Validação Front-end rápida
    if (this.selectedFile.size > 5 * 1024 * 1024) {
      this.uploadError.set('Arquivo maior que 5MB.');
      return;
    }

    this.isUploading.set(true);
    this.fotoService.upload(this.ponto()!.id, this.selectedFile, this.novoTitulo, this.novaDescricao).subscribe({
      next: (foto) => {
        this.fotos.update(lista => [foto, ...lista]);
        this.isUploading.set(false);
        this.toggleUpload();
      },
      error: (err) => {
        this.isUploading.set(false);
        this.uploadError.set(err.error || 'Erro no upload.');
      }
    });
  }

  canDeleteFoto(foto: Foto): boolean {
    const user = this.authService.currentUser();
    if (!user) return false;
    return user.role === 'ADMIN' || user.id === foto.usuarioId;
  }

  excluirFoto(foto: Foto) {
    if (confirm('Tem a certeza que deseja remover esta foto?')) {
      this.fotoService.excluir(foto.id).subscribe(() => {
        this.fotos.update(lista => lista.filter(f => f.id !== foto.id));
      });
    }
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

  getHeroBackgroundUrl(p: PontoTuristico): string {
    const placeholder = 'https://placehold.co/1200x400';
    const overlay = 'linear-gradient(rgba(0,0,0,0.5), rgba(0,0,0,0.5))';

    if (p.capaFilename) {
      const urlReal = `http://localhost:8080/api/fotos/arquivo/${p.capaFilename}`;
      return `${overlay}, url('${urlReal}')`;
    }

    return `${overlay}, url('${placeholder}')`;
  }
}
