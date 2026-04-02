export interface User {
  id: string;
  email: string;
  name: string;
  avatar?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface OtpChallengeResponse {
  challengeId: string;
  email: string;
  devOtp?: string;
}

export interface VerifyLoginRequest {
  email: string;
  challengeId: string;
  otp: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}
