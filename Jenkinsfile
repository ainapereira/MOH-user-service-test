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
                    //publishTestNGResults testResultsPattern: '**/surefire-reports/testng-results.xml', escapeHtml: true

                    // Fallback for Jenkins default test report
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Run JMeter Tests') {
            steps {
                echo 'Running JMeter tests...'
                //mkdir -p jmeter-report //fallback if jmeter doesn't create the report directory
                //jmeter properties are added for html reporting and to ensure we get all the necessary data in the results.jtl file for analysis
                sh '''
                 rm -rf jmeter-report results.jtl

                jmeter -n -t jmeter/getuser-api-load-test.jmx -l results.jtl\
                -j jmeter.log\
                -Jjmeter.save.saveservice.output_format=csv \
                -Jjmeter.save.saveservice.time=true \
                -Jjmeter.save.saveservice.timestamp_format=ms \
                -Jjmeter.save.saveservice.response_data=false \
                -Jjmeter.save.saveservice.samplerData=true \
                -Jjmeter.save.saveservice.label=true \
                -Jjmeter.save.saveservice.response_code=true \
                -Jjmeter.save.saveservice.success=true \
                -Jjmeter.save.saveservice.response_message=true \
                -Jjmeter.save.saveservice.elapsed=true \
                -Jjmeter.save.saveservice.thread_name=true \
                -Jjmeter.save.saveservice.latency=true \
                -Jjmeter.save.saveservice.connect_time=true \
                -Jjmeter.save.saveservice.bytes=true \
                -e -o jmeter-report

                if [ ! -f results.jtl ]; then
                    echo "JMeter test failed"
                    exit 1
                fi

                if [ ! -s results.jtl ]; then
                    echo "JMeter results file is empty"
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
            mail to: 'hai4uaina@gmail.com',
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
            mail to: 'hai4uaina@gmail.com',
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