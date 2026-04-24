pipeline {
    agent any

    tools {
            maven 'Maven'
            jdk 'JDK21'
        }

        environment {
            JMETER_HOME = '/Users/AinaPereira/Desktop/SW/apache-jmeter-5.6.3'
            PATH = "${env.JMETER_HOME}/bin:${env.PATH}"
        }

    triggers {
        pollSCM('* * * * *')
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('Run Unit Tests') {
            steps {
                echo 'Running TestNG unit tests...'
                sh 'mvn clean test'
            }
            post {
                always {
                    // Publish TestNG report
                    publishTestNGResults testResultsPattern: '**/surefire-reports/testng-results.xml', escapeHtml: true

                    // Fallback for Jenkins default test report
                    //junit '**/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Run JMeter Tests') {
            steps {
                echo 'Running JMeter tests...'
                sh '''
                mkdir -p jmeter-report

                jmeter -n -t jmeter/user-api-load-test.jmx -l results.jtl -e -o jmeter-report

                if [ ! -f results.jtl ]; then
                    echo "JMeter test failed"
                    exit 1
                fi
                '''
            }
        }

        stage ('Publish JMeter Report') {
            steps {
                publishHTML target: [
                    reportName: 'JMeter Report',
                    reportDir: 'jmeter-report',
                    reportFiles: 'index.html',
                    keepAll: true,
                    allowMissing: false,
                    alwaysLinkToLastBuild: true
                ]
            }
        }
    }

    post {

        success {
            mail to: 'team@example.com',
                 subject: "Build Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """
Hi Team,

The build completed successfully.

Job: ${env.JOB_NAME}
Build Number: ${env.BUILD_NUMBER}
Build URL: ${env.BUILD_URL}

All stages completed successfully. Please refer to reports for details.

Thanks,
Aina
"""
        }

        failure {
            mail to: 'team@example.com',
                 subject: "Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """
Hi Team,

The build has failed.

Job: ${env.JOB_NAME}
Build Number: ${env.BUILD_NUMBER}
Build URL: ${env.BUILD_URL}

Please check the console logs and reports for details.

Thanks,
Aina
"""
        }
    }
}