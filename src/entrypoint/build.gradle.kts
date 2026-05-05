plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    android {
        namespace = "io.github.numq.flip.entrypoint"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        }
    }

    jvm()

    iosX64()

    iosArm64()

    iosSimulatorArm64()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class) wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            api(projects.common.presentation)
            implementation(projects.feature.navigation.core)
            implementation(projects.feature.navigation.presentation)
            implementation(projects.feature.generator.core)
            implementation(projects.feature.generator.presentation)
            implementation(projects.service.seed)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}