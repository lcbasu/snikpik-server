import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id ("com.gorylenko.gradle-git-properties") version "2.2.2"
	kotlin("jvm") version "1.4.21"
	kotlin("plugin.spring") version "1.4.21"
}

group = "com.dukaankhata.server"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {

	developmentOnly("org.springframework.boot:spring-boot-devtools:2.3.8.RELEASE")

	implementation("org.zalando:logbook-spring-boot-starter:2.6.1")
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
//	implementation("com.twilio.sdk:twilio:7.47.2")
//	implementation("org.apache.tika:tika-core:1.4")
//	implementation("commons-io:commons-io:2.7")
	implementation("org.hibernate:hibernate-envers:5.4.27.Final")
//	implementation("com.github.javafaker:javafaker:1.0.2")
	implementation("com.vladmihalcea:hibernate-types-52:2.9.11")
	implementation("com.google.firebase:firebase-admin:7.1.0")
	implementation("org.springframework.security:spring-security-core:5.3.6.RELEASE")
	implementation("org.springframework.security:spring-security-web:5.3.6.RELEASE")
	implementation("org.springframework.security:spring-security-config:5.3.6.RELEASE")
	implementation("org.flywaydb:flyway-core:7.3.1")
	implementation("org.quartz-scheduler:quartz:2.3.2")
	implementation("org.springframework:spring-context-support:5.2.12.RELEASE")
	implementation("org.springframework:spring-tx:5.2.12.RELEASE")
//	implementation("org.springframework.boot:spring-boot-starter-mail:2.3.8.RELEASE")
//	implementation("io.sentry:sentry-spring:1.7.30")
//	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.11.0")
	implementation("org.hibernate.validator:hibernate-validator:6.1.5.Final")
	implementation("joda-time:joda-time:2.10.9")
//	implementation("com.amazonaws:aws-java-sdk-ses:1.11.842")
//	implementation("org.springframework.cloud:spring-cloud-starter-aws:2.2.1.RELEASE")
//	implementation("org.springframework.boot:spring-boot-starter-thymeleaf:2.3.8.RELEASE")
//	implementation("com.razorpay:razorpay-java:1.3.8")

	runtimeOnly("org.flywaydb:flyway-gradle-plugin:7.3.1")
	runtimeOnly("mysql:mysql-connector-java:8.0.21")

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
