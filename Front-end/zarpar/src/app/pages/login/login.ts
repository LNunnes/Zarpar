import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink], // RouterLink para ir pro cadastro
  template: `
    <div class="container d-flex justify-content-center align-items-center vh-100">
      <div class="card shadow p-4" style="max-width: 400px; width: 100%">
        <h2 class="text-center mb-4">Bem-vindo ao Zarpar</h2>

        @if (errorMessage()) {
          <div class="alert alert-danger">{{ errorMessage() }}</div>
        }

        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          <div class="mb-3">
            <label for="email" class="form-label">E-mail</label>
            <input
              type="email"
              class="form-control"
              id="email"
              formControlName="email"
              [class.is-invalid]="loginForm.get('email')?.invalid && loginForm.get('email')?.touched"
            >
            @if (loginForm.get('email')?.hasError('required') && loginForm.get('email')?.touched) {
              <div class="invalid-feedback">E-mail é obrigatório.</div>
            }
          </div>

          <div class="mb-3">
            <label for="senha" class="form-label">Senha</label>
            <input
              type="password"
              class="form-control"
              id="senha"
              formControlName="senha"
              [class.is-invalid]="loginForm.get('senha')?.invalid && loginForm.get('senha')?.touched"
            >
            @if (loginForm.get('senha')?.hasError('minlength') && loginForm.get('senha')?.touched) {
              <div class="invalid-feedback">Mínimo 6 caracteres.</div>
            }
          </div>

          <button
            type="submit"
            class="btn btn-primary w-100"
            [disabled]="loginForm.invalid || isLoading()"
          >
            @if (isLoading()) {
              <span class="spinner-border spinner-border-sm me-2"></span>Entrando...
            } @else {
              Entrar
            }
          </button>
        </form>

        <div class="mt-3 text-center">
          <small>Não tem conta? <a routerLink="/register">Cadastre-se</a></small>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  isLoading = signal(false);
  errorMessage = signal<string>('');

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    senha: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSubmit() {
    if (this.loginForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set('');

    const { email, senha } = this.loginForm.getRawValue();

    this.authService.login({ email: email!, senha: senha! }).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error || 'Falha ao realizar login.');
      }
    });
  }
}
