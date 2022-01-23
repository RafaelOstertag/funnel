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
        stage('Build and Test') {
            steps {
                configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                    sh label: 'maven install', script: 'mvn -B -s "$MAVEN_SETTINGS_XML" -Dquarkus.package.type=uber-jar clean install'
                }
            }

            post {
                always {
                    junit '**/failsafe-reports/*.xml,**/surefire-reports/*.xml'
                    jacoco()
                }
            }
        }

        stage('Sonarcloud') {
            steps {
                configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                    withSonarQubeEnv(installationName: 'Sonarcloud', credentialsId: 'e8795d01-550a-4c05-a4be-41b48b22403f') {
                        sh label: 'sonarcloud', script: "mvn -B -s \"$MAVEN_SETTINGS_XML\" -Dsonar.branch.name=${env.BRANCH_NAME} $SONAR_MAVEN_GOAL"
                    }
                }
            }
        }

        stage("Quality Gate") {
            steps {
                timeout(time: 30, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage("Check Dependencies") {
            steps {
                configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                    sh 'mvn -B -s "$MAVEN_SETTINGS_XML" -Psecurity-scan dependency-check:check dependency-check:aggregate'
                }
                dependencyCheckPublisher failedTotalCritical: 1, failedTotalHigh: 5, failedTotalLow: 8, failedTotalMedium: 8, pattern: 'target/dependency-check-report.xml', unstableTotalCritical: 0, unstableTotalHigh: 4, unstableTotalLow: 8, unstableTotalMedium: 8
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
                    sh label: 'maven deploy', script: 'mvn -B -s "$MAVEN_SETTINGS_XML" -DskipTests -Dquarkus.package.type=uber-jar deploy'
                }
            }
        }

        stage('Build & Push Development Docker Image') {
            when {
                branch 'develop'
                not {
                    triggeredBy "TimerTrigger"
                }
            }

            parallel {
                stage("ARM64") {
                    agent {
                        label "arm64&&docker&&kotlin"
                    }
                    steps {
                        configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                            sh label: 'maven build', script: 'mvn -B -s "$MAVEN_SETTINGS_XML" -DskipTests clean install'
                        }
                        buildDockerImage("notifier", "latest-arm64")
                        buildDockerImage("rest", "latest-arm64")
                        buildDockerImage("xml-retriever", "latest-arm64")
                    }
                }

                stage("AMD64") {
                    steps {
                        configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                            sh label: 'maven build', script: 'mvn -B -s "$MAVEN_SETTINGS_XML" -DskipTests clean install'
                        }
                        buildDockerImage("notifier", "latest-amd64")
                        buildDockerImage("rest", "latest-amd64")
                        buildDockerImage("xml-retriever", "latest-amd64")
                    }
                }
            }
        }

        stage('Build Development Multi Arch Docker Manifest') {
            when {
                branch 'develop'
                not {
                    triggeredBy "TimerTrigger"
                }
            }

            steps {
                buildMultiArchManifest("latest")
            }
        }

        stage('Build & Push Release Docker Image') {
            when {
                branch 'master'
                not {
                    triggeredBy "TimerTrigger"
                }
            }

            parallel {
                stage("ARM64") {
                    agent {
                        label "arm64&&docker&&kotlin"
                    }

                    environment {
                        VERSION = sh returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\[' | tr -d '\\n'"
                    }

                    steps {
                        configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                            sh label: 'maven build', script: 'mvn -B -s "$MAVEN_SETTINGS_XML" -DskipTests clean install'
                        }
                        buildDockerImage("notifier", env.VERSION + "-arm64")
                        buildDockerImage("rest", env.VERSION+ "-arm64")
                        buildDockerImage("xml-retriever", env.VERSION+ "-arm64")
                    }
                }

                stage("AMD64") {
                    environment {
                        VERSION = sh returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\[' | tr -d '\\n'"
                    }

                    steps {
                        configFileProvider([configFile(fileId: '04b5debb-8434-4986-ac73-dfd1f2045515', variable: 'MAVEN_SETTINGS_XML')]) {
                            sh label: 'maven build', script: 'mvn -B -s "$MAVEN_SETTINGS_XML" -DskipTests clean install'
                        }
                        buildDockerImage("notifier", env.VERSION + "-amd64")
                        buildDockerImage("rest", env.VERSION+ "-amd64")
                        buildDockerImage("xml-retriever", env.VERSION+ "-amd64")
                    }
                }
            }
        }

        stage('Build Production Multi Arch Docker Manifest') {
            when {
                branch 'master'
                not {
                    triggeredBy "TimerTrigger"
                }
            }

            environment {
                VERSION = sh returnStdout: true, script: "mvn -B help:evaluate '-Dexpression=project.version' | grep -v '\\[' | tr -d '\\n'"
            }

            steps {
                buildMultiArchManifest(env.VERSION)
            }
        }

        stage('Trigger k8s deployment') {
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
                build wait: false, job: '../Helm/funnel', parameters: [string(name: 'VERSION', value: env.VERSION)]
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

def buildDockerImage(String fromDirectory, String tag) {
    withEnv(['IMAGE_TAG='+tag]) {
        withCredentials([usernamePassword(credentialsId: '750504ce-6f4f-4252-9b2b-5814bd561430', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            sh 'docker login --username "$USERNAME" --password "$PASSWORD"'
            configFileProvider([configFile(fileId: 'b958fc4b-b1bd-4233-8692-c4a26a51c0f4', variable: 'MAVEN_SETTINGS_XML')]) {
                dir(fromDirectory) {
                    sh '''mvn -B \
                        -s "${MAVEN_SETTINGS_XML}" \\
                        clean \\
                        package \\
                        -DskipTests \\
                        -Dquarkus.container-image.build=true \\
                        -Dquarkus.container-image.tag="${IMAGE_TAG}" \\
                        -Dquarkus.container-image.group=rafaelostertag \\
                        -Dquarkus.container-image.push=true \\
                        -Dquarkus.container-image.username="${USERNAME}" \\
                        -Dquarkus.container-image.password="${PASSWORD}"
                        '''
                }
            }
        }
    }
}

def buildMultiArchManifest(String tag) {
    withEnv(['IMAGE_TAG='+tag]) {
        withCredentials([usernamePassword(credentialsId: '750504ce-6f4f-4252-9b2b-5814bd561430', passwordVariable: 'PASSWORD', usernameVariable: 'USERNAME')]) {
            sh 'docker login --username "$USERNAME" --password "$PASSWORD"'
            sh 'docker manifest create "rafaelostertag/funnel-notifier:${IMAGE_TAG}" --amend "rafaelostertag/funnel-notifier:${IMAGE_TAG}-amd64" --amend "rafaelostertag/funnel-notifier:${IMAGE_TAG}-arm64"'
            sh 'docker manifest create "rafaelostertag/funnel-rest:${IMAGE_TAG}" --amend "rafaelostertag/funnel-rest:${IMAGE_TAG}-amd64" --amend "rafaelostertag/funnel-rest:${IMAGE_TAG}-arm64"'
            sh 'docker manifest create "rafaelostertag/funnel-xml-retriever:${IMAGE_TAG}" --amend "rafaelostertag/funnel-xml-retriever:${IMAGE_TAG}-amd64" --amend "rafaelostertag/funnel-xml-retriever:${IMAGE_TAG}-arm64"'
            sh 'docker manifest push --purge "rafaelostertag/funnel-notifier:${IMAGE_TAG}"'
            sh 'docker manifest push --purge "rafaelostertag/funnel-rest:${IMAGE_TAG}"'
            sh 'docker manifest push --purge "rafaelostertag/funnel-xml-retriever:${IMAGE_TAG}"'
        }
    }
}
