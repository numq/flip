plugins {
    alias(libs.plugins.android.application)
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(projects.entrypoint)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

android {
    namespace = "io.github.numq.flip"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "io.github.numq.flip"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()

        val major = libs.versions.version.major.get().toInt()
        val minor = libs.versions.version.minor.get().toInt()
        val patch = libs.versions.version.patch.get().toInt()

        versionCode = major * 1_000_000 + minor * 1_000 + patch
        versionName = "$major.$minor.$patch"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}