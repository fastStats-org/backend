plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta4"
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
    implementation("io.javalin:javalin:6.4.0")
    implementation("net.thenextlvl.core:files:2.0.0")
}

tasks.shadowJar {
    minimize()
}