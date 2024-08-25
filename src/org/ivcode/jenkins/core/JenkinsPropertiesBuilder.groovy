package org.ivcode.jenkins.core

class JenkinsPropertiesBuilder {

    private List<JenkinsProperty> properties = new ArrayList<>()

    def with(JenkinsPropertiesType type, String name, String defaultValue, String description) {
        properties.add(new JenkinsProperty(type, name, defaultValue, description))
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

    List<JenkinsProperty> build() {
        return properties
    }
}

enum JenkinsPropertiesType {
    BOOLEAN,
    STRING
}

class JenkinsProperty {
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

