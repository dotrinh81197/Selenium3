pipeline {
    agent any

    tools {
            // Use the exact name you configured in "Global Tool Configuration"
            maven 'maven'
            allure 'allure'
        }

    environment {
        PROJECT_NAME = 'Sele3'
        EMAIL_RECIPIENTS = 'dev-team@example.com'
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch to build')
        string(name: 'TEST_CASE_NAME', defaultValue: '', description: 'Test case name to run')
        choice(name: 'ENVIRONMENT', choices: ['AGODA', 'VJ'], description: 'Target environment')
        choice(name: 'BROWSER_NAME', choices: ['chrome', 'firefox'], description: 'Target browser')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run test in headless mode')
    }

    stages {

        stage('Checkout') {
            steps {
                echo "📥 Checking out code..."
                checkout scm: [
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: scm.userRemoteConfigs
                ]
            }
        }

        stage('Clean Allure Results') {
            steps {
                echo "🧹 Cleaning previous Allure results"
                sh 'echo ✅ Shell is working'

                dir("${env.WORKSPACE}") {
                    sh 'rm -rf allure-results'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo "Running tests for environment: ${params.ENVIRONMENT}"
                    echo "Test case: ${params.TEST_CASE_NAME}"
                    echo "Browser: ${params.BROWSER_NAME}"
                    echo "Headless: ${params.HEADLESS}"

                    if (isUnix()) {
                        if (params.TEST_CASE_NAME?.trim()) {
                            sh """
                                mvn test \\
                                -DTEST=${params.TEST_CASE_NAME} \\
                                -DBrowser=${params.BROWSER_NAME} \\
                                -DEnv=${params.ENVIRONMENT} \\
                                -DHeadless=${params.HEADLESS}
                            """
                        } else {
                            sh """
                                mvn test \\
                                -DsuiteXmlFile=src/test/resources/agoda.xml \\
                                -DBrowser=${params.BROWSER_NAME} \\
                                -DEnv=${params.ENVIRONMENT} \\
                                -DHeadless=${params.HEADLESS}
                            """
                        }
                    } else {
                        if (params.TEST_CASE_NAME?.trim()) {
                            bat """
                                mvn test ^
                                -DTEST=${params.TEST_CASE_NAME} ^
                                -DBrowser=${params.BROWSER_NAME} ^
                                -DEnv=${params.ENVIRONMENT} ^
                                -DHeadless=${params.HEADLESS}
                            """
                        } else {
                            bat """
                                mvn test ^
                                -DsuiteXmlFile=src/test/resources/agoda.xml ^
                                -DBrowser=${params.BROWSER_NAME} ^
                                -DEnv=${params.ENVIRONMENT} ^
                                -DHeadless=${params.HEADLESS}
                            """
                        }
                    }
                }
            }

         post {
             always {
                 allure includeProperties: false, jdk: '', results: [[path: 'allure-results/*']]
                    sh 'which allure'
                    sh 'echo $PATH'

                 echo "📊 Generating Allure report"
                 sh 'allure generate --clean --single-file ./allure-results/report-*'
                 archiveArtifacts artifacts: 'allure-report/*'
                 cleanWs()
             }
         }

        }
    }
}
