pipeline {
    agent any

    stages {
        stage('pull source'){
            steps {
                echo "pull sourceCode!"
                git branch: 'master-prod', url: 'https://github.com/ryanimay/ERP-Base.git'
            }
        }
        stage('Build') {
            steps {
                echo "Shell start!"
                sh 'mvn clean package'
                echo "Maven clean package successfully!"
            }
        }
        stage('Docker Login') {
            steps {
                echo "Logging into Docker Hub..."
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKERHUB_USR', passwordVariable: 'DOCKERHUB_PSW')]) {
                    script {
                        sh """
                            echo "${DOCKERHUB_PSW}" | docker login -u "${DOCKERHUB_USR}" --password-stdin
                        """
                    }
                }
            }
        }
        stage('Build Docker Image') {
            steps {
                echo "Building Docker image..."
                sh 'docker build -t ryanimay840121/erp-base:latest .'
            }
        }
        stage('Push Docker Image') {
            steps {
                echo "Pushing Docker image to Docker Hub..."
                sh 'docker push ryanimay840121/erp-base:latest'
                echo "Docker image has been pushed successfully!"
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                echo "Deploying to Kubernetes..."
                sh 'kubectl apply -f ./erp-base-deployment.yml'
                echo "Deployment applied successfully!"
            }
        }
    }
}