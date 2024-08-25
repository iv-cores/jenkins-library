import org.ivcode.jenkins.core.JenkinsStages
import org.ivcode.jenkins.models.DockerImageInfo
import org.ivcode.jenkins.core.JenkinsProperties

import static org.ivcode.jenkins.utils.ScmUtils.isPrimary
import static org.ivcode.jenkins.utils.ModelUtils.notNull

/**
 * Builds and optionally publishes a Docker image based on the provided options.
 *
 * @param options A map containing the options for the Docker image build and publish.
 */
def call(
    Map<String, Object> options = [:]
) {
    node {
        checkout scm
        def info = DockerImageInfo.fromOptions(options)

        // Create the Jenkins Properties
        def properties = JenkinsProperties.create(this) {
            withBoolean(
                name: 'publish docker',
                defaultValue: isPrimary(this),
                description: 'publish to the docker repository'
            )

            withStringArray(
                name: 'publish tags',
                defaultValue: info.tags,
                description: 'comma separated list of tags to publish'
            )
        }


        // Create the Jenkins Stages
        new JenkinsStages(this).apply {
            def image

            create('Build Docker Image') {
                image = docker.build("${info.name}:latest", "--file ${info.file} ${info.path}")
            }

            def isPublish = notNull properties.getBoolean('publish docker')
            create('Publish Docker Image', isPublish) {
                docker.withRegistry(env.DOCKER_URI_SNAPSHOT, 'docker-snapshot') {
                    def tags = notNull properties.getStringArray('publish tags')
                    tags.each { image.push(it) }
                }
            }
        }
    }
}
