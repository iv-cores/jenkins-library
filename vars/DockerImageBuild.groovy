import org.ivcode.jenkins.models.DockerImageInfo

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

import static org.ivcode.jenkins.utils.ScmUtils.isPrimary
import static org.ivcode.jenkins.core.JenkinsProperties.call as JenkinsProperties

def call(
    Map<String, Object> options = [:]
) {
    node {
        checkout scm

        def info = DockerImageInfo.fromOptions(options)
        def isPrimary = isPrimary(this)

        JenkinsProperties {
            withBoolean(
                name: 'publish docker',
                defaultValue: isPrimary,
                description: 'publish to the docker repository'
            )

            withString(
                name: 'publish tags',
                defaultValue: 'latest, stable',
                description: 'comma separated list of tags to publish'
            )
        }

        def isPublish = params['publish docker'] ?: isPrimary
        def tags = splitTags(params['publish tags'] as String) ?: info.tags

        def image = null;
        stage('Build Docker Image') {
            image = docker.build("${info.name}:latest", "--file ${info.file} ${info.path}")
        }

        stage('Publish Docker Image') {
            if(!isPublish) {
                Utils.markStageSkippedForConditional(STAGE_NAME)
                return
            }

            docker.withRegistry(env.DOCKER_URI_SNAPSHOT, 'docker-snapshot') {
                tags.each { tag ->
                    image.push(tag)
                }
            }
        }
    }

}

/**
 * Splits a comma separated string of tags into a list of strings.
 *
 * @param tags a comma separated string of tags
 * @return a list of tags or null if the input is null or empty
 */
private static def splitTags(String tags) {
    if(tags == null || tags.trim().isEmpty()) {
        return null
    }
    return tags.split(',').collect { it.trim() }
}
