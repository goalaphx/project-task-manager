import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface TaskDTO {
  id?: number | string;
  title: string;
  description?: string;
  status?: string;
  dueDate?: string;
}

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiBase = '/api';

  constructor(private http: HttpClient) {}

  createTask(projectId: number, payload: TaskDTO): Observable<TaskDTO> {
    return this.http.post<TaskDTO>(`${this.apiBase}/projects/${projectId}/tasks`, payload, { withCredentials: true });
  }

  getTasks(projectId: number, page = 0, size = 100, search = ''): Observable<any> {
    return this.http.get(`${this.apiBase}/projects/${projectId}/tasks?page=${page}&size=${size}&search=${encodeURIComponent(search)}`, { withCredentials: true });
  }

  updateStatus(taskId: number | string, status: string): Observable<TaskDTO> {
    const id = encodeURIComponent(String(taskId));
    return this.http.patch<TaskDTO>(`${this.apiBase}/tasks/${id}/status?status=${encodeURIComponent(status)}`, {}, { withCredentials: true });
  }

  updateTask(taskId: number | string, payload: TaskDTO): Observable<TaskDTO> {
    const id = encodeURIComponent(String(taskId));
    return this.http.put<TaskDTO>(`${this.apiBase}/tasks/${id}`, payload, { withCredentials: true });
  }

  deleteTask(taskId: number | string): Observable<void> {
    const id = encodeURIComponent(String(taskId));
    return this.http.delete<void>(`${this.apiBase}/tasks/${id}`, { withCredentials: true });
  }
}
