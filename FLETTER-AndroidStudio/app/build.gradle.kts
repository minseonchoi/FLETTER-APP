plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.choiminseon.fletterapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.choiminseon.fletterapp"
        minSdk = 21
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
    buildFeatures {
        mlModelBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // 레트로핏 라이브러리
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 글라이드 라이브러리
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    // 사진 업로드 라이브러리
    implementation("commons-io:commons-io:2.4")

    // 탭바 라이브러리
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")

    // viewPager2 하단에 이미지를 넘길 수 있음을 알려주는 DotsIndicator 라이브러리
    implementation ("com.tbuonomo:dotsindicator:4.3")

    // 애니메이션 lottie 라이브러리
    implementation ("com.airbnb.android:lottie:5.2.0")

    // circleImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")
    
}