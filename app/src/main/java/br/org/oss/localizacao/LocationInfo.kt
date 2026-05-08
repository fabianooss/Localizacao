package br.org.oss.localizacao

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val city: String?,
    val state: String?,
    val country: String?
)