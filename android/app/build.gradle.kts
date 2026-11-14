plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Deux jeux, deux applis, un seul projet :
//  - "patrouille" : Jeu Sofia/maquette.html
//  - "parc"       : Jeu Sofia/course-copains/parc.html
// À chaque compilation, le jeu est copié dans l'appli sous le nom index.html, avec les polices si elles existent.
fun registerCopy(name: String, source: String) = tasks.register<Copy>("copyGame${name.replaceFirstChar { it.uppercase() }}") {
    from(rootProject.file(source)) { rename { "index.html" } }
    from(rootProject.file("../fonts")) { into("fonts") }
    into(layout.buildDirectory.dir("generated/gameAssets/$name"))
}
val copyPatrouille = registerCopy("patrouille", "../maquette.html")
val copyParc = registerCopy("parc", "../course-copains/parc.html")
tasks.named("preBuild") { dependsOn(copyPatrouille, copyParc) }

android {
    namespace = "com.sofia.patrouillecourse"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
    }

    flavorDimensions += "jeu"
    productFlavors {
        create("patrouille") {
            dimension = "jeu"
            applicationId = "com.sofia.patrouillecourse"
            resValue("string", "app_name", "La Patrouille qui fait la course")
        }
        create("parc") {
            dimension = "jeu"
            applicationId = "com.sofia.sortieauparc"
            resValue("string", "app_name", "La sortie au parc")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Applis familiales installées à la main : on signe avec la clé de debug.
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
    sourceSets.getByName("patrouille").assets.srcDir(layout.buildDirectory.dir("generated/gameAssets/patrouille"))
    sourceSets.getByName("parc").assets.srcDir(layout.buildDirectory.dir("generated/gameAssets/parc"))
}
