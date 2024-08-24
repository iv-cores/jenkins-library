package org.ivcode.jenkins.utils

static <T> T notNull(Object obj, String message = "object is null") {
    if (obj == null) {
        throw new IllegalArgumentException(message)
    }
    return obj as T
}