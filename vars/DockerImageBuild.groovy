import org.ivcode.jenkins.core.JenkinsStages
import org.ivcode.jenkins.models.DockerImageInfo
import org.ivcode.jenkins.core.JenkinsProperties

import static org.ivcode.jenkins.utils.ScmUtils.isPrimary

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
        def isPrimary = isPrimary(this)

        def properties = JenkinsProperties.create(this) {
            withBoolean(
                name: 'publish docker',
                defaultValue: isPrimary,
                description: 'publish to the docker repository'
            )

            withStringArray(
                name: 'publish tags',
                defaultValue: info.tags,
                description: 'comma separated list of tags to publish'
            )
        }

        def isPublish = properties.getBoolean('publish docker')
        def tags = properties.getStringArray('publish tags')


        new JenkinsStages(this).apply {
            def image = null;

            create('Build Docker Image') {
                image = docker.build("${info.name}:latest", "--file ${info.file} ${info.path}")
            }

            create('Publish Docker Image', isPublish) {
                docker.withRegistry(env.DOCKER_URI_SNAPSHOT, 'docker-snapshot') {
                    tags.each { tag ->
                        image.push(tag)
                    }
                }
            }
        }
    }

}
