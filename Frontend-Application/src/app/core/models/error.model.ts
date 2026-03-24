export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
  path?: string;
}

