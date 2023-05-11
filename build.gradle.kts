val coreVersion = fetchProperty("version.core", "invalid")
val baseVersion = fetchProperty("version.base", "invalid")
val betaString = fetchProperty("version.beta", "false")
val jenkinsBuildNumber = fetchEnv("BUILD_NUMBER", null, "Unofficial")

val betaBoolean = betaString.toBoolean()
val betaVersion = if (betaBoolean) "Beta-" else ""
version = "$baseVersion.$betaVersion$jenkinsBuildNumber"

fun fetchProperty(propertyName: String, defaultValue: String): String {
    val found = findProperty(propertyName)
    if (found != null) {
        return found.toString()
    }

    return defaultValue
}

fun fetchEnv(envName: String, propertyName: String?, defaultValue: String): String {
    val found = System.getenv(envName)
    if (found != null) {
        return found
    }

    if (propertyName != null) {
        return fetchProperty(propertyName, defaultValue)
    }

    return defaultValue
}

plugins {
    id("java")
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://nexus.sirblobman.xyz/public/")
    maven("https://nexus.sirblobman.xyz/proxy-jitpack/")
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("com.github.sirblobman.api:core:$coreVersion")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    processResources {
        val pluginName = fetchProperty("bukkit.plugin.name", "")
        val pluginPrefix = fetchProperty("bukkit.plugin.prefix", "")
        val pluginDescription = fetchProperty("bukkit.plugin.description", "")
        val pluginWebsite = fetchProperty("bukkit.plugin.website", "")
        val pluginMainClass = fetchProperty("bukkit.plugin.main", "")

        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "pluginName" to pluginName,
                    "pluginPrefix" to pluginPrefix,
                    "pluginDescription" to pluginDescription,
                    "pluginWebsite" to pluginWebsite,
                    "pluginMainClass" to pluginMainClass,
                    "pluginVersion" to version
                )
            )
        }
    }

    named<Jar>("jar") {
        archiveBaseName.set("BasicEconomy")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-Xlint:deprecation")
        options.compilerArgs.add("-Xlint:unchecked")
    }
}
