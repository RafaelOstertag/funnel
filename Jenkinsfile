pipeline {
    agent {
        label 'freebsd&&kotlin'
    }

    triggers {
        pollSCM ''
        cron '@daily'
    }

    tools {
        maven 'Latest Maven'
    }

    options {
        ansiColor('xterm')
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')
        timestamps()
    }

    stages {
        stage('clean') {
            steps {
                sh label: 'maven clean', script: 'mvn -B clean'
            }
        }

        stage('Build and Test') {
            steps {
                sh label: 'maven install', script: 'mvn -B install'
            }
        }

        stage('Publish test results') {
            steps {
                junit '**/failsafe-reports/*.xml,**/surefire-reports/*.xml'
            }
        }

        stage('Deploy to Nexus') {
            when {
                branch 'master'
                not {
                    triggeredBy "TimerTrigger"
                }
            }

            steps {
                configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                    sh label: 'maven deploy', script: 'mvn -B -s "$MAVEN_SETTINGS_XML" -DskipTests deploy'
                }
            }
        }

        stage('Deploy') {
            when {
                branch 'master'
                not {
                  triggeredBy "TimerTrigger"
                }
            }

            steps {
                script {
                    def version = "undefined"
                    version = sh label: 'Retrieve version', returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\['"

                    // retriever-connector
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "a5087093-c53d-4939-ab6f-298b5e5ffb18",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])

                    // persistence-connector
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "efac0085-3408-48c4-8ad9-7c8d75e06fbd",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])

                    // chronos
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "c8b6ecc9-d695-4815-b333-c953b3c3b31a",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])

                    // rest
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "713a8522-ed25-4b05-8253-3a36253a71b6",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])

                    // notifier
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "86370596-68bb-47ac-ab25-a4e4a4abcc2a",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])
                }
            }
        }
    }

    post {
        unsuccessful {
            mail to: "rafi@guengel.ch",
                    subject: "${JOB_NAME} (${BRANCH_NAME};${env.BUILD_DISPLAY_NAME}) -- ${currentBuild.currentResult}",
                    body: "Refer to ${currentBuild.absoluteUrl}"
        }
    }
}