package com.server.common.config

import com.server.common.properties.AwsProperties
import com.server.common.security.SecurityFilter
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.jwk.source.RemoteJWKSet
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jose.util.DefaultResourceRetriever
import com.nimbusds.jose.util.ResourceRetriever
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.MalformedURLException
import java.net.URL

@Configuration
//@Import(AWSConfig::class)
@ConditionalOnClass(SecurityFilter::class)
class CognitoJwtAutoConfiguration {

    @Autowired
    private lateinit var awsProperties: AwsProperties
    private val connectionTimeout = 2000
    private val readTimeout = 2000

    /**
     * Method that exposes the cognito configuration.
     * @return ConfigurableJWTProcessor
     * @throws MalformedURLException
     */
    @Bean
    @Throws(MalformedURLException::class)
    fun configurableJWTProcessor(): ConfigurableJWTProcessor<SecurityContext>? {
        val resourceRetriever: ResourceRetriever = DefaultResourceRetriever(connectionTimeout, readTimeout)
        //https://cognito-idp.{region}.amazonaws.com/{userPoolId}/.well-known/jwks.json.
        val jwkSetURL = URL(awsProperties.amplify.wellKnownUrlEndpoint)
        //Creates the JSON Web Key (JWK)
        val keySource: JWKSource<SecurityContext> = RemoteJWKSet(jwkSetURL, resourceRetriever)
        val jwtProcessor: ConfigurableJWTProcessor<SecurityContext> = DefaultJWTProcessor()
        val keySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, keySource)
        jwtProcessor.jwsKeySelector = keySelector
        return jwtProcessor
    }
}
