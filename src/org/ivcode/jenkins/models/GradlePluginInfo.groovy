package org.ivcode.jenkins.models

import static org.ivcode.jenkins.utils.ModelUtils.notNull

class GradlePluginInfo {
    /* Docker image to use for building the plugin */
    public String buildImage = 'registry.ivcode.org/corretto-ubuntu:21-jammy'

    static GradlePluginInfo fromOptions(Map<String, Object> options) {
        def gradlePluginInfo = new GradlePluginInfo()

        gradlePluginInfo.buildImage = notNull(options['buildImage'] ?: gradlePluginInfo.buildImage, "buildImage is required")

        return gradlePluginInfo
    }
}
