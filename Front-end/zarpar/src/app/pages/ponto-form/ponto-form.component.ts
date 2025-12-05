import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PontoService } from '../../core/services/ponto.service';
import { PontoRequest } from '../../core/models/ponto.model';

@Component({
  selector: 'app-ponto-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container py-5">
      <div class="card shadow">
        <div class="card-header bg-primary text-white">
          <h3 class="mb-0">{{ isEditMode() ? 'Editar' : 'Novo' }} Ponto Turístico</h3>
        </div>
        <div class="card-body">

          @if (errorMessage()) {
            <div class="alert alert-danger">{{ errorMessage() }}</div>
          }

          <form [formGroup]="form" (ngSubmit)="onSubmit()">

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Nome do Ponto *</label>
                <input type="text" class="form-control" formControlName="nome"
                       [class.is-invalid]="form.get('nome')?.invalid && form.get('nome')?.touched">
                <div class="invalid-feedback">Obrigatório.</div>
              </div>

              <div class="col-md-6 mb-3">
                <label class="form-label">Cidade *</label>
                <input type="text" class="form-control" formControlName="cidade"
                       [class.is-invalid]="form.get('cidade')?.invalid && form.get('cidade')?.touched">
              </div>
            </div>

            <div class="row">
               <div class="col-md-4 mb-3">
                <label class="form-label">Estado</label>
                <input type="text" class="form-control" formControlName="estado" placeholder="Ex: GO">
              </div>
              <div class="col-md-4 mb-3">
                <label class="form-label">País</label>
                <input type="text" class="form-control" formControlName="pais" placeholder="Ex: Brasil">
              </div>
            </div>

            <div class="mb-3">
              <label class="form-label">Descrição *</label>
              <textarea class="form-control" rows="3" formControlName="descricao"
                        [class.is-invalid]="form.get('descricao')?.invalid && form.get('descricao')?.touched"></textarea>
            </div>

            <div class="mb-3">
              <label class="form-label">Como Chegar (Orientação)</label>
              <textarea class="form-control" rows="2" formControlName="comoChegar"
                        placeholder="Ex: Pegue a trilha à direita após a ponte..."></textarea>
            </div>

            <div class="mb-3">
              <label class="form-label">Endereço Completo</label>
              <input type="text" class="form-control" formControlName="endereco">
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Latitude</label>
                <input type="number" step="any" class="form-control" formControlName="latitude">
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Longitude</label>
                <input type="number" step="any" class="form-control" formControlName="longitude">
              </div>
            </div>

            <hr>

            <div class="d-flex justify-content-end gap-2">
              <button type="button" class="btn btn-secondary" (click)="cancelar()">Cancelar</button>
              <button type="submit" class="btn btn-primary" [disabled]="form.invalid || isLoading()">
                @if (isLoading()) { Salvando... } @else { Salvar }
              </button>
            </div>

          </form>
        </div>
      </div>
    </div>
  `
})
export class PontoFormComponent {
  private fb = inject(FormBuilder);
  private pontoService = inject(PontoService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  isLoading = signal(false);
  errorMessage = signal('');
  isEditMode = signal(false);
  pontoId: number | null = null;

  form = this.fb.group({
    nome: ['', Validators.required],
    cidade: ['', Validators.required],
    estado: [''],
    pais: ['Brasil'],
    descricao: ['', Validators.required],
    comoChegar: [''],
    endereco: [''],
    latitude: [null as number | null],
    longitude: [null as number | null]
  });

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode.set(true);
      this.pontoId = Number(id);
      this.carregarDados(this.pontoId);
    }
  }

  carregarDados(id: number) {
    this.isLoading.set(true);
    this.pontoService.buscarPorId(id).subscribe({
      next: (ponto) => {
        this.form.patchValue(ponto);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Erro ao carregar dados do ponto.');
        this.isLoading.set(false);
      }
    });
  }

  onSubmit() {
    if (this.form.invalid) return;

    this.isLoading.set(true);
    const dados = this.form.value as PontoRequest;

    const request$ = this.isEditMode() && this.pontoId
      ? this.pontoService.atualizar(this.pontoId, dados)
      : this.pontoService.criar(dados);

    request$.subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error || 'Erro ao salvar ponto.');
      }
    });
  }

  cancelar() {
    this.router.navigate(['/']);
  }
}
