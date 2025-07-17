
plugins {
    kotlin("jvm") version "2.1.21"
    id("org.openjfx.javafxplugin") version "0.1.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("org.openjfx:javafx-controls:23.0.1")
    implementation("org.openjfx:javafx-fxml:23.0.1")
    implementation("org.openjfx:javafx-graphics:23.0.1")
    implementation("org.openjfx:javafx-base:23.0.1")

    implementation("io.github.palexdev:materialfx:11.17.0")
    implementation("io.github.mkpaz:atlantafx-base:2.0.1")
    implementation("org.kordamp.ikonli:ikonli-javafx:12.4.0")
    implementation("org.kordamp.ikonli:ikonli-feather-pack:12.3.1")
    implementation("org.kordamp.ikonli:ikonli-material2-pack:12.4.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

javafx {
    version = "23.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.base")
}

application {
    mainClass = "com.recipe_manager.Main"
}

