pipeline {
    agent none  // No default agent, we assign per stage

    environment {
        DOCKER_HUB_USER = 'junaiduthman'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        REGISTRY_CREDS = 'docker-hub-credentials'
        // tell Docker CLI to use the TCP socket where socat forwards the host daemon
        DOCKER_HOST = 'tcp://host.docker.internal:2376'

    }

    stages {

        // --- Stage 1: Checkout Code ---
        stage('Checkout Code') {
            agent { label 'backend-agent' }  // Pull code on backend agent
            steps {
                checkout scm
            }
        }

        //--- Stage 2: Build Backend ---
        stage('Build Backend (Spring)') {
            agent { label 'backend-agent' }
            tools {
                jdk 'jdk17'
                maven 'maven3'
            }
            steps {
                dir('backend') {
                    echo 'Compiling Backend...'
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        // --- Stage 3: Build Frontend ---
        stage('Build Frontend (Angular)') {
            agent { label 'frontend-agent' }
            tools {
                nodejs 'node18'
            }
            steps {
                dir('frontend') {
                    echo 'Compiling Frontend...'
                     sh 'node -v'   // verifies Node is installed
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        // --- Stage 4: Build & Push Docker Images ---
        stage('Build & Push Docker Images') {
            agent { label 'dockerhub-agent' }  // Dedicated Docker push agent
            tools {
                jdk 'jdk17'  // optional if your Docker images need Java
            }
            steps {
                script {
                    echo 'Building Docker Images...'
                    
                    // Build backend image
                    def backendImage = docker.build("${DOCKER_HUB_USER}/my-backend:${IMAGE_TAG}", "./backend")
                    
                    // Build frontend image
                    def frontendImage = docker.build("${DOCKER_HUB_USER}/my-frontend:${IMAGE_TAG}", "./frontend")
                    
                    // Push to Docker Hub using global credentials
                    docker.withRegistry('', REGISTRY_CREDS) {
                        backendImage.push()
                        backendImage.push('latest')
                        frontendImage.push()
                        frontendImage.push('latest')
                    }
                }
            }
        }
    }

    post {
        always {
            node { 
                cleanWs() 
                echo 'Pipeline finished.'
            }
        }
        success {
            echo 'Great success! Images pushed to Docker Hub.'
        }
        failure {
            echo 'Pipeline failed. Check the logs!'
        }
    }
}
