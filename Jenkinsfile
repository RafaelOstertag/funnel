pipeline {
    agent {
        label 'freebsd&&kotlin'
    }

    triggers {
        pollSCM ''
    }

    tools {
        maven 'Maven 3.5.4'
    }

    options {
        ansiColor('xterm')
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')
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
            }

            steps {
                script {
                    def version = "undefined"
                    version = sh label: 'Retrieve version', returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\['"

                    // retriever-connector
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "b1b0d79f-7d05-48a0-8032-48a75d1a91ae",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])

                    // persistence-connector
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "dea82711-363c-4f03-b033-e09a8223f1b8",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])

                    // chronos
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "88b6f9c6-ae5c-49a7-b594-510a8a42c2ce",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])

                    // rest
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "5449aefb-9bb2-4e59-aa93-88f7fc33a974",
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
        always {
            mail to: "rafi@guengel.ch",
                    subject: "${JOB_NAME} (${BRANCH_NAME};${env.BUILD_DISPLAY_NAME}) -- ${currentBuild.currentResult}",
                    body: "Refer to ${currentBuild.absoluteUrl}"
        }
    }
}