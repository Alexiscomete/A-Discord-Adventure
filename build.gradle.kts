plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
    application
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroup("com.github.Alexiscomete")
        }
    }
}

dependencies {
    implementation("org.javacord:javacord:3.8.0")
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("org.json:json:20231013")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    implementation("com.github.Alexiscomete:procedural_generation:1-ALPHA.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

application {
    mainClass.set("io.github.alexiscomete.lapinousecond.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

group = "io.github.alexiscomete.lapinoudsecond"
version = "v1.0-beta.0.2.3"
description = "A-Discord-Adventure"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

tasks.jar {
    manifest {
        attributes["Main-Class"] = "io.github.alexiscomete.lapinousecond.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        println(file.name)
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Alexiscomete/A-Discord-Adventure")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            artifactId = "ada"
            artifact(tasks.distZip)
        }
    }
}
