import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.diffplug.spotless") version "6.13.0" apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<SpotlessExtension> {
        repositories {
            mavenCentral()
            mavenLocal()
        }
        /*
        Não consegui arrumar o erro do apply no spotlessKotlinAppl - como estamos usando Java 8
        muita coisa é limitada ao usar.
         */
        // kotlin {
        //     target("**/*.kt")
        //     ktlint()
        //     trimTrailingWhitespace()
        //     endWithNewline()
        // }

        java {
            ignoreErrorForPath("C:/Users/isaac/Desktop/DevContributing/tadeuBooter/shared/build")
            target("**/*.java")
            googleJavaFormat("1.7").aosp()
            trimTrailingWhitespace()
            endWithNewline()
            removeUnusedImports()
        }
        format("misc") {
            target("**/*.md", "**/*.gradle.kts", "**/*.gradle")
            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}