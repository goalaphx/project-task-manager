import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProjectService, ProjectDTO } from '../../services/project';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css'],
  changeDetection: ChangeDetectionStrategy.OnPush 
})
export class DashboardComponent implements OnInit {
  projects: ProjectDTO[] = [];
  newName = '';
  newDescription = '';
  error: string | null = null;
  addingProject = false;

  @ViewChild('titleInput') titleInput!: ElementRef;

  constructor(
      private projectService: ProjectService, 
      private router: Router,
      private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    this.projectService.getProjects().subscribe({
      next: (list) => {
        this.projects = list;
        this.cdr.markForCheck(); 
      },
      error: (err) => {
        this.error = err?.message ?? 'Failed to load projects';
        this.cdr.markForCheck();
      }
    });
  }

  trackByProject(index: number, project: ProjectDTO): number | string {
      return project.id || index;
  }

  open(project: ProjectDTO) {
    if (project.id) this.router.navigate(['/projects', project.id]);
  }

  focusInput() {
      this.titleInput?.nativeElement.focus();
  }

  // NEW: Logic to delete a project
  deleteProject(project: ProjectDTO, event: Event) {
    event.stopPropagation(); // Stop card click

    if (!project.id || !confirm(`Are you sure you want to delete "${project.title}"?`)) return;

    const prevProjects = [...this.projects];
    this.projects = this.projects.filter(p => p.id !== project.id);
    this.cdr.markForCheck(); 

    this.projectService.deleteProject(project.id).subscribe({
      next: () => {
        // Success
      },
      error: (err) => {
        this.projects = prevProjects;
        this.error = "Failed to delete project";
        this.cdr.markForCheck();
      }
    });
  }

  create() {
    if (!this.newName?.trim()) return;
    const tempId = `temp-${Date.now()}`;
    const temp: ProjectDTO = { 
        id: tempId as any, 
        title: this.newName, 
        description: this.newDescription, 
        taskCount: 0, 
        completedTaskCount: 0, 
        progress: 0 
    };

    this.projects = [temp, ...this.projects];
    
    const prevName = this.newName;
    const prevDescription = this.newDescription;
    this.newName = '';
    this.newDescription = '';
    this.addingProject = true;
    
    this.projectService.createProject({ title: prevName, description: prevDescription }).subscribe({
      next: (p) => {
        const idx = this.projects.findIndex(x => String(x.id) === tempId);
        if (idx !== -1) {
            const newProjects = [...this.projects];
            newProjects[idx] = p;
            this.projects = newProjects;
        }
        this.addingProject = false;
        this.cdr.markForCheck();
      },
      error: (e) => {
        this.projects = this.projects.filter(x => String(x.id) !== tempId);
        this.addingProject = false;
        this.error = e?.message ?? 'Create failed';
        this.cdr.markForCheck();
      }
    });
  }
}