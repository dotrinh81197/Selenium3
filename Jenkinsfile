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
        string(name: 'BRANCH_NAME', defaultValue: 'trdo/updateJenkins', description: 'Branch to build')
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

        stage('Clean Allure Results') {
            steps {
                echo "🧹 Cleaning previous Allure results"
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

                 echo "📊 Generating Allure report"
                 sh 'allure generate --clean --single-file allure-results -o allure-report'
                 archiveArtifacts artifacts: 'allure-report/*'
                 cleanWs()
             }
         }

        }
    }
}
