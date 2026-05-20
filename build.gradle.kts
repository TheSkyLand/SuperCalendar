// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Вместо id("...") с версиями используем правильное подключение через каталог libs
    //alias(plugins.com.google.devtools.ksp) apply false
}
