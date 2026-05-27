plugins {
    `java-library`
    `maven-publish`
}

val javaVersion: String by project
val forgeLegacyVersion: String by project

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

base {
    archivesName.set("mixinkt-forge-legacy")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven {
        name = "MinecraftForge"
        url = uri("https://maven.minecraftforge.net/")
    }
}

dependencies {
    compileOnly(project(":common"))
    compileOnly("net.minecraftforge:forge:$forgeLegacyVersion:universal")
}

tasks.processResources {
    val expandProps = mapOf(
        "version" to project.version,
        "mod_id" to providers.gradleProperty("modId").get(),
        "mod_name" to providers.gradleProperty("modName").get(),
        "mod_author" to providers.gradleProperty("modAuthor").get(),
        "license" to providers.gradleProperty("license").get(),
        "description" to (project.description ?: ""),
        "credits" to providers.gradleProperty("credits").get(),
    )

    filesMatching(listOf("mcmod.info")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

tasks.jar {
    from(project(":common").tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
        rename { "META-INF/jars/mixinkt-common-${rootProject.version}.jar" }
    }
}
