package org.ivcode.jenkins.utils

static Boolean isPrimary() {
    return "${env.BRANCH_IS_PRIMARY}".equalsIgnoreCase("true")
}