plugins {
  `java-library`
  `maven-publish`
}

version = "0.3.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {

}

java {
  withSourcesJar()
  withJavadocJar()
}

tasks {
  javadoc {
    isFailOnError = false
  }
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
      artifactId = "delphi"
    }
  }

  repositories {
    maven {
      var envVal = System.getenv("ARCADIUSMC_LOCAL_REPO")

      if (envVal == null) {
        url = uri(layout.buildDirectory.dir("maven-repo"))
      } else {
        url = uri(envVal)
      }
    }
  }
}