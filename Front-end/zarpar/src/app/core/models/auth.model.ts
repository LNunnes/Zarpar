export interface AuthResponse {
  id: number;
  nome: string;
  email: string;
  role: 'USER' | 'ADMIN';
  message?: string;
}

export interface User {
  id: number;
  nome: string;
  email: string;
  role: 'USER' | 'ADMIN';
}
