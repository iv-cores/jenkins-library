package org.ivcode.jenkins.core

static def call (script, @DelegatesTo(value = JenkinsPropertiesBuilder, strategy = Closure.DELEGATE_FIRST) Closure closure) {
    def builder = new JenkinsPropertiesBuilder()
    closure.delegate = builder
    closure.resolveStrategy = Closure.DELEGATE_FIRST
    closure()

    register(this, builder)
}

static def register(script, JenkinsPropertiesBuilder builder) {
    def properties = []

    builder.build().each { property ->
        if(property.type == JenkinsPropertiesType.BOOLEAN) {
            properties.add(script.booleanParam(name: property.name, defaultValue: property.defaultValue, description: property.description))
        } else if(property.type == JenkinsPropertiesType.STRING) {
            properties.add(script.string(name: property.name, defaultValue: property.defaultValue, description: property.description))
        }
    }

    script.properties([script.parameters(properties)])
}
