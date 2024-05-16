package org.zalando.zally.ruleset.roedl

import io.swagger.v3.oas.models.Operation
import org.zalando.zally.rule.api.Check
import org.zalando.zally.rule.api.Context
import org.zalando.zally.rule.api.Rule
import org.zalando.zally.rule.api.Severity
import org.zalando.zally.rule.api.Violation

@Rule(
    ruleSet = RoedlRuleSet::class,
    id = "CORS_HEADERS_CHECK",
    severity = Severity.MUST,
    title = "Check CORS Headers in API Spec"
)
class CorsHeadersRule {

    @Check(severity = Severity.MUST)
    fun validate(context: Context): List<Violation> {
        val violations = mutableListOf<Violation>()

        // Verwende sicheren Zugriff auf paths und pathItem
        context.api.paths?.forEach { (_, pathItem) ->
            pathItem?.readOperations()?.forEach { operation ->
                checkCorsInfoInDescription(operation, context, violations)
            }
        }

        return violations
    }

    private fun checkCorsInfoInDescription(
        operation: Operation,
        context: Context,
        violations: MutableList<Violation>
    ) {
        // Sicherstellen, dass die Beschreibung nicht null ist und 'CORS' enthält
        if ((operation.description ?: "").contains("CORS").not()) {
            violations.add(
                context.violation("No CORS information in description", operation)
            )
        }
    }
}
