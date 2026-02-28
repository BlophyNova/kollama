val ktorVersion: String by project
val logbackVersion: String by project

plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jetbrains.dokka") version "2.1.0"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "net.blophy.nova"
version = "0.2.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:${ktorVersion}")
    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-client-logging:${ktorVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")
    testImplementation("io.ktor:ktor-client-mock:${ktorVersion}")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.test {
    useJUnitPlatform()
}

// 生成源码 jar
tasks.withType<Jar> {
    from(sourceSets.main.get().allSource)
}

dokka {
    dokkaPublications.html {
        moduleName.set(project.name)
        moduleVersion.set(project.version.toString())
        // Standard output directory for HTML documentation
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
        failOnWarning.set(false)
        suppressInheritedMembers.set(false)
        suppressObviousFunctions.set(true)
        offlineMode.set(false)
        includes.from("README.md")
    }
    pluginsConfiguration.html {
        footerMessage.set("© 2026 Copyright Lime Network")
    }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
