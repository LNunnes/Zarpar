import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Foto } from '../models/ponto.model';

@Injectable({ providedIn: 'root' })
export class FotoService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/fotos';

  listar(pontoId: number): Observable<Foto[]> {
    return this.http.get<Foto[]>(`${this.apiUrl}/ponto/${pontoId}`);
  }

  upload(pontoId: number, file: File, titulo: string, descricao: string): Observable<Foto> {
    const formData = new FormData();
    formData.append('file', file);
    if (titulo) formData.append('titulo', titulo);
    if (descricao) formData.append('descricao', descricao);

    return this.http.post<Foto>(`${this.apiUrl}/ponto/${pontoId}`, formData);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
