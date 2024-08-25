import org.ivcode.jenkins.models.DockerImageInfo

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

import static org.ivcode.jenkins.utils.ScmUtils.isPrimary
import org.ivcode.jenkins.core.JenkinsProperties

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
