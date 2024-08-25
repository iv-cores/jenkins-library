package org.ivcode.jenkins.utils

static <T> T notNull(T obj, String message = "object is null") {
    if (obj == null) {
        throw new IllegalArgumentException(message)
    }
    return obj
}