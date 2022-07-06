package com.larsreimann.safeds.constant

import com.larsreimann.safeds.safeDS.SdsSchemaEffectReference
import com.larsreimann.safeds.utils.ExperimentalSdsApi

/**
 * The possible [SdsSchemaEffect].
 */
@ExperimentalSdsApi
enum class SdsSchemaEffect(val effect: String?) {
    NoSchemaEffect(null),
    ReadSchemaEffect("\$ReadSchema"),
    CheckColumnEffect("\$CheckColumn");

    override fun toString(): String {
        return effect ?: "NoSchemaEffect"
    }
}

/**
 * Returns the [SdsSchemaEffect] of this [SdsSchemaEffectReference].
 *
 * @throws IllegalArgumentException If the Schema Effect is unknown.
 */
@ExperimentalSdsApi
fun SdsSchemaEffectReference.effect(): SdsSchemaEffect {
    return SdsSchemaEffect.values().firstOrNull { it.effect == this.effect }
        ?: throw IllegalArgumentException("Unknown schema effect '$effect'.")
}