plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta10"
}

group = "org.faststats"
version = "0.1.0"

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

repositories {
    mavenCentral()
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("com.google.guava:guava:33.4.0-jre")
    implementation("io.javalin:javalin:6.5.0")
    implementation("net.thenextlvl.core:files:2.0.1")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.faststats.FastStats"
    }
}