// Archivo de build raiz. Aqui solo se declaran los plugins que usaran los modulos,
// con `apply false` para que cada modulo decida si los aplica.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
