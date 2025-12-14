import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ProjectService, ProjectDTO } from '../../services/project';
import { TaskService, TaskDTO } from '../../services/task';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-project-details',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './project-details.html',
  styleUrls: ['./project-details.css'],
  // PERFORMANCE FIX: Critical for lists to prevent lagging
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProjectDetailsComponent implements OnInit {
  project: ProjectDTO | null = null;
  tasks: TaskDTO[] = [];
  newTitle = '';
  newDescription = '';
  newDueDate: string = '';
  error: string | null = null;
  addingTask = false;
  
  // Sets are efficient, but with OnPush we must manually markForCheck when they change
  pendingTaskIds = new Set<number | string>();
  deletingTaskIds = new Set<number | string>();
  isEditingProject = false;
  updatedProject: ProjectDTO = { title: '' };
  isEditingTask: number | string | null = null;
  updatedTask: TaskDTO = { title: '' };

  constructor(
    private route: ActivatedRoute,
    private projectService: ProjectService,
    private taskService: TaskService,
    private cdr: ChangeDetectorRef // Inject this
  ) {}

  toggleEditProject() {
    if (this.project) {
      this.updatedProject = { ...this.project };
      this.isEditingProject = true;
    }
  }

  cancelEditProject() {
    this.isEditingProject = false;
  }

  saveProject() {
    if (this.project && this.project.id) {
      this.projectService.updateProject(this.project.id, this.updatedProject).subscribe({
        next: (updatedProject) => {
          this.project = updatedProject;
          this.isEditingProject = false;
          this.cdr.markForCheck();
        },
        error: (e) => {
          this.error = e?.message;
          this.cdr.markForCheck();
        },
      });
    }
  }

  toggleEditTask(task: TaskDTO) {
    if (task.id) {
      this.updatedTask = { ...task };
      this.isEditingTask = task.id;
    }
  }

  cancelEditTask() {
    this.isEditingTask = null;
  }

  saveTask(task: TaskDTO) {
    if (task.id) {
      this.taskService.updateTask(task.id, this.updatedTask).subscribe({
        next: (updatedTask) => {
          const index = this.tasks.findIndex((t) => t.id === task.id);
          if (index !== -1) {
            this.tasks[index] = updatedTask;
          }
          this.isEditingTask = null;
          this.cdr.markForCheck();
        },
        error: (e) => {
          this.error = e?.message;
          this.cdr.markForCheck();
        },
      });
    }
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id) {
      this.projectService.getProject(id).subscribe({
        next: p => { 
            this.project = p; 
            this.cdr.markForCheck(); // Update UI
        },
        error: e => { 
            this.error = e?.message; 
            this.cdr.markForCheck(); 
        }
      });
      this.loadTasks(id);
    }
  }

  loadTasks(projectId: number) {
    this.taskService.getTasks(projectId).subscribe({
      next: (page: any) => { 
          this.tasks = page.content ?? page; 
          this.cdr.markForCheck(); 
      },
      error: e => { 
          this.error = e?.message; 
          this.cdr.markForCheck(); 
      }
    });
  }

  trackByTask(index: number, t: TaskDTO) {
    return t.id;
  }

  addTask() {
    if (!this.project?.id || !this.newTitle.trim()) return;
    
    const tempId = `temp-${Date.now()}`;
    const tempTask: TaskDTO = { 
        id: tempId, 
        title: this.newTitle, 
        description: this.newDescription, 
        status: 'PENDING',
        dueDate: this.newDueDate
    };

    // Optimistic Update
    this.tasks = [tempTask, ...this.tasks];
    
    // Update local project stats
    this.updateProjectStats(1, 0);

    const projectId = this.project.id;
    this.newTitle = '';
    this.newDescription = '';
    this.newDueDate = '';
    this.addingTask = true;

    this.taskService.createTask(projectId, { title: tempTask.title, description: tempTask.description, dueDate: tempTask.dueDate }).subscribe({
      next: (t) => {
        const idx = this.tasks.findIndex(x => x.id === tempId);
        if (idx !== -1) {
            const newTasks = [...this.tasks];
            newTasks[idx] = t;
            this.tasks = newTasks;
        }
        this.addingTask = false;
        this.cdr.markForCheck();
      },
      error: (e) => {
        this.tasks = this.tasks.filter(x => x.id !== tempId);
        this.updateProjectStats(-1, 0); // Revert stats
        this.error = e?.message;
        this.addingTask = false;
        this.cdr.markForCheck();
      }
    });
  }

  async completeTask(task: TaskDTO) {
    if (!task.id || (typeof task.id === 'string' && task.id.startsWith('temp-'))) return;
    
    const id = task.id;
    const prevStatus = task.status;
    
    // Optimistic Update
    task.status = 'COMPLETED';
    this.pendingTaskIds.add(id);
    this.updateProjectStats(0, 1); // +0 total, +1 completed

    this.taskService.updateStatus(id as number, 'COMPLETED').subscribe({
      next: () => {
        this.pendingTaskIds.delete(id);
        this.cdr.markForCheck();
      },
      error: (e) => {
        task.status = prevStatus;
        this.updateProjectStats(0, -1); // Revert
        this.pendingTaskIds.delete(id);
        this.error = e?.message;
        this.cdr.markForCheck();
      }
    });
  }

  deleteTaskById(task: TaskDTO) {
    if (!task.id || !confirm('Delete this task?')) return;
    const id = task.id;

    if (typeof id === 'string' && id.startsWith('temp-')) {
      this.tasks = this.tasks.filter(t => t.id !== id);
      return;
    }

    const previousTasks = [...this.tasks];
    const wasCompleted = task.status === 'COMPLETED';

    this.tasks = this.tasks.filter(t => t.id !== id);
    this.deletingTaskIds.add(id);
    
    // Update stats immediately
    this.updateProjectStats(-1, wasCompleted ? -1 : 0);

    this.taskService.deleteTask(id as number).subscribe({
      next: () => {
        this.deletingTaskIds.delete(id);
        this.cdr.markForCheck();
      },
      error: (e) => {
        this.tasks = previousTasks; // Revert list
        this.deletingTaskIds.delete(id);
        this.updateProjectStats(1, wasCompleted ? 1 : 0); // Revert stats
        this.error = e?.message;
        this.cdr.markForCheck();
      }
    });
  }

  // Helper to keep project stats in sync without refreshing
  private updateProjectStats(deltaTotal: number, deltaCompleted: number) {
      if (!this.project) return;
      
      const newTotal = (this.project.taskCount || 0) + deltaTotal;
      const newCompleted = (this.project.completedTaskCount || 0) + deltaCompleted;
      
      this.project.taskCount = Math.max(0, newTotal);
      this.project.completedTaskCount = Math.max(0, newCompleted);
      
      this.project.progress = this.project.taskCount === 0 ? 0 
        : ((this.project.completedTaskCount / this.project.taskCount) * 100);
  }

  isTempId(id: number | string | undefined): boolean {
    return typeof id === 'string' && id.startsWith('temp-');
  }

  isPendingId(id: number | string | undefined): boolean {
    return id !== undefined && this.pendingTaskIds.has(id);
  }

  isDeletingId(id: number | string | undefined): boolean {
    return id !== undefined && this.deletingTaskIds.has(id);
  }
}