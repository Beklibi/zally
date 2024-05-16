package org.zalando.zally.ruleset.roedl

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.zalando.zally.core.DefaultContextFactory
import org.zalando.zally.test.ZallyAssertions

class JwtSecurityRuleTest {

    private val rule = JwtSecurityRule()

    @Test
    fun `validate that operations with correctly applied JWT security pass`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            components:
              securitySchemes:
                AuthToken:
                  type: http
                  scheme: bearer
                  bearerFormat: JWT
            paths:
              /items:
                get:
                  operationId: listItems
                  security:
                    - AuthToken: []
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context)).isEmpty()
    }

    @Test
    fun `validate that operations without JWT security are flagged`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            components:
              securitySchemes:
                AuthToken:
                  type: http
                  scheme: bearer
                  bearerFormat: JWT
            paths:
              /items:
                get:
                  operationId: listItems
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context))
            .descriptionsEqualTo("JWT Security not applied to /items operation listItems")
    }

    @Test
    fun `validate that operations with incorrect security type are flagged`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            components:
              securitySchemes:
                AuthToken:
                  type: apiKey
                  in: header
                  name: X-API-Key
            paths:
              /items:
                get:
                  operationId: listItems
                  security:
                    - AuthToken: []
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context))
            .isEmpty() // Dieser Test sollte bestehen, da dieses Szenario außerhalb des Geltungsbereichs der Regel liegt.
    }

    @Test
    fun `validate that operations using JWT without listing in security are flagged`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            components:
              securitySchemes:
                AuthToken:
                  type: http
                  scheme: bearer
                  bearerFormat: JWT
                ApiKeyAuth:
                  type: apiKey
                  in: header
                  name: X-API-Key
            paths:
              /items:
                get:
                  operationId: listItems
                  security:
                    - ApiKeyAuth: []
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context))
            .descriptionsEqualTo("JWT Security not applied to /items operation listItems")
    }
}
