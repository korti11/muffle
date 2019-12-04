package io.korti.muffle.entity

data class MufflePoint(val lat: Float, val lng: Float, val name: String, var enable: Boolean = true,
                       var active: Boolean = false) {

    fun getStatus(): String {
        return when {
            enable.not() -> {
                "Disabled"
            }
            active -> {
                "Active"
            }
            else -> {
                "Not active"
            }
        }
    }

}