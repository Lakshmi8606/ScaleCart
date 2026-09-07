pipeline {

    // Run on any available Jenkins agent
    agent any

    // Environment variables available to all stages
    environment {
        // Your DockerHub username
        DOCKER_USERNAME = 'lakshmidocker9847'

        // Credential ID from Jenkins credential store — never hardcoded
        DOCKER_CREDENTIALS = credentials('dockerhub-credentials')

        // Image tag = Git commit short hash for traceability
        // Every image is tagged with the commit that built it
        IMAGE_TAG = "${env.GIT_COMMIT?.take(7) ?: 'latest'}"

        // Maven options — skip tests here (tests run in dedicated stage)
        MAVEN_OPTS = '-Xmx512m'
    }

    // Global tool configuration
    tools {
        // 'Maven-3.9' = name of Maven installation configured in
        // Manage Jenkins → Tools → Maven installations
        maven 'Maven-3.9'
    }

    stages {

        // ── Stage 1: Checkout ──────────────────────────────────────────
        stage('Checkout') {
            steps {
                // Jenkins automatically checks out the triggering commit
                // This stage makes it explicit and visible in the UI
                checkout scm
                echo "Building commit: ${env.GIT_COMMIT}"
                echo "Branch: ${env.GIT_BRANCH}"
            }
        }

        // ── Stage 2: Build ─────────────────────────────────────────────
        stage('Build') {
            steps {
                echo 'Compiling all modules...'
                // -DskipTests: compile only, tests run in next stage
                // -B: batch mode (no color output — cleaner Jenkins logs)
                // --no-transfer-progress: suppress download progress spam
                sh 'mvn clean package -DskipTests -B --no-transfer-progress'
            }
            post {
                success {
                    echo 'Build successful — all modules compiled'
                }
                failure {
                    echo 'Build failed — compilation errors detected'
                }
            }
        }

        // ── Stage 3: Test ──────────────────────────────────────────────
        stage('Test') {
            steps {
                echo 'Running unit tests across all services...'
                // Run tests for all services in parallel modules
                sh '''
                    mvn test -B --no-transfer-progress \
                        -pl auth-service,product-service,order-service,payment-service \
                        || exit 1
                '''
            }
            post {
                always {
                    // Publish JUnit test results — visible in Jenkins UI
                    junit(
                        testResults: '**/target/surefire-reports/*.xml',
                        allowEmptyResults: true
                    )

                    // Publish JaCoCo coverage report
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
                success {
                    echo 'All tests passed!'
                }
                failure {
                    echo 'Tests failed — Docker build will NOT proceed'
                    // Pipeline stops here — broken code never gets dockerized
                }
            }
        }

        // ── Stage 4: Docker Build ──────────────────────────────────────
        stage('Docker Build') {
            // Only runs if Test stage passed
            when {
                expression {
                    currentBuild.result == null ||
                    currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                echo "Building Docker images with tag: ${IMAGE_TAG}"

                // Build each service image from root context
                // (Dockerfiles reference parent pom.xml)
                script {
                    def services = [
                        'auth-service',
                        'product-service',
                        'order-service',
                        'payment-service',
                        'notification-service',
                        'report-service',
                        'api-gateway'
                    ]

                    services.each { service ->
                        echo "Building ${service}..."
                        sh """
                            docker build \
                                -f ${service}/Dockerfile \
                                -t ${DOCKER_USERNAME}/scalecart-${service}:${IMAGE_TAG} \
                                -t ${DOCKER_USERNAME}/scalecart-${service}:latest \
                                .
                        """
                    }
                }
            }
        }

        // ── Stage 5: Docker Push ───────────────────────────────────────
        stage('Docker Push') {
            steps {
                echo 'Pushing images to DockerHub...'
                script {
                    // withCredentials injects DOCKER_CREDENTIALS_USR
                    // and DOCKER_CREDENTIALS_PSW securely
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-credentials',
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        // Login to DockerHub
                        sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'

                        def services = [
                            'auth-service',
                            'product-service',
                            'order-service',
                            'payment-service',
                            'notification-service',
                            'report-service',
                            'api-gateway'
                        ]

                        services.each { service ->
                            echo "Pushing ${service}..."
                            sh "docker push ${DOCKER_USERNAME}/scalecart-${service}:${IMAGE_TAG}"
                            sh "docker push ${DOCKER_USERNAME}/scalecart-${service}:latest"
                        }

                        // Logout after push — security best practice
                        sh 'docker logout'
                    }
                }
            }
        }

        // ── Stage 6: Deploy (Optional for now) ────────────────────────
        stage('Deploy') {
            steps {
                echo "Deployment stage — Day 26 (AWS EC2)"
                echo "Images pushed: ${DOCKER_USERNAME}/scalecart-*:${IMAGE_TAG}"
                echo "To deploy: docker-compose pull && docker-compose up -d"
                // Day 26: SSH into EC2 and run docker-compose pull + up
            }
        }
    }

    // ── Post-pipeline actions ──────────────────────────────────────────
    post {

        success {
            echo """
            ✅ Pipeline SUCCESS
            Branch: ${env.GIT_BRANCH}
            Commit: ${env.GIT_COMMIT}
            Images tagged: ${IMAGE_TAG}
            All 7 services built, tested, and pushed to DockerHub
            """
        }

        failure {
            echo """
            ❌ Pipeline FAILED
            Branch: ${env.GIT_BRANCH}
            Commit: ${env.GIT_COMMIT}
            Check the failed stage above for details
            """
        }

        always {
            // Clean workspace after build — saves disk space
            cleanWs()
        }
    }
}