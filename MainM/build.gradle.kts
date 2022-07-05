import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    kotlin("jvm") version "1.7.0"
}

group = "io.github.alexiscomete.lapinoudsecond"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javacord:javacord:3.5.0")
    implementation("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.json:json:20220320")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.7.0-RC")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.7.0-RC")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}