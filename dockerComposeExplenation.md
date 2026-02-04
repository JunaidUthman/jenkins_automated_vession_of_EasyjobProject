# Docker Compose Configuration
This docker-compose.yml file defines the architecture for the full-stack application. It orchestrates three separate services—Database (MySQL), Backend (Spring Boot), and Frontend (Angular)—allowing them to run simultaneously in isolated containers with a single command.

File Version
YAML
version: '3.8'
Description: Specifies the version of the Docker Compose file format.

Why: Version 3.8 is a modern standard that supports advanced configuration features for networks and volumes.

Services Defined
The services block defines the individual containers that make up the application.

1. The Database Service (mysql-db)
This service runs the MySQL database server.

YAML
  mysql-db:
    image: mysql:8.0
    container_name: my-mysql-container
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: mydb
      MYSQL_USER: user
      MYSQL_PASSWORD: password
    ports:
      - "3307:3306"
    networks:
      - my-app-network
image: mysql:8.0: Uses the official MySQL 8.0 image from Docker Hub. No custom build is required.

container_name: Sets a fixed name (my-mysql-container) for easier identification when running docker ps.

environment: Configures the initial database setup. These variables cause the image to automatically create a database named mydb and a user user with the specified password on the first run.

ports: "3307:3306": Maps the host machine's port 3307 to the container's internal port 3306.

External Access: Developers can connect to the DB via localhost:3307 using tools like DBeaver or Workbench.

Conflict Avoidance: Using 3307 prevents conflicts with any local MySQL server already running on the default port 3306.

networks: Connects the container to the shared my-app-network.

2. The Backend Service (backend)
This service runs the Spring Boot API.

YAML
  backend:
    build: ./backend
    container_name: my-spring-backend
    ports:
      - "8080:8080"
    depends_on:
      - mysql-db
    environment:
      DB_URL: jdbc:mysql://mysql-db:3306/mydb
      DB_USERNAME: user
      DB_PASSWORD: password
    networks:
      - my-app-network
build: ./backend: Tells Docker to build a custom image using the Dockerfile located in the ./backend directory.

ports: "8080:8080": Maps port 8080 on the host to port 8080 in the container, making the API accessible at http://localhost:8080.

depends_on: Ensures Docker starts the mysql-db container before starting the backend.

Note: This checks if the container has started, not if the database is fully ready to accept connections.

environment: Injects configuration variables into the Spring Boot application.

DB_URL: Connects to the database using the service name mysql-db as the hostname. Docker's internal DNS resolves mysql-db to the database container's IP address.

Usage: These variables are mapped to placeholders in application.properties (e.g., spring.datasource.url=${DB_URL}).

networks: Joins the shared network to communicate with the database.

3. The Frontend Service (frontend)
This service runs the Angular application served by Nginx.

YAML
  frontend:
    build: ./frontend
    container_name: my-angular-frontend
    ports:
      - "80:80"
    networks:
      - my-app-network
build: ./frontend: Builds the custom image using the Dockerfile in the ./frontend directory.

ports: "80:80": Maps the standard HTTP port 80 on the host to port 80 in the container. The application is accessible in a browser at http://localhost.

networks: Joins the shared network.

Network Configuration
YAML
networks:
  my-app-network:
    driver: bridge
driver: bridge: Creates a custom bridge network.

Purpose: This provides Network Isolation. Only containers defined in this network can communicate with each other using their service names (e.g., backend can ping mysql-db). Containers outside this network cannot access these internal services.

# # ⚠️ Important Note on Data Persistence
Currently, this configuration does not define a Volume for the database.

Implication: If the mysql-db container is removed (e.g., docker-compose down), all data stored in mydb will be permanently lost.

Recommendation: For development where data persistence is needed, add a volume mapping to /var/lib/mysql.