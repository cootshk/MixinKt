plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm")
}

val modId: String by project
val modName: String by project
val modAuthor: String by project
val license: String by project
val credits: String by project
val javaVersion: String by project
val mixinVersion: String by project
val mixinextrasVersion: String by project
val asmVersion: String by project
val floggerVersion: String by project

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

base {
    archivesName.set("$modId-common")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven {
        name = "SpongePowered"
        url = uri("https://repo.spongepowered.org/repository/maven-public")
    }
}

dependencies {
    compileOnly("org.spongepowered:mixin:$mixinVersion")
    compileOnly("io.github.llamalad7:mixinextras-common:$mixinextrasVersion")
    compileOnly("org.ow2.asm:asm-tree:$asmVersion")
    annotationProcessor("io.github.llamalad7:mixinextras-common:$mixinextrasVersion")
    implementation(kotlin("stdlib-jdk8"))
    compileOnly("com.google.flogger:flogger:$floggerVersion")
}

tasks.processResources {
    val expandProps = mapOf(
        "version" to project.version,
        "group" to project.group,
        "mod_name" to modName,
        "mod_author" to modAuthor,
        "mod_id" to modId,
        "license" to license,
        "description" to (project.description ?: ""),
        "credits" to credits,
        "java_version" to javaVersion,
    )

    filesMatching(listOf("*.mixins.json")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

tasks.jar {
    manifest {
        attributes(
            "Specification-Title" to modName,
            "Specification-Vendor" to modAuthor,
            "Specification-Version" to archiveVersion.get(),
            "Implementation-Title" to project.name,
            "Implementation-Version" to archiveVersion.get(),
            "Implementation-Vendor" to modAuthor,
        )
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
    repositories {
        System.getenv("local_maven_url")?.let { localMaven ->
            maven { url = uri(localMaven) }
        }
    }
}
