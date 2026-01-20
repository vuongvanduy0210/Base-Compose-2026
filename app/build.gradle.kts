import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.google.services)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

android {
    namespace = "com.duyvv.basecompose"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.duyvv.basecompose"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = "key_release"
            keyPassword = "Vinh0340"
            storeFile = file("key/key_release.jks")
            storePassword = "Vinh0340"
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }

    applicationVariants.all {
        val outputFileName = "Base compose 2026" +
                "_${SimpleDateFormat("dd-MM", Locale.US).format(Date())}" +
                "_v${defaultConfig.versionName}.${defaultConfig.versionCode}_${buildType.name}" +
                ".apk"
        outputs.all {
            val output = this as? BaseVariantOutputImpl
            output?.outputFileName = outputFileName
        }
    }

    @Suppress("UnstableApiUsage")
    bundle {
        language {
            enableSplit = false
        }
        density {
            enableSplit = true
        }
        abi {
            enableSplit = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    //compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.constraintlayout.compose)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation(libs.coil.compose)

    // koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.androidx.compose)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.config)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    //data store
    implementation(libs.data.store)

    //navigation 3
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    // room
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ads
    implementation(libs.play.services.ads)
    implementation(libs.panda.ads)
    implementation(libs.playServicesAdsIdentifier)

    implementation("com.appsflyer:af-android-sdk:6.17.3") {
        exclude(group = "com.google.android.play", module = "integrity")
    }
    implementation (libs.installreferrer)

    implementation(libs.ssp.android)
    implementation(libs.sdp.android)

    implementation(libs.kotlinx.immutable)
}

secrets {
    propertiesFileName = "secret.properties"
}