package de.zalando.zally.rule

import de.zalando.zally.rule.api.Rule
import de.zalando.zally.rule.zalando.InvalidApiSchemaRule

abstract class RulesValidator<RuleT>(val rules: List<RuleT>, val rulesPolicy: RulesPolicy, val invalidApiRule: InvalidApiSchemaRule) : ApiValidator where RuleT : Rule {

    final override fun validate(swaggerContent: String, ignoreRules: List<String>): List<Violation> {
        val ruleChecker = try {
            createRuleChecker(swaggerContent)
        } catch (e: Exception) {
            return listOf(invalidApiRule.getGeneralViolation())
        }
        return rules
                .filter { it.code !in ignoreRules }
                .filter { rulesPolicy.accepts(it) }
                .flatMap(ruleChecker)
                .sortedBy(Violation::violationType)
    }

    @Throws(java.lang.Exception::class)
    abstract fun createRuleChecker(swaggerContent: String): (RuleT) -> Iterable<Violation>
}
