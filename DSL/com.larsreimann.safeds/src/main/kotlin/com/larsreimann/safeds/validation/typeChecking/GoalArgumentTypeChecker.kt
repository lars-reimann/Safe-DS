package com.larsreimann.safeds.validation.typeChecking

import com.larsreimann.safeds.safeDS.SafeDSPackage.Literals
import com.larsreimann.safeds.safeDS.SdsGoalArgument
import com.larsreimann.safeds.staticAnalysis.linking.parameterOrNull
import com.larsreimann.safeds.staticAnalysis.typing.UnresolvedType
import com.larsreimann.safeds.staticAnalysis.typing.isSubstitutableFor
import com.larsreimann.safeds.staticAnalysis.typing.type
import com.larsreimann.safeds.validation.AbstractSafeDSChecker
import com.larsreimann.safeds.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check
import org.eclipse.xtext.validation.CheckType

class GoalArgumentTypeChecker : AbstractSafeDSChecker() {

    @Check(CheckType.NORMAL)
    fun value(sdsGoalArgument: SdsGoalArgument) {
        val argumentType = sdsGoalArgument.type()
        if (argumentType is UnresolvedType) {
            return // Scoping error already shown
        }

        val parameterType = (sdsGoalArgument.parameterOrNull() ?: return).type()

        if (!argumentType.isSubstitutableFor(parameterType)) {
            var argumentTypeString = argumentType.toSimpleString()
            var parameterTypeString = parameterType.toSimpleString()

            if (argumentTypeString == parameterTypeString) {
                argumentTypeString = argumentType.toString()
                parameterTypeString = parameterType.toString()
            }

            error(
                "An goal argument of type '$argumentTypeString' cannot be assigned to a parameter of type '$parameterTypeString'.",
                Literals.SDS_GOAL_ARGUMENT__VALUE,
                ErrorCode.WrongType,
            )
        }
    }
}
