export interface Media {
  id: string;
  filename: string;
  contentType: string;
  size: number;
  uploadedBy: string;
  createdAt: string;
}

export interface UploadResponse {
  id: string;
  url: string;
  filename: string;
}

