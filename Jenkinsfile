pipeline {
    agent any

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
        stage('Trigger the Job') {
            when {
                 expression {
                            // ❗ Prevent triggering self if already triggered upstream
                            !currentBuild.getBuildCauses().any { it.toString().contains("UpstreamCause") }
                        }
            }

            steps {
                script {
                    def buildResult = build job: 'Sele3',
                        wait: true,
                        propagate: false,
                        parameters: [
                            string(name: 'BRANCH_NAME', value: "${params.BRANCH_NAME}"),
                            string(name: 'BROWSER_NAME', value: "${params.BROWSER_NAME}"),
                            string(name: 'ENVIRONMENT', value: "${params.ENVIRONMENT}"),
                            string(name: 'TEST_SUITE', value: 'src/test/resources/agoda.xml'),
                            booleanParam(name: 'HEADLESS', value: true),
                            string(name: 'TEST_CASE_NAME', value: "${params.TEST_CASE_NAME}")
                        ]
                    echo "Triggered job ID: ${buildResult.getNumber()}"
                    env.BUILD_RESULT_ID = buildResult.getNumber().toString()
                }
            }
        }

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

        stage('Run Tests') {
            steps {
                script {
                    withCredentials([file(credentialsId: params.ENVIRONMENT, variable: 'CREDENTIALS_FILE')]) {
                        def credentials = readFile file: CREDENTIALS_FILE
                        def credentialsFilePath = "${env.WORKSPACE}/credentials.properties"
                        writeFile file: credentialsFilePath, text: credentials

                        echo "Running tests for environment: ${params.ENVIRONMENT}"

                        if (isUnix()) {
                            sh """
                                mvn test \\
                                -DTEST=${params.TEST_CASE_NAME} \\
                                -DBrowser=${params.BROWSER_NAME} \\
                                -DEnv=${params.ENVIRONMENT}
                                -DHeadless=${params.HEADLESS}
                            """
                        } else {
                            bat """
                                mvn test ^
                                -DTEST=${params.TEST_CASE_NAME} ^
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
                    allure([
                        includeProperties: false,
                        jdk: '',
                        commandline: 'allure',
                        results: [[path: 'allure-results']]
                    ])
                    cleanWs()
                }
            }
        }
    }
}
