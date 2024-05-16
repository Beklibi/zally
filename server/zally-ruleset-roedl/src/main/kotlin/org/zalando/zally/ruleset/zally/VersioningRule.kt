package org.zalando.zally.ruleset.roedl

import org.zalando.zally.rule.api.Check
import org.zalando.zally.rule.api.Context
import org.zalando.zally.rule.api.Rule
import org.zalando.zally.rule.api.Severity
import org.zalando.zally.rule.api.Violation

@Rule(
    ruleSet = RoedlRuleSet::class,
    id = "ENSURE_VERSIONING",
    severity = Severity.MUST,
    title = "Ensure version parameter is documented"
)
class VersioningRule {

    @Check(severity = Severity.MUST)
    fun validate(context: Context): List<Violation> {
        val violations = mutableListOf<Violation>()

        // Überprüfe, ob paths vorhanden sind, und iteriere durch jeden Pfad
        context.api.paths?.forEach { (path, pathItem) ->
            pathItem?.readOperations()?.forEach { operation ->
                // Stelle sicher, dass parameters nicht null ist, bevor darauf zugegriffen wird
                val versionParam = operation.parameters?.find {
                    it.name.equals("version", ignoreCase = true) && it.`in` == "query"
                }
                if (versionParam == null) {
                    violations.add(
                        context.violation("Version parameter is missing in operation ${operation.operationId} at $path", operation)
                    )
                }

                // Überprüfe, ob eine 400 Bad Request Antwort vorhanden ist
                if (operation.responses?.entries?.any { (statusCode, _) -> statusCode == "400" } != true) {
                    violations.add(
                        context.violation("Missing 400 Bad Request response for missing or invalid version parameter in operation ${operation.operationId}", operation)
                    )
                }
            }
        }

        return violations
    }
}
