package org.ivcode.jenkins.models

import static org.ivcode.jenkins.utils.ModelUtils.notNull

/**
 * Class representing Docker image information.
 */
class DockerImageInfo {

    public String name
    public List<String> tags
    public String file
    public String path

    /**
     * Creates a DockerImageInfo instance from a map of options.
     *
     * @param options A map containing the options for the Docker image.
     * @return A DockerImageInfo instance populated with the provided options.
     * @throws IllegalArgumentException if the required options are not provided.
     */
    static DockerImageInfo fromOptions(Map<String, Object> options) {

        def dockerImageInfo = new DockerImageInfo()

        dockerImageInfo.name = notNull(options['name'], "docker image name is required")
        dockerImageInfo.tags = notNull(options['tags'] as List<String>, "docker image tags are required")
        dockerImageInfo.file = options['file'] ?: "./Dockerfile"
        dockerImageInfo.path = options['path'] ?: "."

        return dockerImageInfo
    }
}