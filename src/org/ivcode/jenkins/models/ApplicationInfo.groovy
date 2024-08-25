package org.ivcode.jenkins.models

import static org.ivcode.jenkins.utils.ModelUtils.notNull

class ApplicationInfo {

    /* Docker image to use for building the application */
    public String buildImage = 'registry.ivcode.org/corretto-ubuntu:21-jammy'

    /* enables publishing the application to Maven */
    public Boolean publishMaven = false

    /* enables publishing the application to Docker */
    public Boolean publishDocker = false
    /* path to the Dockerfile relative to the project root */
    public String dockerFile
    /* Docker context relative to the project root */
    public String dockerPath = '.'

    static ApplicationInfo fromOptions(Map<String, Object> options) {
        def applicationInfo = new ApplicationInfo()

        applicationInfo.buildImage = notNull(options['buildImage'] ?: applicationInfo.buildImage, "buildImage is required")
        applicationInfo.publishMaven = notNull(['publishMaven'] ?: applicationInfo.publishMaven, "publishMaven is required")
        applicationInfo.publishDocker = notNull(['publishDocker'] ?: applicationInfo.publishDocker, "publishDocker is required")
        applicationInfo.dockerFile = options['dockerFile'] ?: applicationInfo.dockerFile
        applicationInfo.dockerPath = options['dockerPath'] ?: applicationInfo.dockerPath

        if(applicationInfo.publishDocker && applicationInfo.dockerFile==null) {
            throw new IllegalArgumentException("dockerFile is required when publishDocker is enabled")
        }
        if(applicationInfo.publishDocker && applicationInfo.dockerPath==null) {
            throw new IllegalArgumentException("dockerPath is required when publishDocker is enabled")
        }

        return applicationInfo
    }
}
