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

  categoria?: 'NATUREZA' | 'HISTORICO' | 'URBANO' | 'PRAIA' | 'AVENTURA' | 'RELIGIOSO' | 'OUTRO';

  criadoPorId: number;
  criadoPorNome: string;
  mediaAvaliacoes?: number;
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

  categoria?: string;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

export interface Avaliacao {
  id: string;
  pontoId: number;
  usuarioId: number;
  nomeUsuario: string;
  nota: number;
  comentario: string;
  data: string;
}

export interface AvaliacaoRequest {
  nota: number;
  comentario: string;
}
