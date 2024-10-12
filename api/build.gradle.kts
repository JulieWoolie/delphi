plugins {
  `java-library`
  `maven-publish`
}

version = "1.0.1-SNAPSHOT"

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
      // Define the local directory where you want to publish the artifact
      url = uri(layout.buildDirectory.dir("maven-repo"))
    }
  }
}