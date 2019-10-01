package com.denizd.substitutionplan.data

/**
 * Enum class for declaring Firebase Cloud Messaging topics without using the raw strings
 * throughout the project to avoid human error
 */
internal enum class Topic(val tag: String) {
    ANDROID("substitutions-android"),
    BROADCAST("substitutions-broadcast"),
    IOS("substitutions-ios"),
    DEVELOPMENT("substitutions-debug")
}
