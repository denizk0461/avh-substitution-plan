package com.denizd.substitutionplan.data

internal enum class Topic(val tag: String) {
    ANDROID("substitutions-android"),
    BROADCAST("substitutions-broadcast"),
    IOS("substitutions-ios"),
    DEVELOPMENT("substitutions-debug")
}
