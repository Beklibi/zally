package org.zalando.zally.ruleset.roedl

import org.zalando.zally.rule.api.Check
import org.zalando.zally.rule.api.Context
import org.zalando.zally.rule.api.Rule
import org.zalando.zally.rule.api.Severity
import org.zalando.zally.rule.api.Violation

@Rule(
    ruleSet = RoedlRuleSet::class,
    id = "ENSURE_ERROR_HANDLING",
    severity = Severity.MUST,
    title = "Ensure error responses are properly documented"
)
class EnsureErrorHandlingRule {

    // Korrekt spezifizierte Header
    private val requiredHeaders = listOf(
        "X-Flow-ID",
        "X-Tenant-ID",
        "X-Sales-Channel",
        "X-Frontend-Type"
    )

    @Check(severity = Severity.MUST)
    fun validate(context: Context): List<Violation> {
        val violations = mutableListOf<Violation>()

        context.api.paths?.forEach { (path, pathItem) ->
            pathItem?.readOperations()?.forEach { operation ->
                operation.responses?.entries?.forEach { (statusCode, response) ->
                    if (statusCode.startsWith("4") || statusCode.startsWith("5")) {
                        val missingHeaders = requiredHeaders.filterNot { header ->
                            response.headers?.containsKey(header) == true
                        }
                        if (missingHeaders.isNotEmpty()) {
                            violations.add(
                                context.violation(
                                    "Missing error details in the response for $statusCode at $path operation ${operation.operationId}: ${missingHeaders.joinToString()}",
                                    response
                                )
                            )
                        }
                    }
                }
            }
        }

        return violations
    }
}
