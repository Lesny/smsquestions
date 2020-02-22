import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val verSonar = "2.7.1"
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:$verSonar")
    }
}

plugins {
    kotlin("jvm").version("1.3.41")
    idea
    java
    application
    id("com.github.johnrengelman.shadow").version("5.1.0")
    id("com.github.ben-manes.versions").version("0.21.0")
}

apply {
    plugin("kotlin")
    plugin("org.sonarqube")
    plugin("maven")
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "pl.banas"
version = "0.1"

application {
    mainClassName = "pl.banas.sms.questions.Main"
    applicationDefaultJvmArgs = listOf(
            "-Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.Log4j2LogDelegateFactory",
            "-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"
    )
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    compile("io.vertx:vertx-core:3.6.3")
    compile("io.vertx:vertx-web:3.6.3")
    compile("io.vertx:vertx-lang-kotlin-coroutines:3.6.3")
    compile("io.vertx:vertx-lang-kotlin:3.6.3")
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.41")
    compile("org.jetbrains.kotlin:kotlin-stdlib:1.3.41")
    compile("org.jetbrains.kotlin:kotlin-reflect:1.3.41")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.9.8")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-properties:2.9.8")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")

    compile("com.rethinkdb:rethinkdb-driver:2.3.3")
    compile("org.asynchttpclient:async-http-client:2.10.4")

    testCompile("io.vertx:vertx-unit:3.6.3")
    testCompile("io.vertx:vertx-codegen:3.6.3")
    testCompile("org.junit.jupiter:junit-jupiter-api:5.4.1")
    testCompile("org.mockito:mockito-core:2.25.1")
    testCompile("com.nhaarman:mockito-kotlin:1.6.0")
    testCompile("com.github.tomakehurst:wiremock:2.22.0")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.1")
}

tasks.withType<Test> {
    if (System.getProperty("skipTests") == null || System.getProperty("skipTests") == "false") {
        useJUnitPlatform {
            includeEngines = setOf("junit-jupiter")
            excludeTags("integration")
        }

        filter {
            includeTestsMatching("*Test")
        }
    }
}

tasks.withType<KotlinCompile> {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()

    kotlinOptions {
        jvmTarget = "1.8"
        apiVersion = "1.3"
        languageVersion = "1.3"
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
