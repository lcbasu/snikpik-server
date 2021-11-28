import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id ("com.gorylenko.gradle-git-properties") version "2.2.2"
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.21"
}

group = "com.server.dk"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

//dependencyManagement {
//	imports {
//		mavenBom(group: "org.springframework.data", name: 'spring-data-releasetrain', version: 'Neumann-SR9', ext: 'pom')
//	}
//}

repositories {
	mavenCentral()
}

dependencies {


//	implementation(group = "org.springframework.data", name = "spring-data-releasetrain", version = "Neumann-SR9", ext = "pom")

	developmentOnly("org.springframework.boot:spring-boot-devtools:2.3.8.RELEASE")

	implementation("io.micrometer:micrometer-registry-datadog:1.8.0")
	implementation("org.springframework.cloud:spring-cloud-starter-aws-messaging:2.2.6.RELEASE")
	implementation("com.algolia:algoliasearch-core:3.10.0")
	implementation("com.algolia:algoliasearch-apache:3.10.0")
	implementation("com.cloudinary:cloudinary-http44:1.29.0")
	implementation("software.aws.mcs:aws-sigv4-auth-cassandra-java-driver-plugin:4.0.4")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	implementation("com.nimbusds:nimbus-jose-jwt:9.9.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.0")
	implementation("org.xhtmlrenderer:flying-saucer-pdf:9.1.20")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.2.6.RELEASE")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.2")
	implementation("org.zalando:logbook-spring-boot-starter:2.6.1")
	implementation("org.zalando:logbook-json:2.6.1")
	implementation("io.sentry:sentry-spring-boot-starter:4.3.0")
	implementation("io.sentry:sentry-logback:4.3.0")
	implementation("javax.xml.bind:jaxb-api:2.3.0")
	implementation("org.springframework.boot:spring-boot-starter-mustache:2.3.8.RELEASE")
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.21")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.21")
	implementation("org.springframework.boot:spring-boot-starter-web:2.3.8.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-actuator:2.3.8.RELEASE")
	implementation("org.slf4j:slf4j-api:1.7.21")
	implementation("ch.qos.logback:logback-classic:1.2.3")
	implementation("ch.qos.logback:logback-core:1.2.3")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.3.8.RELEASE")
	implementation("com.twilio.sdk:twilio:8.10.0")
	implementation("org.hibernate:hibernate-envers:5.4.27.Final")
	implementation("com.vladmihalcea:hibernate-types-52:2.9.11")
	implementation("com.google.firebase:firebase-admin:7.1.0")
	implementation("org.springframework.security:spring-security-core:5.3.6.RELEASE")
	implementation("org.springframework.security:spring-security-web:5.3.6.RELEASE")
	implementation("org.springframework.security:spring-security-config:5.3.6.RELEASE")
	implementation("org.flywaydb:flyway-core:7.3.1")
	implementation("org.quartz-scheduler:quartz:2.3.2")
	implementation("org.springframework:spring-context-support:5.2.12.RELEASE")
	implementation("org.springframework:spring-tx:5.2.12.RELEASE")
	implementation("org.hibernate.validator:hibernate-validator:6.1.5.Final")
	implementation("joda-time:joda-time:2.10.9")
	implementation("com.github.ben-manes.caffeine:caffeine:2.5.5")
	implementation("software.amazon.awssdk:rekognition:2.17.87")
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.99")
	implementation("com.amazonaws:aws-java-sdk-core:1.12.99")
	implementation("com.github.javafaker:javafaker:0.15")
	implementation("org.springframework.data:spring-data-elasticsearch:4.0.0.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-data-cassandra:2.3.8.RELEASE")
	implementation("org.springframework.boot:spring-boot-starter-data-rest:2.3.8.RELEASE")
	implementation("com.datastax.oss:java-driver-core:4.13.0") {
		exclude(group= "org.apache.tinkerpop", module= "*")
	}
	implementation("com.datastax.oss:native-protocol:1.5.0")

	runtimeOnly("org.flywaydb:flyway-gradle-plugin:7.3.1")
	runtimeOnly("mysql:mysql-connector-java:8.0.25")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.3.8.RELEASE")

	testImplementation("org.springframework.boot:spring-boot-starter-test:2.3.8.RELEASE")
	testImplementation("org.springframework.boot:spring-boot-starter-test:2.3.8.RELEASE") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
