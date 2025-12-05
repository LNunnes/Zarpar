import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Page, PontoRequest, PontoTuristico } from '../models/ponto.model';

@Injectable({
  providedIn: 'root'
})
export class PontoService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/pontos';

  listar(page: number = 0, size: number = 10, filtros?: { cidade?: string, categoria?: string }): Observable<Page<PontoTuristico>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (filtros?.cidade) params = params.set('cidade', filtros.cidade);
    if (filtros?.categoria) params = params.set('categoria', filtros.categoria);
    // if (filtros?.notaMinima) params = params.set('notaMinima', filtros.notaMinima.toString());

    return this.http.get<Page<PontoTuristico>>(this.apiUrl, { params });
  }

  buscarPorId(id: number): Observable<PontoTuristico> {
    return this.http.get<PontoTuristico>(`${this.apiUrl}/${id}`);
  }

  criar(dados: PontoRequest): Observable<PontoTuristico> {
    return this.http.post<PontoTuristico>(this.apiUrl, dados);
  }

  atualizar(id: number, dados: PontoRequest): Observable<PontoTuristico> {
    return this.http.put<PontoTuristico>(`${this.apiUrl}/${id}`, dados);
  }

  excluir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
