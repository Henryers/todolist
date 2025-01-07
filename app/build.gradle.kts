plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.todolist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.todolist"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.core:core-ktx:1.14.0")
    implementation("androidx.core:core:1.14.0")
    // Retrofit 依赖
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson 转换器，用于将 JSON 转换为对象
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp3 核心库
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    // OkHttp3 日志拦截器（可选，用于调试请求和响应的详细日志）
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
}