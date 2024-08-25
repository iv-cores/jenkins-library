package org.ivcode.jenkins.core

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

class JenkinsStages {
    private final def node;

    JenkinsStages(node) {
        this.node = node
    }

    void create(String name, Closure closure) {
        node.stage(name, closure)
    }

    void create(String name, Boolean condition, Closure closure) {
        if(!condition) {
            Utils.markStageSkippedForConditional(name)
            return
        }

        node.stage(name, closure)
    }
}
