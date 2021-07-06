import com.android.build.api.dsl.BaseFlavor
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.android.build.gradle.internal.dsl.DefaultConfig

plugins {
    id(Plugins.androidApplication)
    kotlin(Plugins.android)
    id(Plugins.kapt)
}

android {
    compileSdk = AndroidConfig.compileSdk

    defaultConfig {
        applicationId = AndroidConfig.applicationId

        minSdk = AndroidConfig.minSdk
        targetSdk = AndroidConfig.targetSdk
        buildToolsVersion = AndroidConfig.buildTools

        versionCode = AndroidConfig.versionCode
        versionName = AndroidConfig.versionName

        buildConfigFieldFromGradleProperty("keyTmdb")

        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true
    }

    buildTypes {
        getByName(BuildType.RELEASE) {
            isMinifyEnabled = BuildTypeRelease.isMinifyEnabled
            isDebuggable = BuildTypeRelease.isDebuggable
            proguardFiles("proguard-android.txt", "proguard-rules.pro")
        }

        getByName(BuildType.DEBUG) {
            isMinifyEnabled = BuildTypeDebug.isMinifyEnabled
            isDebuggable = BuildTypeDebug.isDebuggable
        }
    }

    buildFeatures {
        compose = true

        // Disable unused AGP features
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.version
    }
}

dependencies {
    implementation(project(ModuleDependency.coreUi))
    implementation(project(ModuleDependency.coreBase))

    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Coroutines.android)

    implementation(Libs.Koin.android)

    implementation(Libs.Others.klock)

    implementation(Libs.Loggers.timber)
    implementation(Libs.Loggers.prettyLogger)

    androidTestImplementation(Libs.junit)
    androidTestImplementation(Libs.Test.core)
    androidTestImplementation(Libs.Test.espressoCore)
    androidTestImplementation(Libs.Test.rules)
    androidTestImplementation(Libs.Test.Ext.junit)
    androidTestImplementation(Libs.AndroidX.Compose.uiTest)

    // androidx.test is forcing JUnit, 4.12. This forces it to use 4.13
    configurations.configureEach {
        resolutionStrategy {
            force(Libs.junit)
        }
    }
}

fun BaseFlavor.buildConfigFieldFromGradleProperty(gradlePropertyName: String) {
    val propertyValue = gradleLocalProperties(rootDir)[gradlePropertyName] as? String
    checkNotNull(propertyValue) { "Gradle property $gradlePropertyName is null" }

    val androidResourceName = "GRADLE_${gradlePropertyName.toSnakeCase()}".toUpperCase()
    buildConfigField("String", androidResourceName, propertyValue)
}

fun String.toSnakeCase() = this.split(Regex("(?=[A-Z])")).joinToString("_") { it.toLowerCase() }

fun DefaultConfig.buildConfigField(name: String, value: Set<String>) {
    // Generates String that holds Java String Array code
    val strValue = value.joinToString(
        prefix = "{",
        separator = ",",
        postfix = "}",
        transform = {
            "\"$it\""
        }
    )
    buildConfigField("String[]", name, strValue)
}
