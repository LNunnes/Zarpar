import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Avaliacao, AvaliacaoRequest } from '../models/ponto.model';

@Injectable({ providedIn: 'root' })
export class AvaliacaoService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/avaliacoes';

  listar(pontoId: number): Observable<Avaliacao[]> {
    return this.http.get<Avaliacao[]>(`${this.apiUrl}/ponto/${pontoId}`);
  }

  salvar(pontoId: number, dados: AvaliacaoRequest): Observable<Avaliacao> {
    return this.http.post<Avaliacao>(`${this.apiUrl}/ponto/${pontoId}`, dados);
  }

  excluir(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
