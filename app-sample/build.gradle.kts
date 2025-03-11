plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.polargx.sample"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.polargx.sample"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug-keystore.jks")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

        create("release") {
            storeFile = file("release-keystore.jks")
            storePassword = "android"
            keyAlias = "androidreleasekey"
            keyPassword = "android"
        }
    }

    flavorDimensions += listOf("buildMode")
    productFlavors {
        create("dev") {
            dimension = "buildMode"
            applicationId = "com.polargx.sample.dev"
            buildConfigField("String", "POLAR_APP_ID", "\"ad71f83f-4bc3-447a-94c8-d78c3ec8cce2\"")
            buildConfigField("String", "POLAR_API_KEY", "\"IF7M2WBS034QFvycReBBPavHfo5xXOkLOaOJmP9i\"")
        }
        create("staging") {
            dimension = "buildMode"
            applicationId = "com.polargx.sample.staging"
            buildConfigField("String", "POLAR_APP_ID", "\"ad71f83f-4bc3-447a-94c8-d78c3ec8cce2\"")
            buildConfigField("String", "POLAR_API_KEY", "\"IF7M2WBS034QFvycReBBPavHfo5xXOkLOaOJmP9i\"")
        }
        create("prod") {
            dimension = "buildMode"
            applicationId = "com.polargx.sample.prod"
            buildConfigField("String", "POLAR_APP_ID", "\"217ae0a5-fec5-40a5-947a-d0a8c6b27332\"")
            buildConfigField("String", "POLAR_API_KEY", "\"b7jAcEsxOk948NaMiioN43R3au2U4RmZZpHQsfDc\"")
        }
    }

    buildTypes {
        getByName("debug") {
//            productFlavors.getByName("dev").signingConfig = signingConfigs.getByName("debug")
//            productFlavors.getByName("staging").signingConfig = signingConfigs.getByName("debug")
//            productFlavors.getByName("prod").signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
//            productFlavors.getByName("dev").signingConfig = signingConfigs.getByName("debug")
//            productFlavors.getByName("staging").signingConfig = signingConfigs.getByName("debug")
//            productFlavors.getByName("prod").signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            enableAndroidTestCoverage = true
        }
    }
    // Required for Ktor
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    // Multidex
    implementation(libs.multidex)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // View
    implementation(libs.constraintlayout)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.auth)

    // Required for Ktor
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Internet time
    implementation(libs.kronos.java)
    implementation(libs.kronos.android)

    // Library
    implementation(project(":polargx"))
}