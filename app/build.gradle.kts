plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.cinecompare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cinecompare"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        manifestPlaceholders["MAPS_API_KEY"] = "YOUR_MAPS_API_KEY"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    implementation(project(":core:core-ui"))
    implementation(project(":core:core-network"))
    implementation(project(":core:core-database"))
    implementation(project(":core:core-common"))
    implementation(project(":aggregator:aggregator-base"))
    implementation(project(":aggregator:aggregator-bookmyshow"))
    implementation(project(":aggregator:aggregator-district"))
    implementation(project(":aggregator:aggregator-ticketnew"))
    implementation(project(":feature:feature-home"))
    implementation(project(":feature:feature-movie-detail"))
    implementation(project(":feature:feature-compare"))
    implementation(project(":feature:feature-search"))
    implementation(project(":feature:feature-profile"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons.extended)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    implementation(libs.workmanager.ktx)
    implementation(libs.splashscreen)
    implementation(libs.timber)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.work.compiler)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.2")
    debugImplementation(libs.compose.ui.tooling)
}
