package org.ivcode.jenkins.models

import static org.ivcode.jenkins.utils.ModelUtils.notNull

class DockerImageInfo {

    public String name
    public List<String> tags
    public String file
    public String path

    static DockerImageInfo fromOptions(Map<String, Object> options) {

        def dockerImageInfo = new DockerImageInfo()

        dockerImageInfo.name = notNull(options['name'] as String, "docker image name is required")
        dockerImageInfo.tags = notNull(options['tags'] as List<String>, "docker image tags are required")
        dockerImageInfo.tags = options['file'] as String ?: "./Dockerfile"
        dockerImageInfo.path = options['path'] as String ?: "."

        return dockerImageInfo
    }
}