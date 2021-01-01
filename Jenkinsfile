pipeline {
    agent {
        label 'amd64&&docker&&kotlin'
    }

    triggers {
        pollSCM '@hourly'
        cron '@daily'
    }

    tools {
        maven 'Latest Maven'
    }

    options {
        ansiColor('xterm')
        buildDiscarder logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '15')
        timestamps()
        disableConcurrentBuilds()
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

        stage('Sonarcloud') {
            steps {
                withSonarQubeEnv(installationName: 'Sonarcloud', credentialsId: 'e8795d01-550a-4c05-a4be-41b48b22403f') {
                    sh label: 'sonarcloud', script: "mvn $SONAR_MAVEN_GOAL"
                }
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

        stage('Build & Push Docker Image') {
            agent {
                label "arm64&&docker"
            }

            environment {
                VERSION = sh returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\[' | tr -d '\\n'"
            }

            when {
                branch 'master'
                not {
                    triggeredBy "TimerTrigger"
                }
            }

            steps {
                sh "docker build --build-arg 'VERSION=${env.VERSION}' -t rafaelostertag/funnel-chronos:${env.VERSION} docker/chronos"
                sh "docker build --build-arg 'VERSION=${env.VERSION}' -t rafaelostertag/funnel-notifier:${env.VERSION} docker/notifier"
                sh "docker build --build-arg 'VERSION=${env.VERSION}' -t rafaelostertag/funnel-persistence:${env.VERSION} docker/persistence"
                sh "docker build --build-arg 'VERSION=${env.VERSION}' -t rafaelostertag/funnel-retriever:${env.VERSION} docker/retriever"
                sh "docker build --build-arg 'VERSION=${env.VERSION}' -t rafaelostertag/funnel-rest:${env.VERSION} docker/rest"
                withCredentials([usernamePassword(credentialsId: '750504ce-6f4f-4252-9b2b-5814bd561430', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
                    sh 'docker login --username "$USERNAME" --password "$PASSWORD"'
                    sh "docker push rafaelostertag/funnel-chronos:${env.VERSION}"
                    sh "docker push rafaelostertag/funnel-notifier:${env.VERSION}"
                    sh "docker push rafaelostertag/funnel-persistence:${env.VERSION}"
                    sh "docker push rafaelostertag/funnel-retriever:${env.VERSION}"
                    sh "docker push rafaelostertag/funnel-rest:${env.VERSION}"
                }
            }
        }

        stage('Deploy to k8s') {
            agent {
                label "helm"
            }

            environment {
                VERSION = sh returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\[' | tr -d '\\n'"
            }

            when {
                branch 'master'
                not {
                    triggeredBy "TimerTrigger"
                }
            }

            steps {
                withKubeConfig(credentialsId: 'a9fe556b-01b0-4354-9a65-616baccf9cac') {
                    sh "helm upgrade -n funnel -i --set image.tag=${env.VERSION} chronos helm/chronos"
                    sh "helm upgrade -n funnel -i --set image.tag=${env.VERSION} notifier helm/notifier"
                    sh "helm upgrade -n funnel -i --set image.tag=${env.VERSION} persistence helm/persistence"
                    sh "helm upgrade -n funnel -i --set image.tag=${env.VERSION} rest helm/rest"
                    sh "helm upgrade -n funnel -i --set image.tag=${env.VERSION} retriever helm/retriever"
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