import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="container d-flex justify-content-center align-items-center vh-100">
      <div class="card shadow p-4" style="max-width: 400px; width: 100%">
        <h2 class="text-center mb-4">Crie sua conta</h2>

        @if (errorMessage()) {
          <div class="alert alert-danger">{{ errorMessage() }}</div>
        }

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div class="mb-3">
            <label class="form-label">Nome Completo</label>
            <input type="text" class="form-control" formControlName="nome"
              [class.is-invalid]="registerForm.get('nome')?.invalid && registerForm.get('nome')?.touched">
          </div>

          <div class="mb-3">
            <label class="form-label">E-mail</label>
            <input type="email" class="form-control" formControlName="email"
              [class.is-invalid]="registerForm.get('email')?.invalid && registerForm.get('email')?.touched">
             @if (registerForm.get('email')?.hasError('email')) {
               <div class="invalid-feedback">E-mail inválido.</div>
             }
          </div>

          <div class="mb-3">
            <label class="form-label">Senha</label>
            <input type="password" class="form-control" formControlName="senha"
              [class.is-invalid]="registerForm.get('senha')?.invalid && registerForm.get('senha')?.touched">
            <div class="form-text">Mínimo de 6 caracteres.</div>
          </div>

          <button type="submit" class="btn btn-success w-100" [disabled]="registerForm.invalid || isLoading()">
             @if (isLoading()) { Criando... } @else { Criar Conta }
          </button>
        </form>

        <div class="mt-3 text-center">
          <small>Já tem conta? <a routerLink="/login">Faça Login</a></small>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  isLoading = signal(false);
  errorMessage = signal<string>('');

  registerForm = this.fb.group({
    nome: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSubmit() {
    if (this.registerForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set(''); // Limpa erro anterior

    const { nome, email, senha } = this.registerForm.getRawValue();

    this.authService.register({ nome: nome!, email: email!, senha: senha! }).subscribe({
      next: () => {
        this.router.navigate(['/']); // Vai para home já logado
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(typeof err.error === 'string' ? err.error : 'Erro ao criar conta');
      }
    });
  }
}
