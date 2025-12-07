import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ImportService, ImportResult } from '../../core/services/import.service';

@Component({
  selector: 'app-importar-dados',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './importar-dados.html',
  styleUrl: './importar-dados.css',
})
export class ImportarDadosComponent {
  private importService = inject(ImportService);
  private authService = inject(AuthService);
  private router = inject(Router);

  selectedFile = signal<File | null>(null);
  isImporting = signal(false);
  resultado = signal<ImportResult | null>(null);
  erro = signal('');

  ngOnInit() {
    // Redirecionar se não for ADMIN
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/']);
    }
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const extensao = file.name.split('.').pop()?.toLowerCase();
      
      if (!['json', 'csv', 'xml'].includes(extensao || '')) {
        this.erro.set('Formato não suportado. Use JSON, CSV ou XML.');
        this.selectedFile.set(null);
        return;
      }
      
      this.selectedFile.set(file);
      this.erro.set('');
      this.resultado.set(null);
    }
  }

  importar() {
    const file = this.selectedFile();
    if (!file) {
      this.erro.set('Selecione um arquivo');
      return;
    }

    this.isImporting.set(true);
    this.erro.set('');
    this.resultado.set(null);

    this.importService.importarArquivo(file).subscribe({
      next: (result) => {
        this.isImporting.set(false);
        this.resultado.set(result);
        this.selectedFile.set(null);
        
        // Limpar input de arquivo
        const fileInput = document.getElementById('fileInput') as HTMLInputElement;
        if (fileInput) fileInput.value = '';
      },
      error: (err) => {
        this.isImporting.set(false);
        this.erro.set(err.error || 'Erro ao importar dados');
        console.error('Erro de importação:', err);
      }
    });
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
