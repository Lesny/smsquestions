import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val verKotlin = "1.3.41"
val verVertx = "3.6.3"

val verJunitJupiter = "5.4.1"
val verJunit_4 = "4.12"
val verJunitVintage = "5.4.1"

val verMockito = "2.25.1"
val verMockitoKotlin = "1.6.0"
val verWiremock = "2.22.0"
val verJackson = "2.9.8"

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
    compile("io.vertx:vertx-core:$verVertx")
    compile("io.vertx:vertx-web:$verVertx")

    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$verKotlin")
    compile("org.jetbrains.kotlin:kotlin-stdlib:$verKotlin")
    compile("org.jetbrains.kotlin:kotlin-reflect:$verKotlin")

    compile("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$verJackson")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$verJackson")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-properties:$verJackson")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:$verJackson")

    testCompile("io.vertx:vertx-unit:$verVertx")
    testCompile("io.vertx:vertx-codegen:$verVertx")
    testCompile("org.junit.jupiter:junit-jupiter-api:$verJunitJupiter")
    testCompile("junit:junit:$verJunit_4")
    testCompile("org.mockito:mockito-core:$verMockito")
    testCompile("com.nhaarman:mockito-kotlin:$verMockitoKotlin")
    testCompile("com.github.tomakehurst:wiremock:$verWiremock")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:$verJunitJupiter")
    testRuntime("org.junit.vintage:junit-vintage-engine:$verJunitVintage")
}

tasks.withType<Test> {
    if (System.getProperty("skipTests") == null || System.getProperty("skipTests") == "false") {
        useJUnitPlatform {
            includeEngines = setOf("junit-jupiter")
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
