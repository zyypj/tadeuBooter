plugins {
    java
    kotlin("jvm") version "2.1.20"
    id("com.diffplug.spotless")
}

group = "me.zyypj.booter.minecraft.spigot"
version = "1.9"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "spigotmc-repo"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
}

dependencies {
    compileOnly(fileTree("libs") {
        include("**/*.jar")
    })
    compileOnly("org.projectlombok:lombok:1.18.38")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")

    annotationProcessor("org.projectlombok:lombok:1.18.38")

    implementation("com.zaxxer:HikariCP:4.0.3")
    implementation("net.kyori:adventure-text-serializer-legacy:4.19.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
    implementation("org.reflections:reflections:0.10.2")

    implementation(project(":shared"))
}

val targetJavaVersion = JavaVersion.VERSION_1_8

kotlin {
    sourceSets["main"].kotlin.srcDirs("src/main/java", "src/main/kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("-source", "1.8", "-target", "1.8"))
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
