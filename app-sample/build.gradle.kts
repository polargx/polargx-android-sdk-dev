plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    alias(libs.plugins.google.services)
//    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
}

android {
    namespace = "com.linkattribution.sample"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.linkattribution.sample"
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
            applicationId = "com.linkattribution.sample.dev"
            buildConfigField("String", "API_URL", "\"test.amazonaws.com/dev\"")
            buildConfigField("String", "API_KEY", "\"bBTcxnZUNQ3qIeHO3pXMf90D0SYoRCKI71lgXiZn\"")
            buildConfigField("String", "BRANCH_APP_UNID", "\"25d6d1c0-d767-4794-b3cb-68696c3ff104\"")
            buildConfigField("String", "BRANCH_KEY", "\"7d85d434986e646b39e9fa57edf6db270d10a5b7\"")
        }
        create("staging") {
            dimension = "buildMode"
            applicationId = "com.linkattribution.sample.staging"
            buildConfigField("String", "API_URL", "\"test.amazonaws.com/staging\"")
            buildConfigField("String", "API_KEY", "\"bBTcxnZUNQ3qIeHO3pXMf90D0SYoRCKI71lgXiZn\"")
            buildConfigField("String", "BRANCH_APP_UNID", "\"25d6d1c0-d767-4794-b3cb-68696c3ff104\"")
            buildConfigField("String", "BRANCH_KEY", "\"7d85d434986e646b39e9fa57edf6db270d10a5b7\"")
        }
        create("prod") {
            dimension = "buildMode"
            applicationId = "com.linkattribution.sample.prod"
            buildConfigField("String", "API_URL", "\"test.amazonaws.com/prod\"")
            buildConfigField("String", "API_KEY", "\"bBTcxnZUNQ3qIeHO3pXMf90D0SYoRCKI71lgXiZn\"")
            buildConfigField("String", "BRANCH_APP_UNID", "\"25d6d1c0-d767-4794-b3cb-68696c3ff104\"")
            buildConfigField("String", "BRANCH_KEY", "\"7d85d434986e646b39e9fa57edf6db270d10a5b7\"")
        }
    }

    buildTypes {
        getByName("debug") {
            productFlavors.getByName("dev").signingConfig = signingConfigs.getByName("debug")
            productFlavors.getByName("staging").signingConfig = signingConfigs.getByName("debug")
            productFlavors.getByName("prod").signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            productFlavors.getByName("dev").signingConfig = signingConfigs.getByName("debug")
            productFlavors.getByName("staging").signingConfig = signingConfigs.getByName("debug")
            productFlavors.getByName("prod").signingConfig = signingConfigs.getByName("release")
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
        compose = true
    }
//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.5.1"
//    }
}

dependencies {

    // Multidex
    implementation(libs.multidex)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // View
    implementation(libs.constraintlayout)

    // Firebase
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.analytics.ktx)
//    implementation(libs.firebase.inappmessaging.display.ktx)
//    implementation(libs.firebase.messaging.ktx)
//    implementation(libs.firebase.crashlytics.ktx)

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
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.navigation)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    // Internet time
    implementation(libs.kronos.java)
    implementation(libs.kronos.android)

    implementation(project(":app-shared"))
    implementation(project(":library:core"))
    implementation(project(":library:network"))
    implementation(project(":library:link-attribution"))
    implementation(project(":data:users"))
    implementation(project(":data:shared"))
    implementation(project(":data:products"))
    implementation(project(":feature:shared"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:main"))
    implementation(project(":feature:settings"))

}