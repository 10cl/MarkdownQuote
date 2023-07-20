plugins {
  id("java")
  id("org.jetbrains.intellij") version "1.9.0"
}

group = "org.intellij.sdk"
version = "1.0.2"

repositories {
  mavenCentral()
}

dependencies {
//  implementation("org.eclipse.jgit:org.eclipse.jgit:6.6.0.202305301015-r")
//  implementation("org.eclipse.jgit:org.eclipse.jgit.archive:6.6.0.202305301015-r")
//  implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.jsch:6.6.0.202305301015-r")
//  implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.apache:6.5.0.202303070854-r")
//  implementation("commons-io:commons-io:2.13.0")
//  implementation("org.slf4j:slf4j-simple:1.7.36")
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
  version.set("2021.3.3")
}

tasks {
  buildSearchableOptions {
    enabled = false
  }

  patchPluginXml {
    version.set("${project.version}")
    sinceBuild.set("193.52")
    untilBuild.set("222.*")
  }
}
