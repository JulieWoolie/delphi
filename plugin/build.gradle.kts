import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
  java
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  kotlin("jvm") version "2.0.0"
}

val minecraftVersion = "1.21.4"
val pluginBaseName = "delphi-papermc"

version = "$minecraftVersion-0.2.0"

paperweight.reobfArtifactConfiguration.set(ReobfArtifactConfiguration.MOJANG_PRODUCTION)

repositories {
  mavenCentral()
  maven("https://libraries.minecraft.net/")
}

dependencies {
  implementation(project(":dom"))
  implementation(project(":chimera"))
  implementation(project(":api"))
  implementation(project(":render"))

  paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
}

java {

}

tasks {
  jar {
    archiveBaseName.set(pluginBaseName)
  }
  shadowJar {
    archiveBaseName.set(pluginBaseName)
  }

  processResources {
    filesMatching("versions.yml") {
      expand(mapOf(
        "chimeraVersion" to project(":chimera").version,
        "apiVersion" to project(":api").version,
        "domVersion" to project(":dom").version
      ))
    }

    filesMatching("paper-plugin.yml") {
      expand(mapOf("version" to version))
    }
  }
}