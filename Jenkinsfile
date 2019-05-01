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

        stage('Deploy funnel persistence') {
            when {
                branch 'master'
            }

            steps {
                script {
                    def version = "undefined"
                    dir('persistence-connector') {
                        version = sh label: 'Retrieve version', returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\['"
                    }
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "dea82711-363c-4f03-b033-e09a8223f1b8",
                          options                : "version=$version",
                          rundeckInstance        : "gizmo",
                          shouldFailTheBuild     : true,
                          shouldWaitForRundeckJob: true,
                          tailLog                : true])
                }
            }
        }

        stage('Deploy funnel retriever') {
            when {
                branch 'master'
            }

            steps {
                script {
                    def version = "undefined"
                    dir('retriever-connector') {
                        version = sh label: 'Retrieve version', returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\['"
                    }
                    step([$class                 : "RundeckNotifier",
                          includeRundeckLogs     : true,
                          jobId                  : "b1b0d79f-7d05-48a0-8032-48a75d1a91ae",
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