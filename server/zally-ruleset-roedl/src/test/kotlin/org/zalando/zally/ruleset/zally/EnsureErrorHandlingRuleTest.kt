package org.zalando.zally.ruleset.roedl

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.zalando.zally.core.DefaultContextFactory
import org.zalando.zally.test.ZallyAssertions

class EnsureErrorHandlingRuleTest {

    private val rule = EnsureErrorHandlingRule()

    @Test
    fun `validate that error responses with all required headers pass`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths:
              /items:
                get:
                  operationId: getItems
                  responses:
                    '400':
                      description: Bad Request
                      headers:
                        X-Flow-ID: {}
                        X-Tenant-ID: {}
                        X-Sales-Channel: {}
                        X-Frontend-Type: {}
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context)).isEmpty()
    }

    @Test
    fun `validate that error responses missing any required headers are flagged`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths:
              /items:
                get:
                  operationId: getItems
                  responses:
                    '400':
                      description: Bad Request
                      headers:
                        X-Flow-ID: {}
                        X-Tenant-ID: {}
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context))
            .descriptionsEqualTo("Missing error details in the response for 400 at /items operation getItems: X-Sales-Channel, X-Frontend-Type")
    }

    @Test
    fun `validate should handle null paths`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths: null
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context)).isEmpty()
    }

    @Test
    fun `validate should handle null pathItem`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths:
              /items: null
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context)).isEmpty()
    }
}
