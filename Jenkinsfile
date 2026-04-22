pipeline {
    agent none

    environment {
        IMAGE_TAG = "${BUILD_NUMBER}"
        RELEASE_NAME = "bank-app"
        CHART_PATH = "./deployment/bank-app"
    }

    stages {

        stage('Build Maven') {
            agent {
                docker {
                    image 'maven:3.9.6-eclipse-temurin-21'
                }
            }
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Build Docker Images') {
            agent any
            steps {
                sh """
                docker build -t my-bank-app-accounts-service:${IMAGE_TAG} ./accounts-service
                docker build -t my-bank-app-cash-service:${IMAGE_TAG} ./cash-service
                docker build -t my-bank-app-transfer-service:${IMAGE_TAG} ./transfer-service
                docker build -t my-bank-app-notifications-service:${IMAGE_TAG} ./notifications-service
                docker build -t my-bank-app-api-gateway:${IMAGE_TAG} ./api-gateway
                """
            }
        }

        stage('Deploy with Helm') {
            agent any
            steps {
                sh """
                helm upgrade --install ${RELEASE_NAME} ${CHART_PATH} \
                  --set accounts.image.tag=${IMAGE_TAG} \
                  --set cash.image.tag=${IMAGE_TAG} \
                  --set transfer.image.tag=${IMAGE_TAG} \
                  --set notifications.image.tag=${IMAGE_TAG} \
                  --set apiGateway.image.tag=${IMAGE_TAG}
                """
            }
        }

        stage('Helm Smoke Test') {
            agent any
            steps {
                sh "helm test ${RELEASE_NAME}"
            }
        }
    }
}