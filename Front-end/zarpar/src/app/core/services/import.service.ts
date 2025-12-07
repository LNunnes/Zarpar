import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ImportResult {
  totalProcessados: number;
  sucessos: number;
  erros: number;
  mensagensErro: string[];
}

@Injectable({
  providedIn: 'root'
})
export class ImportService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/import';

  importarArquivo(file: File): Observable<ImportResult> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<ImportResult>(this.apiUrl, formData, { 
      withCredentials: true 
    });
  }
}

