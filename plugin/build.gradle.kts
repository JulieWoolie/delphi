import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
  java
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
  id("com.github.johnrengelman.shadow") version "8.1.1"
}

val minecraftVersion = "1.21.7"
val pluginBaseName = "delphi-papermc"

version = "$minecraftVersion-0.4.1"

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
  implementation(project(":hephaestus"))

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
        "domVersion" to project(":dom").version,
        "jsVersion" to project(":hephaestus").version
      ))
    }

    filesMatching("paper-plugin.yml") {
      expand(mapOf("version" to version))
    }
  }
}