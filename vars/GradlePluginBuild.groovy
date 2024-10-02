import org.ivcode.jenkins.core.JenkinsProperties
import org.ivcode.jenkins.core.JenkinsStages
import org.ivcode.jenkins.models.GradlePluginInfo

import static org.ivcode.jenkins.utils.ScmUtils.isPrimary
import static org.ivcode.jenkins.utils.ModelUtils.notNull

def call(
    Map<String, Object> options = [:]
) {
    node {
        checkout scm

        // Build info from the given options
        def info = GradlePluginInfo.fromOptions(options)

        // Gradle info from the gradle build
        Map<String, Object> gradleInfo = [:]

        // Create the Jenkins Properties
        def properties = JenkinsProperties.create(this) {
            withBoolean(
                name: 'sonar',
                defaultValue: isPrimary(this),
                description: 'run sonarqube analysis'
            )
            withBoolean(
                name: 'publish maven',
                defaultValue: isPrimary(this),
                description: 'publish to the maven repository'
            )
        }

        // Create the Jenkins Stages
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

                def isSonar = notNull properties.getBoolean('sonar')
                create("SonarQube", isSonar) {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_LOGIN')]) {
                        sh './gradlew sonar ' +
                                '-D"sonar.scm.revision=$GIT_COMMIT" ' +
                                '-D"sonar.scm.url=$GIT_URL" '
                    }
                }

                def isPublish = notNull properties.getBoolean('publish maven')
                create("Publish Maven", isPublish) {
                    // publish to maven
                    withEnv(["MVN_URL=${MVN_URI_SNAPSHOT}"]) {
                        withCredentials([usernamePassword(credentialsId: 'mvn-snapshot', usernameVariable: 'MVN_USERNAME', passwordVariable: 'MVN_PASSWORD')]) {
                            sh './gradlew publish'
                        }
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