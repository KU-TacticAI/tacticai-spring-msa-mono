plugins {
	java
	id("org.springframework.boot") version "3.4.5"
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
	implementation(project(":common-module"))

	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("com.vladmihalcea:hibernate-types-60:2.21.1")

	// jwt
	implementation ("io.jsonwebtoken:jjwt-api:0.12.3")
	implementation ("io.jsonwebtoken:jjwt-impl:0.12.3")
	implementation ("io.jsonwebtoken:jjwt-jackson:0.12.3")

	// 레디스
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")
	implementation ("org.redisson:redisson-spring-boot-starter:3.23.4")
	implementation ("org.springframework.boot:spring-boot-starter-aop")
	testImplementation ("org.testcontainers:testcontainers:1.19.4")
	testImplementation ("org.testcontainers:junit-jupiter:1.19.4")
}

tasks.withType<Test> {
	useJUnitPlatform()
}