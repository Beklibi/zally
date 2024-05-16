package org.zalando.zally.ruleset.roedl

import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.zalando.zally.core.DefaultContextFactory
import org.zalando.zally.test.ZallyAssertions

class VersioningRuleTest {

    private val rule = VersioningRule()

    @Test
    fun `validate that operations with version parameter and appropriate error response pass`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths:
              /items:
                get:
                  operationId: listItems
                  parameters:
                    - name: version
                      in: query
                      required: true
                  responses:
                    '400':
                      description: Bad Request - Missing or invalid version parameter
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context)).isEmpty()
    }

    @Test
    fun `validate that operations without version parameter are flagged`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths:
              /items:
                get:
                  operationId: listItems
                  responses:
                    '400':
                      description: Bad Request - Missing or invalid version parameter
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context))
            .descriptionsEqualTo("Version parameter is missing in operation listItems at /items")
    }

    @Test
    fun `validate that missing 400 error response is flagged`() {
        @Language("YAML")
        val context = DefaultContextFactory().getOpenApiContext(
            """
            openapi: 3.0.1
            paths:
              /items:
                get:
                  operationId: listItems
                  parameters:
                    - name: version
                      in: query
                      required: true
                  responses: {}
            """.trimIndent()
        )

        ZallyAssertions.assertThat(rule.validate(context))
            .descriptionsEqualTo("Missing 400 Bad Request response for missing or invalid version parameter in operation listItems")
    }
}
