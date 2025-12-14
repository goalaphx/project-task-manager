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

-   [Java JDK 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
-   [Node.js and npm](https://nodejs.org/en/)
-   [Docker and Docker Compose](https://www.docker.com/products/docker-desktop/)

### Backend & Database Setup (using Docker Compose)

1.  **Navigate to the project root directory:**
    ```bash
    cd project-task-manager
    ```
    (Ensure you are in the directory containing `docker-compose.yml` and the `taskbackend` folder.)
2.  **Update Database Credentials (if necessary):**
    If you need to change the MySQL username or password (default is `root`/`password`), edit the `application.properties` file in `taskbackend/src/main/resources/application.properties` and the `docker-compose.yml` file.
3.  **Build and run the backend and database:**
    ```bash
    docker-compose up --build
    ```
    This command will:
    -   Build the backend Spring Boot application's Docker image.
    -   Start the MySQL database container.
    -   Start the backend application container.
    The backend server will be accessible at `http://localhost:8080`.

### Frontend Setup

1.  **Navigate to the frontend directory:**
    ```bash
    cd taskfrontend
    ```
2.  **Install dependencies:**
    ```bash
    npm install
    ```
3.  **Run the application:**
    ```bash
    npm start
    ```
    The frontend development server will start. Open your browser and navigate to `http://localhost:4200`.

## 📹 Demo Video

*[Link to your 1-2 minute demo video will go here. You can upload it to YouTube, Google Drive, etc.]*
