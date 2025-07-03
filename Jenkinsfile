pipeline {
    agent any

    triggers {
        cron('30 21 * * 4')
    }

    tools {
        maven 'maven'
        allure 'allure'
    }

    environment {
        PROJECT_NAME = 'Sele3'
        ALLURE_HOME = tool 'allure'
        PATH = "${ALLURE_HOME}/bin:${env.PATH}"
        GRID_URL = "http://localhost:4444/wd/hub"
        SELENIUM_SERVER_JAR = 'selenium-server-4.20.0.jar'
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch to build')
        string(name: 'TEST_CASE_NAME', defaultValue: '', description: 'Test case name to run')
        choice(name: 'TEST_SUITE', choices: ['src/test/resources/agoda.xml', 'src/test/resources/vj.xml'], description: 'Test suite to run')
        choice(name: 'ENVIRONMENT', choices: ['AGODA', 'VJ'], description: 'Target environment')
        choice(name: 'BROWSER_NAME', choices: ['chrome', 'firefox'], description: 'Target browser')
        booleanParam(name: 'USE_GRID', defaultValue: true, description: 'Run tests on Selenium Grid')
        choice(name: 'PARALLEL_MODE', choices: ['methods', 'classes', 'tests'], description: 'Parallel execution mode')
        choice(name: 'THREAD_COUNT', choices: ['2', '4', '5', '10'], description: 'Thread count for parallel execution')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run tests in headless mode')
        string(name: 'EMAIL_RECIPIENTS', defaultValue: 'trinhzyn@gmail.com', description: 'Emails for result notifications')
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo "📥 Checking out code from branch: ${params.BRANCH_NAME}"
                checkout scm: [
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH_NAME}"]],
                    userRemoteConfigs: scm.userRemoteConfigs
                ]
            }
        }

        stage('Clean Previous Artifacts') {
            steps {
                echo "🧹 Cleaning previous Allure artifacts and reports"
                sh 'rm -rf allure-results allure-report allure-report-single'
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo "🧪 Executing tests..."
                    def remoteOption = params.USE_GRID ? "-Dselenide.remote=${GRID_URL}" : ""

                    def mvnCmd = params.TEST_CASE_NAME?.trim() ?
                        "mvn test -Dtest=${params.TEST_CASE_NAME} -Dselenide.browser=${params.BROWSER_NAME} -DEnv=${params.ENVIRONMENT} -Dselenide.headless=${params.HEADLESS} -Dselenide.browserSize=1920x1080 -Dparallel=${params.PARALLEL_MODE} -DthreadCount=${params.THREAD_COUNT} ${remoteOption}" :
                        "mvn test -DsuiteXmlFile=${params.TEST_SUITE} -Dselenide.browser=${params.BROWSER_NAME} -DEnv=${params.ENVIRONMENT} -Dselenide.headless=${params.HEADLESS} -Dselenide.browserSize=1920x1080 -Dparallel=${params.PARALLEL_MODE} -DthreadCount=${params.THREAD_COUNT} ${remoteOption}"

                    sh mvnCmd
                }
            }
        }
    }

    post {
        always {
            script {
                echo "📊 Generating Allure reports"
                allure includeProperties: false, jdk: '', results: [[path: 'allure-results/*']]

                // Generate single-file report for attachment
                sh "allure generate --clean --single-file ./allure-results/report-* -o allure-report-single"
                archiveArtifacts artifacts: 'allure-report-single/*'

                // Generate standard report for Jenkins plugin
                sh "allure generate --clean ./allure-results/report-* -o allure-report"

               def jsonText = readFile 'allure-report/widgets/summary.json'
               def data = new groovy.json.JsonSlurper().parseText(jsonText).statistic

               def total = data.total ?: 0
               def passed = data.passed ?: 0
               def failed = data.failed ?: 0
               def broken = data.broken ?: 0
               def skipped = data.skipped ?: 0

               // Immediately nullify non-serializable references
               data = null

                def summaryHtml = """
                <h3>📊 Test Summary</h3>
                <table border='1' cellpadding='6' cellspacing='0' style='border-collapse:collapse; text-align:center; font-family:sans-serif;'>
                    <tr style='background-color:#f2f2f2;'>
                        <th>Total</th>
                        <th style='color:green;'>Passed</th>
                        <th style='color:red;'>Failed</th>
                        <th style='color:orange;'>Broken</th>
                        <th style='color:gray;'>Skipped</th>
                    </tr>
                    <tr>
                        <td><strong>${total}</strong></td>
                        <td style='color:green;'><strong>${passed}</strong></td>
                        <td style='color:red;'><strong>${failed}</strong></td>
                        <td style='color:orange;'><strong>${broken}</strong></td>
                        <td style='color:gray;'><strong>${skipped}</strong></td>
                    </tr>
                </table>
                """.stripIndent()

                // Send email with summary and single-file report attached
                emailext(
                    subject: "🔔 ${env.PROJECT_NAME} Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult} - Trinh Do",
                    body: """
                        <p>Hi Team,</p>
                        <p>Build Result: <strong>${currentBuild.currentResult}</strong></p>
                        ${summaryHtml}
                        <p>Allure report is attached for offline viewing.</p>
                        <p>📊 <a href='${env.BUILD_URL}allure'>Click here to view the Allure Report on Jenkins</a></p>
                    """,
                    mimeType: 'text/html',
                    attachmentsPattern: 'allure-report-single/index.html',
                    to: "${params.EMAIL_RECIPIENTS}"
                )
            }
        }
        cleanup {
            cleanWs()
        }
    }
}
