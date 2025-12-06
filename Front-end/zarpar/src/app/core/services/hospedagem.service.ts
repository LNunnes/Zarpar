import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hospedagem, HospedagemRequest } from '../models/hospedagem.model';

@Injectable({
  providedIn: 'root'
})
export class HospedagemService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  listarPorPonto(pontoId: number): Observable<Hospedagem[]> {
    return this.http.get<Hospedagem[]>(`${this.apiUrl}/pontos/${pontoId}/hospedagens`, { withCredentials: true });
  }

  buscarPorId(pontoId: number, id: number): Observable<Hospedagem> {
    return this.http.get<Hospedagem>(`${this.apiUrl}/pontos/${pontoId}/hospedagens/${id}`, { withCredentials: true });
  }

  criar(pontoId: number, request: HospedagemRequest): Observable<Hospedagem> {
    return this.http.post<Hospedagem>(`${this.apiUrl}/pontos/${pontoId}/hospedagens`, request, { withCredentials: true });
  }

  atualizar(pontoId: number, id: number, request: HospedagemRequest): Observable<Hospedagem> {
    return this.http.put<Hospedagem>(`${this.apiUrl}/pontos/${pontoId}/hospedagens/${id}`, request, { withCredentials: true });
  }

  excluir(pontoId: number, id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/pontos/${pontoId}/hospedagens/${id}`, { withCredentials: true, responseType: 'text' });
  }
}

