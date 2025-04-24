rootProject.name = "tadeuBooter"

rootDir.walkTopDown()
    .filter { it.isDirectory }
    .filter { dir -> dir.listFiles()?.any { it.name == "build.gradle.kts" } == true }
    .map { it.toRelativeString(rootDir).replace(File.separatorChar, ':') }
    .forEach { include(it) }