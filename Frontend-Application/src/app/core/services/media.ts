import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UploadResponse } from '../models/media.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class MediaService {
  private readonly API_URL = `${environment.apiUrl}/media`;
  private readonly MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

  constructor(private http: HttpClient) {}

  uploadImage(file: File): Observable<UploadResponse> {
    if (!this.isValidImage(file)) {
      throw new Error('Invalid file type. Only images are allowed.');
    }
    if (file.size > this.MAX_FILE_SIZE) {
      throw new Error('File size exceeds 2MB limit.');
    }

    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<UploadResponse>(`${this.API_URL}/images`, formData);
  }

  getImageUrl(id: string): string {
    return `${this.API_URL}/images/${id}`;
  }

  deleteImage(id: string): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/images/${id}`);
  }

  private isValidImage(file: File): boolean {
    return file.type.startsWith('image/');
  }
}

