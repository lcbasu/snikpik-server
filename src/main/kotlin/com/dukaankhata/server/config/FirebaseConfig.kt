package com.dukaankhata.server.config

import com.dukaankhata.server.properties.SecurityProperties
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class FirebaseConfig {

    @Autowired
    var securityProperties: SecurityProperties? = null

    @Primary
    @Bean
    fun firebaseInit() {
        try {
            val inputStream = javaClass.getResourceAsStream("/serviceAccountKey.json")
            val options = FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .setDatabaseUrl(securityProperties?.firebaseProps?.databaseUrl)
                    .setStorageBucket(securityProperties?.firebaseProps?.storageBucket)
                    .build()
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
