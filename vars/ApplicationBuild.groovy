import org.ivcode.jenkins.core.JenkinsProperties
import org.ivcode.jenkins.core.JenkinsStages
import org.ivcode.jenkins.models.ApplicationInfo

import static org.ivcode.jenkins.utils.ScmUtils.isPrimary

def call(
    Map<String, Object> options = [:]
) {
    node {
        checkout scm

        // Application info from the given options
        def info = ApplicationInfo.fromOptions(options)

        // Gradle info from the gradle build
        Map<String, Object> gradleInfo = [:]

        // Properties defined for the jenkins job
        def properties = JenkinsProperties.create(this) {

            // If maven push is enabled, we give the option to run it within the build
            // For a maven push to happen, maven publishing must be enabled for the application and turned on for the build
            if(info.publishMaven) {
                withBoolean(
                    name: 'publish maven',
                    defaultValue: isPrimary(this),
                    description: 'publish to the maven repository'
                )
            }

            // If docker push is enabled, we give the option to run it within the build
            // For a docker push to happen, docker publishing must be enabled for the application and turned on for the build
            if(info.publishDocker) {
                withBoolean(
                    name: 'publish docker',
                    defaultValue: isPrimary(this),
                    description: 'publish to the docker repository'
                )
            }
        }

        // Jenkins Stages
        new JenkinsStages(this).apply {
            def buildImg = docker.image(info.buildImage)

            buildImg.inside {
                create('Prepare') {
                    // start the daemon
                    sh './gradlew info --daemon'

                    // load the properties
                    gradleInfo = readProperties file: 'build/properties/build.properties'

                    echo "Project Name: ${gradleInfo.name}"
                    echo "Project Version: ${gradleInfo.version}"
                }

                create('Build') {
                    sh './gradlew clean build'
                }

                def isPublishMavenEnabled = info.publishMaven
                if(isPublishMavenEnabled) {
                    // If maven publish isn't enable for this application, skip maven entirely

                    def isPublishMaven = properties.getBoolean('publish maven')
                    create("Publish Maven", isPublishMaven) {
                        // publish to maven
                        withEnv(["MVN_URL=${MVN_URI_SNAPSHOT}"]) {
                            withCredentials([usernamePassword(credentialsId: 'mvn-snapshot', usernameVariable: 'MVN_USERNAME', passwordVariable: 'MVN_PASSWORD')]) {
                                sh './gradlew publish'
                            }
                        }

                        if(!isSnapshot(gradleInf.version as String)) {
                            throw new UnsupportedOperationException("release process not yet defined")
                        }
                    }
                }
            }

            def isPublishDockerEnabled = info.publishDocker
            if(isPublishDockerEnabled) {
                // If docker publish isn't enable for this application, skip the entire docker build process

                def dockerBuildImage = null
                create("Build Docker") {
                    dockerBuildImage = docker.build(gradleInfo.name, "--file ${info.dockerFile} ${info.dockerPath}")
                }

                def isPublishDocker = properties.getBoolean('publish docker')
                create("Publish Docker", isPublishDocker) {
                    docker.withRegistry(env.DOCKER_URI_SNAPSHOT, 'docker-snapshot') {
                        dockerBuildImage.push(gradleInfo.version as String)
                        dockerBuildImage.push("latest")
                    }

                    if(!isSnapshot(gradleInfo.version as String)) {
                        throw new UnsupportedOperationException("release process not yet defined")
                    }
                }
            }
        }
    }
}

static def isSnapshot(String version) {
    return version.endsWith("-SNAPSHOT")
}