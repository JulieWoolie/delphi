import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
  java
  id("io.freefair.lombok") version "8.6"
  id("io.papermc.paperweight.userdev") version "1.7.1"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  kotlin("jvm") version "2.0.0"
}

val minecraftVersion = "1.21"
val pluginBaseName = "delphi-papermc"

version = "$minecraftVersion-1.0.0"

paperweight.reobfArtifactConfiguration.set(ReobfArtifactConfiguration.MOJANG_PRODUCTION)

repositories {
  mavenCentral()
  maven("https://libraries.minecraft.net/")
}

dependencies {
  implementation(project(":dom"))
  implementation(project(":chimera"))
  implementation(project(":api"))

  //implementation(kotlin("stdlib"))

  paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
  jar {
    archiveBaseName.set(pluginBaseName)
  }
  shadowJar {
    archiveBaseName.set(pluginBaseName)
  }
}