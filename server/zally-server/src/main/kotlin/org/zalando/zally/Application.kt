package org.zalando.zally

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.scheduling.annotation.EnableScheduling
import org.zalando.twintip.spring.SchemaResource
import org.zalando.zally.core.JsonRulesValidator
import org.zalando.zally.core.RulesManager

@SpringBootApplication
@Import(SchemaResource::class)
@EnableScheduling
class Application {

    @Bean
    fun jsonRulesValidator(rulesManager: RulesManager): JsonRulesValidator {
        return JsonRulesValidator(rulesManager)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
