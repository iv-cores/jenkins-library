package org.ivcode.jenkins.core

import org.jenkinsci.plugins.pipeline.modeldefinition.Utils

class JenkinsStages {
    private final def node;

    JenkinsStages(node) {
        this.node = node
    }

    void apply(@DelegatesTo(value = JenkinsStages, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        closure.delegate = this
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()
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
