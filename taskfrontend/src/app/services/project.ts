import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface ProjectDTO {
  id?: number;
  title: string;
  description?: string;
  taskCount?: number;
  completedTaskCount?: number;
  progress?: number;
}

@Injectable({ providedIn: 'root' })
export class ProjectService {
  private apiUrl = '/api/projects'; // Check your port

  constructor(private http: HttpClient) {}

  getProjects() {
    return this.http.get<ProjectDTO[]>(this.apiUrl);
  }

  getProject(id: number) {
    return this.http.get<ProjectDTO>(`${this.apiUrl}/${id}`);
  }

  createProject(p: ProjectDTO) {
    return this.http.post<ProjectDTO>(this.apiUrl, p);
  }

  updateProject(id: number, p: ProjectDTO) {
    return this.http.put<ProjectDTO>(`${this.apiUrl}/${id}`, p);
  }

  // ADD THIS IF MISSING
  deleteProject(id: number) {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}