package org.ivcode.jenkins.core

import jenkins.model.Jenkins


class JenkinsProperties {

    Map<String, JenkinsProperty> buildProperties = [:]

    def static create(Jenkins node, @DelegatesTo(value = Builder, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        def builder = new Builder(node)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        return new JenkinsProperties(node, builder)
    }

    private JenkinsProperties(Jenkins node, Builder builder) {
        def properties = []

        builder.build().each { property ->
            if(property.type == JenkinsPropertiesType.BOOLEAN) {
                properties.add(node.booleanParam(name: property.name, defaultValue: property.defaultValue, description: property.description))
            } else if(property.type == JenkinsPropertiesType.STRING) {
                properties.add(node.string(name: property.name, defaultValue: property.defaultValue, description: property.description))
            }
        }

        node.properties([node.parameters(properties)])
    }

    Boolean getBoolean(String name) {
        return buildProperties[name]?.defaultValue
    }

    String get(String name) {
        return buildProperties[name]?.defaultValue
    }

    static class Builder {
        private final Jenkins node
        private final List<JenkinsProperty> buildProperties = new ArrayList<>()

        Builder(Jenkins node) {
            this.node = node
        }

        def with(JenkinsPropertiesType type, String name, String defaultValue, String description) {
            this.buildProperties.add(new JenkinsProperty(type, name, defaultValue, description))
            return this
        }

        def withBoolean(String name, String defaultValue, String description) {
            return with(JenkinsPropertiesType.BOOLEAN, name, defaultValue, description)
        }

        def withBoolean(Map params) {
            return with(JenkinsPropertiesType.BOOLEAN, params.name as String, params.defaultValue as String, params.description as String)
        }

        def withString(String name, String defaultValue, String description) {
            return with(JenkinsPropertiesType.STRING, name, defaultValue, description)
        }

        def withString(Map params) {
            return with(JenkinsPropertiesType.STRING, params.name as String, params.defaultValue as String, params.description as String)
        }

        JenkinsProperty build() {
            return new JenkinsProperty(node, this)
        }
    }

    static enum JenkinsPropertiesType {
        BOOLEAN,
        STRING
    }

    static class JenkinsProperty {
        JenkinsPropertiesType type
        String name
        String defaultValue
        String description

        JenkinsProperty(JenkinsPropertiesType type, String name, String defaultValue, String description) {
            this.type = type
            this.name = name
            this.defaultValue = defaultValue
            this.description = description
        }
    }
}
