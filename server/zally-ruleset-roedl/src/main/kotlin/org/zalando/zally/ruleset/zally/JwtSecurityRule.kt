package org.zalando.zally.ruleset.roedl

import io.swagger.v3.oas.models.security.SecurityScheme
import org.zalando.zally.rule.api.Check
import org.zalando.zally.rule.api.Context
import org.zalando.zally.rule.api.Rule
import org.zalando.zally.rule.api.Severity
import org.zalando.zally.rule.api.Violation

@Rule(
    ruleSet = RoedlRuleSet::class,
    id = "JWT_SECURITY_CHECK",
    severity = Severity.MUST,
    title = "Check JWT Security in API Spec"
)
class JwtSecurityRule {

    @Check(severity = Severity.MUST)
    fun validate(context: Context): List<Violation> {
        val violations = mutableListOf<Violation>()

        context.api.components?.securitySchemes?.forEach { (key, scheme) ->
            if (scheme.type == SecurityScheme.Type.HTTP && scheme.scheme == "bearer" && scheme.bearerFormat == "JWT") {
                context.api.paths?.forEach { (path, pathItem) ->
                    pathItem.readOperations().forEach { operation ->
                        if (operation.security?.any { it.keys.contains(key) } != true) {
                            violations.add(
                                context.violation("JWT Security not applied to $path operation ${operation.operationId}", operation)
                            )
                        }
                    }
                }
            }
        }

        return violations
    }
}
