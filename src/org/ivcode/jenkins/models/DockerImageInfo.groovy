package org.ivcode.jenkins.models

import static org.ivcode.jenkins.utils.ModelUtils.notNull

class DockerImageInfo {

    public String name
    public List<String> tags
    public String file
    public String path

    static DockerImageInfo fromOptions(Map<String, Object> options) {

        def dockerImageInfo = new DockerImageInfo()

        dockerImageInfo.name = notNull(options['name'], "docker image name is required")
        dockerImageInfo.tags = notNull(options['tags'], "docker image tags are required")
        dockerImageInfo.file = options['file'] ?: "./Dockerfile"
        dockerImageInfo.path = options['path'] ?: "."

        return dockerImageInfo
    }
}