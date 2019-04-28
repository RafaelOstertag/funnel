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
                sh 'mvn -B clean'
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn -B install'
            }
        }

        stage('Deploy to Nexus') {
            when {
                branch 'master'
            }

            steps {
                configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                    sh 'mvn -B -s "$MAVEN_SETTINGS_XML" -DskipTests deploy'
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