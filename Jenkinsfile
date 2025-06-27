pipeline {
    agent any

    tools {
        maven 'maven'
        allure 'allure'
    }

    environment {
        PROJECT_NAME = 'Sele3'
        ALLURE_HOME = tool 'allure'
        PATH = "${ALLURE_HOME}/bin:${env.PATH}"
        GRID_URL = "http://localhost:4444/wd/hub"
    }

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch to build')
        string(name: 'TEST_CASE_NAME', defaultValue: '', description: 'Test case name to run')
        choice(name: 'TEST_SUITE', description: 'Test suite to run', choices: ['src/test/resources/agoda.xml', 'src/test/resources/vj.xml'])
        choice(name: 'ENVIRONMENT', choices: ['AGODA', 'VJ'], description: 'Target environment')
        choice(name: 'BROWSER_NAME', choices: ['chrome', 'firefox'], description: 'Target browser')
        booleanParam(name: 'USE_GRID', defaultValue: true, description: 'Run tests on Selenium Grid')
        choice(name: 'PARALLEL_MODE', choices: ['methods', 'classes', 'tests'], description: 'Parallel execution mode')
        choice(name: 'THREAD_COUNT', choices: ['2', '4', '5', '10'], description: 'Thread count for parallel execution')
        booleanParam(name: 'HEADLESS', defaultValue: true, description: 'Run headless')
        string(name: 'EMAIL_RECIPIENTS', defaultValue: 'trinhzyn@gmail.com', description: 'Emails')
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

        stage('Clean Previous Allure Results') {
            steps {
                echo "🧹 Cleaning previous Allure results"
                sh 'rm -rf allure-results allure-report allure-report-single'
            }
        }

        stage('Start Local Grid (if needed)') {
            when { expression { params.USE_GRID } }
            steps {
                script {
                    def gridStatus = sh(script: "nc -z localhost 4444 || echo 'not running'", returnStdout: true).trim()
                    if (gridStatus == 'not running') {
                        echo "🚀 Starting local Selenium Grid..."
                        sh "nohup java -jar selenium-server-4.20.0.jar standalone > grid.log 2>&1 &"
                        sleep 10
                    } else {
                        echo "✅ Selenium Grid already running."
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    echo "🧪 Running tests "
                    def remoteOption = params.USE_GRID ? "-Dselenide.remote=${GRID_URL}" : ""

                    def mvnCmd = params.TEST_CASE_NAME?.trim() ?
                        "mvn test -Dtest=${params.TEST_CASE_NAME} -Dselenide.browser=${params.BROWSER_NAME} -DEnv=${params.ENVIRONMENT} -Dselenide.headless=${params.HEADLESS} -Dselenide.browserSize=1920x1080 -Dparallel=${params.PARALLEL_MODE} ${remoteOption}" :
                        "mvn test -DsuiteXmlFile=${params.TEST_SUITE} -Dselenide.browser=${params.BROWSER_NAME} -DEnv=${params.ENVIRONMENT} -Dselenide.headless=${params.HEADLESS} -Dselenide.browserSize=1920x1080 -Dparallel=${params.PARALLEL_MODE} ${remoteOption}"

                    sh mvnCmd
                }
            }
        }
    }

    post {
        always {
            script {
                echo "📊 Generating Allure report"

                // Generate normal report for Jenkins plugin viewing
                sh "allure generate --clean ./allure-result/report-* -o allure-report"

                // Generate single-file for email attachment
                sh "allure generate --clean --single-file ./allure-result/report-* -o allure-report-single"

                // Parse summary.json safely
                def jsonText = readFile 'allure-report/widgets/summary.json'
                def jsonObj = new groovy.json.JsonSlurper().parseText(jsonText)
                def stats = jsonObj.statistic

                def total = stats.total ?: 0
                def passed = stats.passed ?: 0
                def failed = stats.failed ?: 0
                def broken = stats.broken ?: 0
                def skipped = stats.skipped ?: 0

                def summaryHtml = """
                <h3>Test Summary</h3>
                <table border='1' cellpadding='4' cellspacing='0'>
                <tr><th>Total</th><th>Passed</th><th>Failed</th><th>Broken</th><th>Skipped</th></tr>
                <tr><td>${total}</td><td>${passed}</td><td>${failed}</td><td>${broken}</td><td>${skipped}</td></tr>
                </table>
                """.stripIndent()

                emailext(
                    subject: "🔔 ${env.PROJECT_NAME} Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
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
