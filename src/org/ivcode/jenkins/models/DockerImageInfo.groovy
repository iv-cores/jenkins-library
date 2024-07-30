package org.ivcode.jenkins.models

class DockerImageInfo {

    public String name
    public List<String> tags
    public String file
    public String path

    static DockerImageInfo fromOptions(Map<String, Object> options) {

        def dockerImageInfo = new DockerImageInfo()

        dockerImageInfo.name = options.require('name')
        dockerImageInfo.tags = options.require('tags')
        dockerImageInfo.tags = options['file'] ?: "./Dockerfile"
        dockerImageInfo.path = options['path'] ?: "."

        return dockerImageInfo
    }
}