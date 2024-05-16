package org.zalando.zally.ruleset.roedl

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.zalando.zally.core.DefaultContextFactory
import org.zalando.zally.test.ZallyAssertions

class CorsHeadersRuleTest {

    private val rule = CorsHeadersRule()

    @Test
    fun `validate that operations with CORS information pass`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths:
              /items:
                get:
                  operationId: getItems
                  description: "Retrieve items. Supports CORS."
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context)).isEmpty()
    }

    @Test
    fun `validate that operations without CORS information are flagged`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths:
              /items:
                get:
                  operationId: getItems
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context))
            .descriptionsEqualTo("No CORS information in description")
    }
}
