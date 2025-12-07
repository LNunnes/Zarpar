import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ExportService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/export';

  exportarTodos(formato: 'json' | 'csv' | 'xml') {
    const url = `${this.apiUrl}/all?formato=${formato}`;
    this.http.get(url, { 
      responseType: 'blob',
      withCredentials: true 
    }).subscribe({
      next: (blob) => {
        this.baixarArquivo(blob, formato, false);
      },
      error: (err) => {
        console.error('Erro ao exportar:', err);
        alert('Erro ao exportar dados. Verifique se você tem permissão.');
      }
    });
  }

  exportarPonto(pontoId: number, formato: 'json' | 'csv' | 'xml') {
    const url = `${this.apiUrl}/ponto/${pontoId}?formato=${formato}`;
    this.http.get(url, { 
      responseType: 'blob',
      withCredentials: true 
    }).subscribe({
      next: (blob) => {
        this.baixarArquivo(blob, formato, true);
      },
      error: (err) => {
        console.error('Erro ao exportar:', err);
        alert('Erro ao exportar ponto. Verifique se você tem permissão.');
      }
    });
  }

  private baixarArquivo(blob: Blob, formato: string, unico: boolean) {
    const data = new Date().toISOString().split('T')[0];
    const nomeBase = unico ? 'ponto' : 'pontos_turisticos';
    const nomeArquivo = `${nomeBase}_${data}.${formato}`;
    
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = nomeArquivo;
    link.click();
    window.URL.revokeObjectURL(link.href);
  }
}

