plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Le jeu vit dans "Jeu Sofia/maquette.html" (une seule source).
// À chaque compilation, on le copie dans l'app sous le nom index.html, avec les polices si elles existent.
val gameAssets = layout.buildDirectory.dir("generated/gameAssets")
val copyGame by tasks.registering(Copy::class) {
    from(rootProject.file("../maquette.html")) { rename { "index.html" } }
    from(rootProject.file("../fonts")) { into("fonts") }
    into(gameAssets)
}
tasks.named("preBuild") { dependsOn(copyGame) }

android {
    namespace = "com.sofia.patrouillecourse"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sofia.patrouillecourse"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // App familiale installée à la main : on signe avec la clé de debug.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    sourceSets.getByName("main").assets.srcDir(gameAssets)
}
