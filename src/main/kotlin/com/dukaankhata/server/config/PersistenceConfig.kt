package com.dukaankhata.server.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing


@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorProvider")
class PersistenceConfig {
    @Bean
    fun auditorProvider(): AuditorAware<String> {
        val source = SpringSecurityAuditorAware()
        return source
    }
}
