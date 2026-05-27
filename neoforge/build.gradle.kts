plugins {
    `java-library`
    `maven-publish`
}

val javaVersion: String by project

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

base {
    archivesName.set("mixinkt-neoforge")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    withSourcesJar()
}

repositories {
    mavenCentral()
}

sourceSets {
    // NeoForge's @Mod annotation lives in fancymodloader, which pulls Mojang's
    // logging artifact transitively. In-tree stub keeps this subproject free
    // of Minecraft-related transitive deps; NeoForge supplies the real
    // annotation at runtime.
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

    filesMatching(listOf("META-INF/neoforge.mods.toml")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

tasks.jar {
    from(project(":common").tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
        rename { "META-INF/jars/mixinkt-common-${rootProject.version}.jar" }
    }
}
