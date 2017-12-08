package de.zalando.zally.rule.zalando

import com.typesafe.config.Config
import de.zalando.zally.dto.ViolationType
import de.zalando.zally.rule.Violation
import de.zalando.zally.util.PatternUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PascalCaseHttpHeadersRule(@Autowired ruleSet: ZalandoRuleSet, @Autowired rulesConfig: Config) : HttpHeadersRule(ruleSet, rulesConfig) {
    override val title = "Prefer Hyphenated-Pascal-Case for HTTP header fields"
    override val violationType = ViolationType.SHOULD
    override val id = "132"

    override fun isViolation(header: String) = !PatternUtil.isHyphenatedPascalCase(header)

    override fun createViolation(paths: List<String>): Violation {
        return Violation(this, title, "Header is not Hyphenated-Pascal-Case", violationType, paths)
    }
}
