import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    java
    id("com.diffplug.spotless") version "6.13.0" apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-source", "1.8", "-target", "1.8"))
    }

    configure<SpotlessExtension> {
        repositories {
            mavenCentral()
            mavenLocal()
        }
        java { target("src/**/*.java")
            googleJavaFormat("1.7").aosp()
            trimTrailingWhitespace()
            endWithNewline()
        }
        format("misc") {
            target("**/*.md", "**/*.gradle.kts", "**/*.gradle")
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}