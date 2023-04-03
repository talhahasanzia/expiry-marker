package io.github.talhahasanzia.expiry.marker

import io.github.talhahasanzia.expiry.annotation.Expiry

class FeatureFlag {

    @Expiry("02-03-2022")
    val featureFlaggingEnabled = true

    @Expiry("02-04-2023")
    fun isFastEnabled() = true

    @Expiry("02-05-2023")
    fun isSlowEnabled() = true
}