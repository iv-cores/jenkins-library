package org.ivcode.jenkins.models

class DockerImageInfo {
    public String name
    public List<String> tags

    DockerImageInfo(String name, List<String> tags) {
        this.name = name
        this.tags = tags
    }

    static DockerImageInfo fromOptions(Map<String, Object> options) {
        return new DockerImageInfo(
            options['name'] as String,
            options['tags'] as List<String>
        )
    }
}