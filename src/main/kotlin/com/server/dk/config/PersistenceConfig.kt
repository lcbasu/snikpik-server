package com.server.dk.config


import com.server.dk.utils.DateUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.LocalDateTime
import java.util.*


@Configuration
@EnableJpaAuditing(auditorAwareRef="auditorProvider", dateTimeProviderRef = "dateTimeProvider")
class PersistenceConfig {
    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return SpringSecurityAuditorAware()
    }

    @Bean
    fun dateTimeProvider() = DateTimeProvider {
        Optional.of(LocalDateTime.from(DateUtils.dateTimeNow()))
    }
}
