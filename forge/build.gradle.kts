plugins {
    `java-library`
    `maven-publish`
}

val javaVersion: String by project

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

base {
    archivesName.set("mixinkt-forge")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    withSourcesJar()
}

repositories {
    mavenCentral()
}

sourceSets {
    // Modern Forge's @Mod annotation only ships via ForgeGradle's userdev
    // pipeline, which doesn't yet support Gradle 9.x. In-tree stub satisfies
    // the compiler; the real annotation is provided by Forge at runtime.
    create("stubs") {
        java.srcDir("src/stubs/java")
    }
}

dependencies {
    compileOnly(project(":common"))
    compileOnly(sourceSets["stubs"].output)
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

    filesMatching(listOf("META-INF/mods.toml")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

tasks.jar {
    from(project(":common").tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
        rename { "META-INF/jars/mixinkt-common-${rootProject.version}.jar" }
    }
}
