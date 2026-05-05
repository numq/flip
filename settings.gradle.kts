rootProject.name = "flip"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("jvm") version "1.9.22"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

val rootModules = listOf("service", "common", "feature", "entrypoint", "platform", "test")

val commonModules = listOf("core", "presentation")

val featureModules = listOf("navigation", "generator")

val featureSubmodules = listOf("core", "presentation")

val platformModules = listOf("android", "desktop", "ios", "web")

val serviceModules = listOf("seed")

rootModules.forEach { moduleName ->
    include(":$moduleName")
    project(":$moduleName").projectDir = file("src/$moduleName")
}

commonModules.forEach { moduleName ->
    val path = ":common:$moduleName"
    include(path)
    project(path).projectDir = file("src/common/$moduleName")
}

featureModules.forEach { moduleName ->
    val featureParent = ":feature:$moduleName"
    include(featureParent)
    project(featureParent).projectDir = file("src/feature/$moduleName")

    featureSubmodules.forEach { submoduleName ->
        val submodule = "$featureParent:$submoduleName"
        include(submodule)
        project(submodule).projectDir = file("src/feature/$moduleName/$submoduleName")
    }
}

platformModules.forEach { moduleName ->
    val path = ":platform:$moduleName"
    include(path)
    project(path).projectDir = file("src/platform/$moduleName")
}

serviceModules.forEach { moduleName ->
    val path = ":service:$moduleName"
    include(path)
    project(path).projectDir = file("src/service/$moduleName")
}