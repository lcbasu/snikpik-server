package com.server.common.config
import com.bugsnag.Bugsnag
import com.server.common.properties.BugsnagProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BugsnagConfig {

    @Autowired
    private lateinit var bugsnagProperties: BugsnagProperties

    @Bean
    fun bugsnag(): Bugsnag {
        return Bugsnag(bugsnagProperties.unboxServerApiKey)
    }
}
