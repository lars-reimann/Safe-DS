package com.larsreimann.safeds.staticAnalysis.linking

import de.unibonn.simpleml.emf.closestAncestorOrNull
import de.unibonn.simpleml.emf.isNamed
import de.unibonn.simpleml.emf.isResolved
import de.unibonn.simpleml.emf.typeParametersOrEmpty
import com.larsreimann.safeds.safeDS.SdsCall
import com.larsreimann.safeds.safeDS.SdsClass
import com.larsreimann.safeds.safeDS.SdsEnumVariant
import com.larsreimann.safeds.safeDS.SdsFunction
import com.larsreimann.safeds.safeDS.SdsNamedType
import com.larsreimann.safeds.safeDS.SdsTypeArgument
import com.larsreimann.safeds.safeDS.SdsTypeArgumentList
import com.larsreimann.safeds.safeDS.SdsTypeParameter
import de.unibonn.simpleml.staticAnalysis.callableOrNull

/**
 * Returns the [SmlTypeParameter] that corresponds to this [SmlTypeArgument] or `null` if it cannot be resolved.
 */
fun SmlTypeArgument.typeParameterOrNull(): SmlTypeParameter? {
    return when {
        this.isNamed() -> typeParameter
        else -> {
            val typeArgumentList = closestAncestorOrNull<SmlTypeArgumentList>() ?: return null

            // Cannot match positional type argument if it is preceded by named type arguments
            val firstNamedTypeArgumentIndex = typeArgumentList.typeArguments.indexOfFirst { it.isNamed() }
            val thisIndex = typeArgumentList.typeArguments.indexOf(this)
            if (firstNamedTypeArgumentIndex != -1 && thisIndex > firstNamedTypeArgumentIndex) {
                return null
            }

            typeArgumentList.typeParametersOrNull()?.getOrNull(thisIndex)
        }
    }
}

/**
 * Returns the list of [SmlTypeParameter]s that corresponds to this list of [SmlTypeArgument]s or `null` if it cannot
 * not be resolved.
 */
fun SmlTypeArgumentList.typeParametersOrNull(): List<SmlTypeParameter>? {
    return when (val parent = eContainer()) {
        is SmlCall -> {
            when (val callable = parent.callableOrNull()) {
                is SmlClass -> callable.typeParametersOrEmpty()
                is SmlEnumVariant -> callable.typeParametersOrEmpty()
                is SmlFunction -> callable.typeParametersOrEmpty()
                else -> null
            }
        }
        is SmlNamedType -> {
            val declaration = parent.declaration
            when {
                !declaration.isResolved() -> null
                declaration is SmlClass -> declaration.typeParametersOrEmpty()
                declaration is SmlEnumVariant -> declaration.typeParametersOrEmpty()
                else -> null
            }
        }
        else -> null
    }
}