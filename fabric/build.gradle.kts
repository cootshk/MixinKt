plugins {
    `maven-publish`
    kotlin("jvm")
    id("net.fabricmc.fabric-loom") version "1.16-SNAPSHOT"
}

val modId: String by project
val modName: String by project
val modAuthor: String by project
val license: String by project
val credits: String by project
val javaVersion: String by project
val minecraftVersion: String by project
val loaderVersion: String by project
val fabricApiVersion: String by project
val flkVersion: String by project

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

base {
    archivesName.set("$modId-fabric")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion.toInt()))
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
}

sourceSets {
    named("test") {
        compileClasspath += sourceSets.main.get().compileClasspath + sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().runtimeClasspath + sourceSets.main.get().output
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$loaderVersion")
    implementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    implementation("net.fabricmc:fabric-language-kotlin:$flkVersion")

    // common is exposed transitively so the testmod sourceSet (and downstream
    // consumers) see the MixinKt API alongside the Fabric entrypoint.
    api(project(":common"))
    implementation(kotlin("stdlib-jdk8"))
}

loom {
    runs {
        register("testClient") {
            client()
            source(sourceSets["test"])
        }
        register("testServer") {
            server()
            source(sourceSets["test"])
        }
    }
    mods {
        register(modId) {
            sourceSet("main")
        }
        register("mixinkt_test") {
            sourceSet("test")
        }
    }
}

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
    "minecraft_version" to minecraftVersion,
    "loader_version" to loaderVersion,
)

tasks.processResources {
    filesMatching(listOf("fabric.mod.json")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

tasks.named<ProcessResources>("processTestResources") {
    filesMatching(listOf("fabric.mod.json", "*.mixins.json")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(javaVersion.toInt())
}

tasks.jar {
    from(project(":common").tasks.named<Jar>("jar").flatMap { it.archiveFile }) {
        rename { "META-INF/jars/mixinkt-common-${rootProject.version}.jar" }
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
