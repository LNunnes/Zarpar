export type TipoHospedagem = 'HOTEL' | 'POUSADA' | 'HOSTEL' | 'RESORT';

export interface Hospedagem {
  id: number;
  pontoId: number;
  nome: string;
  endereco: string;
  telefone?: string;
  precoMedio: number;
  tipo: TipoHospedagem;
  linkReserva?: string;
  criadoPorId: number;
  criadoPorNome: string;
}

export interface HospedagemRequest {
  nome: string;
  endereco: string;
  telefone?: string;
  precoMedio: number;
  tipo: TipoHospedagem;
  linkReserva?: string;
}

