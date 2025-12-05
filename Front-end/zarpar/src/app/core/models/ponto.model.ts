export interface PontoTuristico {
  id: number;
  nome: string;
  descricao: string;
  cidade: string;
  estado?: string;
  pais?: string;
  endereco?: string;
  comoChegar?: string;
  latitude?: number;
  longitude?: number;
  criadoPorId: number;
  criadoPorNome: string;
}

export interface PontoRequest {
  nome: string;
  descricao: string;
  cidade: string;
  estado?: string;
  pais?: string;
  endereco?: string;
  comoChegar?: string;
  latitude?: number;
  longitude?: number;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
