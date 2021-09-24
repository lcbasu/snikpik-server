package com.dukaankhata.server.config

import io.sentry.Sentry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.zalando.logbook.Conditions.exclude
import org.zalando.logbook.Conditions.requestTo
import org.zalando.logbook.DefaultHttpLogWriter
import org.zalando.logbook.DefaultSink
import org.zalando.logbook.Logbook
import org.zalando.logbook.json.JsonHttpLogFormatter

@Configuration
class LogbookConfig {

    @Bean
    fun logbookInit(): Logbook? {
        try {
            return Logbook.builder()
                .condition(
                    exclude(
                        requestTo("/actuator/health"),
                        requestTo("/health"),
//                        requestTo("/seoData/**"),
//                        contentType("application/octet-stream"),
//                        header("X-Secret", newHashSet("1", "true")::contains)
                    )
                )
                .sink(
                    DefaultSink(
                        JsonHttpLogFormatter(),
                        DefaultHttpLogWriter()
                    ))
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            Sentry.captureException(e)
            return null
        }
    }
}
