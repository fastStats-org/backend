plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta12"
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
    implementation("com.google.code.gson:gson:2.13.0")
    implementation("com.google.guava:guava:33.4.7-jre")
    implementation("io.javalin:javalin:6.6.0")
    implementation("net.thenextlvl.core:files:2.0.2")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.faststats.FastStats"
    }
}