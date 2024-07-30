package org.ivcode.jenkins.utils

static Boolean isPrimary(script) {
    return "${script.env.BRANCH_IS_PRIMARY}".equalsIgnoreCase("true")
}