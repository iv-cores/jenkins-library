import org.ivcode.jenkins.models.DockerImageInfo
import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

import static org.ivcode.jenkins.utils.ScmUtils.isPrimary

def call(
    Map<String, Object> options = [:]
) {
    def info = DockerImageInfo.fromOptions(options)

    properties([
        parameters([
                booleanParam(name: 'publish docker', defaultValue: isPrimary, description: 'publish to the docker repository'),
                string(name: 'publish tags', defaultValue: "${defaultTags.join(',')}", description: 'comma seperated list of tags to publish')
        ])
    ])

    node {
        checkout scm

        def isPrimary = isPrimary()
        def isPublish = params['publish docker'] ?: isPrimary
        def tags = splitTags(params['publish tags'] as String) ?: defaultTags

        def image = null;
        stage('Build Docker Image') {
            image = docker.build("${imageName}:latest", "--file ${file} ${path}")
        }

        stage('Publish Docker Image') {
            if(!isPublish) {
                Utils.markStageSkippedForConditional(STAGE_NAME)
            }

            docker.withRegistry(env.DOCKER_URI_SNAPSHOT, 'docker-snapshot') {
                tags.each { tag ->
                    image.push(tag)
                }
            }
        }
    }

}

private static def splitTags(String tags) {
    if(tags == null || tags.trim().isEmpty()) {
        return null
    }
    return tags.split(',').collect { it.trim() }
}
