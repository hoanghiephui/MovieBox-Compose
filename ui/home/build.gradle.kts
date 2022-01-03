import Version.composeCompiler

plugins {
    id(Plugin.androidLibrary)
    kotlin(Plugin.android)
}

android {
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeCompiler
    }
}

dependencies {
    implementation(project(ModuleDependency.Core.ui))

    implementation(project(ModuleDependency.navigation))

    implementation(project(ModuleDependency.Feature.AppPreferences.api))

    implementation(Dependency.AndroidX.navigationCompose)

    implementation(Dependency.Accompanist.navigationMaterial)
    implementation(Dependency.Accompanist.navigationAnimation)
    implementation(Dependency.Accompanist.insets)

    implementation(Dependency.Koin.compose)
    implementation(Dependency.Other.mviOrbit)

    implementation(Dependency.Loggers.prettyLogger)
}
