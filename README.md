# Project Task Manager

A full-stack web application for managing projects and their associated tasks. Users can register, log in, create projects, and manage tasks within those projects while tracking overall progress.

## ✨ Features

-   **Authentication:** Secure user login and registration using JWT tokens stored in HTTP-only cookies.
-   **Project Management:** Create, view, and delete projects.
-   **Task Management:** Within each project, users can create, complete, and delete tasks.
-   **Progress Tracking:** Each project displays a real-time progress bar and percentage based on task completion.
-   **Clean UI:** A responsive and modern user interface built with Bootstrap.

## 🛠️ Tools & Technologies

-   **Backend:**
    -   Java 17
    -   Spring Boot 3
    -   Spring Security (for JWT Authentication)
    -   MySQL (for data persistence)
    -   Maven (for dependency management)
-   **Frontend:**
    -   Angular 17
    -   TypeScript
    -   Bootstrap 5 & bootstrap-icons
    -   Node.js (for package management)

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed on your local machine:

-   [Docker and Docker Compose](https://www.docker.com/products/docker-desktop/)

### Running the Application with Docker Compose

1.  **Navigate to the project root directory:**
    ```bash
    cd project-task-manager
    ```
2.  **Build and run the entire application:**
    ```bash
    docker-compose up --build
    ```
    This command will:
    -   Build the Docker image for the frontend application.
    -   Build the Docker image for the backend application.
    -   Start the `frontend`, `backend`, and `db` services.

    The application will be accessible at `http://localhost`. The frontend is served on port 80, the backend on port 8080, and the MySQL database on port 3306.


## 📹 Demo Video

*[Link to your 1-2 minute demo video will go here. You can upload it to YouTube, Google Drive, etc.]*
