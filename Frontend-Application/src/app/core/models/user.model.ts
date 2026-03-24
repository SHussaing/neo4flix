export interface User {
  id: string;
  email: string;
  name: string;
  role: 'CLIENT' | 'SELLER';
  avatar?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
  role: 'CLIENT' | 'SELLER';
}

export interface AuthResponse {
  token: string;
  user: User;
}

