package org.ivcode.jenkins.core

import hudson.model.ParametersDefinitionProperty
import hudson.model.BooleanParameterDefinition
import hudson.model.StringParameterDefinition
import jenkins.model.Jenkins

class JenkinsProperties {

    private final def node
    private final Map<String, JenkinsProperty> buildProperties

    static JenkinsProperties create(node, @DelegatesTo(value = Builder, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        def builder = new Builder(node)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        return builder.build()
    }

    private JenkinsProperties(Jenkins node, Map<String, JenkinsProperty> buildProperties) {
        this.node = node
        this.buildProperties = new HashMap<>(buildProperties)

        def properties = []
        this.buildProperties.each { name, property ->
            if (property.type == JenkinsPropertiesType.BOOLEAN) {
                properties.add(new BooleanParameterDefinition(property.name, property.defaultValue.toBoolean(), property.description))
            } else if (property.type == JenkinsPropertiesType.STRING) {
                properties.add(new StringParameterDefinition(property.name, property.defaultValue, property.description))
            }
        }

        Jenkins.get().getGlobalNodeProperties().add(new ParametersDefinitionProperty(properties))
    }

    Boolean getBoolean(String name) {
        return get(name)?.toBoolean()
    }

    String get(String name) {
        return node.params[name] ?: buildProperties[name]?.defaultValue
    }

    static class Builder {
        private final def node
        private final Map<String, JenkinsProperty> buildProperties = [:]

        Builder(node) {
            this.node = node
        }

        def with(JenkinsPropertiesType type, String name, String defaultValue, String description) {
            this.buildProperties[name] = new JenkinsProperty(type, name, defaultValue, description)
            return this
        }

        def withBoolean(String name, Boolean defaultValue, String description) {
            return with(JenkinsPropertiesType.BOOLEAN, name, defaultValue?.toString(), description)
        }

        def withBoolean(Map params) {
            return withBoolean(params.name as String, params.defaultValue as Boolean, params.description as String)
        }

        def withString(String name, String defaultValue, String description) {
            return with(JenkinsPropertiesType.STRING, name, defaultValue, description)
        }

        def withString(Map params) {
            return withString(params.name as String, params.defaultValue as String, params.description as String)
        }

        JenkinsProperties build() {
            return new JenkinsProperties(node, buildProperties)
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
