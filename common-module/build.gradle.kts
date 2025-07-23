plugins {
	java
	id("org.springframework.boot") version "3.4.5" apply false
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

//repositories {
//	mavenCentral()
//}

dependencies {
	implementation("org.springframework:spring-web")

	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.5"))
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation ("com.fasterxml.jackson.core:jackson-databind:2.17.0")
	compileOnly("org.projectlombok:lombok:1.18.30")
	annotationProcessor("org.projectlombok:lombok:1.18.30")

	// jwt
	implementation ("io.jsonwebtoken:jjwt-api:0.12.3")
	implementation ("io.jsonwebtoken:jjwt-impl:0.12.3")
	implementation ("io.jsonwebtoken:jjwt-jackson:0.12.3")

	compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")

	// redis
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	implementation ("org.redisson:redisson-spring-boot-starter:3.23.4")
	implementation ("org.springframework.boot:spring-boot-starter-aop")
	testImplementation ("org.testcontainers:testcontainers:1.19.4")
	testImplementation ("org.testcontainers:junit-jupiter:1.19.4")

	// S3 연동d
	implementation ("org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE")
	implementation ("com.amazonaws:aws-java-sdk-s3:1.12.529")
	testImplementation ("cloud.localstack:localstack-utils:0.2.20")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
	enabled = true
}

tasks.withType<JavaCompile> {
	options.compilerArgs.add("-parameters")
}