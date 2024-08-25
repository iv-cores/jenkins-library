package org.ivcode.jenkins.core

/**
 * Class representing Jenkins properties.
 */
class JenkinsProperties {

    private final def node
    private final Map<String, JenkinsProperty> buildProperties

    /**
     * Creates a new instance of JenkinsProperties.
     *
     * @param node The Jenkins node.
     * @param closure The closure to configure the properties.
     * @return A new instance of JenkinsProperties.
     */
    static JenkinsProperties create(node, @DelegatesTo(value = Builder, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        def builder = new Builder(node)
        closure.delegate = builder
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure()

        return builder.build()
    }

    /**
     * Private constructor for JenkinsProperties.
     *
     * @param node The Jenkins node.
     * @param buildProperties The map of build properties.
     */
    private JenkinsProperties(node, Map<String, JenkinsProperty> buildProperties) {
        this.node = node
        this.buildProperties = new HashMap<>(buildProperties)

        def properties = []
        this.buildProperties.each { name, property ->
            if(property.type == JenkinsPropertiesType.BOOLEAN) {
                properties.add(node.booleanParam(name: property.name, defaultValue: property.defaultValue, description: property.description))
            } else if(property.type == JenkinsPropertiesType.STRING) {
                properties.add(node.string(name: property.name, defaultValue: property.defaultValue, description: property.description))
            } else {
                throw new IllegalArgumentException("unknown property type: ${property.type}")
            }
        }

        node.properties([node.parameters(properties)])
    }

    /**
     * Gets a boolean property by name.
     *
     * @param name The name of the property.
     * @return The boolean value of the property.
     */
    Boolean getBoolean(String name) {
        return get(name)?.toBoolean()
    }

    /**
     * Gets a property by name.
     *
     * @param name The name of the property.
     * @return The value of the property.
     */
    String get(String name) {
        return node.params[name] ?: buildProperties[name]?.defaultValue
    }

    /**
     * Gets a string array property by name.
     *
     * @param name The name of the property.
     * @return The list of string values of the property.
     */
    List<String> getStringArray(String name) {
        return get(name)?.split(',')?.collect { it.trim() }
    }

    /**
     * Builder class for JenkinsProperties.
     */
    static class Builder {
        private final def node
        private final Map<String, JenkinsProperty> buildProperties = [:]

        /**
         * Constructor for Builder.
         *
         * @param node The Jenkins node.
         */
        Builder(node) {
            this.node = node
        }

        /**
         * Adds a property to the builder.
         *
         * @param type The type of the property.
         * @param name The name of the property.
         * @param defaultValue The default value of the property.
         * @param description The description of the property.
         * @return The builder instance.
         */
        def with(JenkinsPropertiesType type, String name, String defaultValue, String description) {
            this.buildProperties[name] = new JenkinsProperty(type, name, defaultValue, description)
            return this
        }

        /**
         * Adds a boolean property to the builder.
         *
         * @param name The name of the property.
         * @param defaultValue The default value of the property.
         * @param description The description of the property.
         * @return The builder instance.
         */
        def withBoolean(String name, Boolean defaultValue, String description) {
            return with(JenkinsPropertiesType.BOOLEAN, name, defaultValue?.toString(), description)
        }

        /**
         * Adds a boolean property to the builder using a map.
         *
         * @param params The map of parameters.
         * @return The builder instance.
         */
        def withBoolean(Map params) {
            return withBoolean(params.name as String, params.defaultValue as Boolean, params.description as String)
        }

        /**
         * Adds a string property to the builder.
         *
         * @param name The name of the property.
         * @param defaultValue The default value of the property.
         * @param description The description of the property.
         * @return The builder instance.
         */
        def withString(String name, String defaultValue, String description) {
            return with(JenkinsPropertiesType.STRING, name, defaultValue, description)
        }

        /**
         * Adds a string property to the builder using a map.
         *
         * @param params The map of parameters.
         * @return The builder instance.
         */
        def withString(Map params) {
            return withString(params.name as String, params.defaultValue as String, params.description as String)
        }

        /**
         * Adds a string array property to the builder.
         *
         * @param name The name of the property.
         * @param defaultValue The default value of the property.
         * @param description The description of the property.
         * @return The builder instance.
         */
        def withStringArray(String name, List<String> defaultValue, String description) {
            return with(JenkinsPropertiesType.STRING, name, defaultValue.join(', '), description)
        }

        /**
         * Adds a string array property to the builder using a map.
         *
         * @param params The map of parameters.
         * @return The builder instance.
         */
        def withStringArray(Map params) {
            return withStringArray(params.name as String, params.defaultValue as List<String>, params.description as String)
        }

        /**
         * Builds the JenkinsProperties instance.
         *
         * @return A new instance of JenkinsProperties.
         */
        JenkinsProperties build() {
            return new JenkinsProperties(node, buildProperties)
        }
    }

    /**
     * Enum representing the types of Jenkins properties.
     */
    static enum JenkinsPropertiesType {
        BOOLEAN,
        STRING
    }

    /**
     * Class representing a Jenkins property.
     */
    static class JenkinsProperty {
        JenkinsPropertiesType type
        String name
        String defaultValue
        String description

        /**
         * Constructor for JenkinsProperty.
         *
         * @param type The type of the property.
         * @param name The name of the property.
         * @param defaultValue The default value of the property.
         * @param description The description of the property.
         */
        JenkinsProperty(JenkinsPropertiesType type, String name, String defaultValue, String description) {
            this.type = type
            this.name = name
            this.defaultValue = defaultValue
            this.description = description
        }
    }
}