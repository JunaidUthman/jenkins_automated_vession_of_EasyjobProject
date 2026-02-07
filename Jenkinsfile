pipeline {
    agent any

    // 1. Define the tools we configured in Step 1
    tools {
        //jdk 'jdk17'
        maven 'maven3'
        nodejs 'node18'
    }

    // 2. Define variables (Environment variables)
    environment {
        // Your Docker Hub Username
        DOCKER_HUB_USER = 'junaiduthman' 
        // A unique tag for this build (using the Jenkins Build Number)
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        // The ID you created in Step 2
        REGISTRY_CREDS = 'docker-hub-credentials'
    }

    stages {
        
        // --- Stage A: Get the code ---
        stage('Checkout Code') {
            steps {
                // Pulls code from the repo where this Jenkinsfile lives
                checkout scm 
            }
        }

        // --- Stage B: Compile & Test Backend ---
        stage('Build Backend (Spring)') {
            steps {
                dir('backend') { // Go into the 'backend' folder
                    echo 'Compiling Backend...'
                    // Run Maven Wrapper or Maven tool
                    sh 'mvn clean package -DskipTests' 
                    // Ideally remove -DskipTests to actually run unit tests!
                }
            }
        }

        // --- Stage C: Compile Frontend ---
        stage('Build Frontend (Angular)') {
            steps {
                dir('frontend') { // Go into the 'frontend' folder
                    echo 'Compiling Frontend...'
                    sh 'npm install'
                    sh 'npm run build' // Creates the 'dist' folder
                }
            }
        }

        // --- Stage D: Build Docker Images ---
        stage('Build Docker Images') {
            steps {
                script {
                    echo 'Building Docker Images...'
                    
                    // Build Backend Image
                    // This looks for a Dockerfile in ./backend
                    def backendImage = docker.build("${DOCKER_HUB_USER}/my-backend:${IMAGE_TAG}", "./backend")
                    
                    // Build Frontend Image
                    // This looks for a Dockerfile in ./frontend
                    def frontendImage = docker.build("${DOCKER_HUB_USER}/my-frontend:${IMAGE_TAG}", "./frontend")
                    
                    // Log in to Docker Hub and Push
                    docker.withRegistry('', REGISTRY_CREDS) {
                        backendImage.push()
                        backendImage.push('latest') // Also update the 'latest' tag
                        
                        frontendImage.push()
                        frontendImage.push('latest')
                    }
                }
            }
        }
    }
    
    // 3. Post-build actions (Notifications/Cleanup)
    post {
        always {
            // Clean up workspace to save disk space
            cleanWs() 
            echo 'Pipeline finished.'
        }
        success {
            echo 'Great success! Images pushed to Docker Hub.'
        }
        failure {
            echo 'Pipeline failed. Check the logs!'
        }
    }
}